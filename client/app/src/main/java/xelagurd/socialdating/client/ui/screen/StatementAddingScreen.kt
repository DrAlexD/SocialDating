package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.form.StatementFormData
import xelagurd.socialdating.client.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementAddingUiState
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.StatementAddingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementAddingScreen(
    onSuccessStatementAdding: () -> Unit,
    onNavigateUp: () -> Unit,
    statementAddingViewModel: StatementAddingViewModel = hiltViewModel()
) {
    val statementAddingUiState by statementAddingViewModel.uiState.collectAsState()

    StatementAddingScreenComponent(
        statementAddingUiState = statementAddingUiState,
        onSuccessStatementAdding = onSuccessStatementAdding,
        onNavigateUp = onNavigateUp,
        onValueChange = statementAddingViewModel::updateUiState,
        onStatementAddingClick = statementAddingViewModel::addStatement
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementAddingScreenComponent(
    statementAddingUiState: StatementAddingUiState = StatementAddingUiState(),
    onSuccessStatementAdding: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onValueChange: (StatementFormData) -> Unit = {},
    onStatementAddingClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(StatementAddingDestination.titleRes),
                navigateUp = onNavigateUp
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = StatementAddingDestination.topLevelRoute
            )
        }
    ) { innerPadding ->
        ComponentWithActionRequestStatus(
            actionRequestStatus = statementAddingUiState.actionRequestStatus,
            onSuccess = onSuccessStatementAdding,
            contentPadding = innerPadding
        ) {
            StatementDetailsBody(
                statementAddingUiState = statementAddingUiState,
                onValueChange = onValueChange,
                onStatementAddingClick = onStatementAddingClick
            )
        }
    }
}

@Composable
private inline fun StatementDetailsBody(
    statementAddingUiState: StatementAddingUiState,
    crossinline onValueChange: (StatementFormData) -> Unit,
    noinline onStatementAddingClick: () -> Unit
) {
    val statementFormData = statementAddingUiState.formData
    val actionRequestStatus = statementAddingUiState.actionRequestStatus

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AppTextField(
            value = statementFormData.text,
            onValueChange = { onValueChange(statementFormData.copy(text = it)) },
            label = stringResource(R.string.statement_text),
            error = statementFormData.textError
        )
        AppMediumTitleText(text = stringResourceWithColon(R.string.defining_themes))
        AppMediumTitleText(text = stringResource(R.string.is_support_defining_theme))
        DataMultiChoosingListComponent(
            dataListUiState = statementAddingUiState,
            chosenEntityIds = statementFormData.definingThemes.keys,
            maxHeight = LocalConfiguration.current.screenHeightDp.dp / 3
        ) { entity, isChosen ->
            DefiningThemeChoosingCard(
                definingTheme = entity as DefiningTheme,
                isChosen = isChosen,
                isSupportDefiningTheme = statementFormData.definingThemes[entity.id],
                onDefiningThemeClick = { onValueChange(statementFormData.toggleDefiningTheme(entity.id)) },
                onOpinionClick = { onValueChange(statementFormData.updateDefiningThemeOpinion(entity.id, it)) }
            )
        }
        AppLargeTextCard(
            isEnabled = statementFormData.isValid && actionRequestStatus.isAllowedActionRefresh(),
            text = stringResource(R.string.add_statement),
            onClick = onStatementAddingClick
        )
    }
}

@Composable
private inline fun DefiningThemeChoosingCard(
    definingTheme: DefiningTheme,
    isChosen: Boolean,
    isSupportDefiningTheme: Boolean?,
    crossinline onDefiningThemeClick: () -> Unit,
    crossinline onOpinionClick: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AppMediumTextCard(
            text = definingTheme.name,
            onClick = { onDefiningThemeClick() },
            isHasBorder = isChosen
        )
        if (isChosen) {
            AppYesNoRadioGroup(
                isSelectedYes = isSupportDefiningTheme,
                onSelect = { onOpinionClick(it) },
                testTagSuffix = definingTheme.id.toString()
            )
        }
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentWithoutChosenDefiningThemesPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData.copy(definingThemes = mapOf())
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true, locale = "ru")
@Composable
private fun StatementAddingComponentWithoutChosenDefiningThemesRuPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData.copy(definingThemes = mapOf())
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentWithChosenDefiningThemesPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentWithoutChosenOpinionPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData.copy(
                    definingThemes = mapOf(FakeData.mainDefiningTheme.id to null)
                )
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentInvalidFormPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData.copy(text = "T")
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentActionErrorPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = FakeData.statementFormData,
                actionRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentDataErrorPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                dataRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}
