package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
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
        onStatementAddingClick = statementAddingViewModel::statementAdding
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AppTextField(
            value = statementFormData.text,
            onValueChange = { onValueChange(statementFormData.copy(text = it)) },
            label = stringResource(R.string.statement_text)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            AppMediumTitleText(text = stringResourceWithColon(R.string.defining_theme))
            DataChoosingListComponent(
                dataListUiState = statementAddingUiState,
                chosenEntityId = statementFormData.definingThemeId,
                maxHeight = LocalConfiguration.current.screenHeightDp.dp / 4
            ) { entity, isHasBorder ->
                AppMediumTextCard(
                    text = (entity as DefiningTheme).name,
                    onClick = {
                        onValueChange(
                            statementFormData.copy(
                                definingThemeId = entity.id
                                    .takeIf { statementFormData.definingThemeId == null }
                            )
                        )
                    },
                    isHasBorder = isHasBorder
                )
            }
        }
        AppMediumTitleText(text = stringResource(R.string.is_support_defining_theme))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = statementFormData.isSupportDefiningTheme == true,
                onClick = { onValueChange(statementFormData.copy(isSupportDefiningTheme = true)) },
                modifier = Modifier.testTag(stringResource(R.string.yes))
            )
            AppMediumTitleText(
                text = stringResource(R.string.yes),
                overrideModifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small)
                )
            )
            RadioButton(
                selected = statementFormData.isSupportDefiningTheme == false,
                onClick = { onValueChange(statementFormData.copy(isSupportDefiningTheme = false)) },
                modifier = Modifier.testTag(stringResource(R.string.no))
            )
            AppMediumTitleText(
                text = stringResource(R.string.no),
                overrideModifier = Modifier.padding(
                    top = dimensionResource(R.dimen.padding_small),
                    bottom = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_small)
                )
            )
        }
        AppLargeTextCard(
            isEnabled = statementFormData.isValid,
            text = stringResource(R.string.add_statement),
            onClick = onStatementAddingClick
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentAllDataFullFormPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = StatementFormData("Text", true)
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true, locale = "ru")
@Composable
private fun StatementAddingComponentAllDataFullFormRuPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = StatementFormData("Text", true)
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentFewDataPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = listOf(
                    FakeData.definingThemes[0],
                    FakeData.definingThemes[1]
                )
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentChosenDataWrongFormPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                entities = FakeData.definingThemes,
                formData = StatementFormData(definingThemeId = 1),
                actionRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementAddingComponentEmptyDataPreview() {
    AppTheme {
        StatementAddingScreenComponent(
            statementAddingUiState = StatementAddingUiState(
                dataRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}
