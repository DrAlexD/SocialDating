package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
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
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.additional.StatementDetails
import xelagurd.socialdating.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.ui.state.StatementAddingUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.StatementAddingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementAddingScreen(
    onSuccessStatementAdding: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    statementAddingViewModel: StatementAddingViewModel = hiltViewModel()
) {
    val statementAddingUiState by statementAddingViewModel.uiState.collectAsState()

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
        ComponentWithRequestStatus(
            requestStatus = statementAddingUiState.requestStatus,
            onSuccess = onSuccessStatementAdding,
            failedText = stringResource(R.string.failed_add_statement),
            errorText = stringResource(R.string.no_internet_connection),
            contentPadding = innerPadding
        ) {
            StatementDetailsBody(
                statementAddingUiState = statementAddingUiState,
                onValueChange = statementAddingViewModel::updateUiState,
                onStatementAddingClick = statementAddingViewModel::statementAdding
            )
        }
    }
}

@Composable
fun StatementDetailsBody(
    statementAddingUiState: StatementAddingUiState,
    onValueChange: (StatementDetails) -> Unit,
    onStatementAddingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statementDetails = statementAddingUiState.formDetails

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        AppTextField(
            value = statementDetails.text,
            onValueChange = { onValueChange(statementDetails.copy(text = it)) },
            label = stringResource(R.string.statement_text)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_very_large))
        ) {
            AppMediumTitleText(text = stringResourceWithColon(R.string.defining_theme))
            DefiningThemesBody(
                definingThemes = statementAddingUiState.definingThemes,
                onDefiningThemeClick = {
                    onValueChange(
                        statementDetails.copy(
                            definingThemeId = it.takeIf { statementDetails.definingThemeId == null }
                        )
                    )
                },
                chosenDefiningThemeId = statementDetails.definingThemeId
            )
        }
        AppMediumTitleText(text = stringResource(R.string.is_support_defining_theme))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = statementDetails.isSupportDefiningTheme == true,
                onClick = { onValueChange(statementDetails.copy(isSupportDefiningTheme = true)) },
                modifier = Modifier.testTag(stringResource(R.string.yes))
            )
            AppMediumTitleText(text = stringResource(R.string.yes))
            RadioButton(
                selected = statementDetails.isSupportDefiningTheme == false,
                onClick = { onValueChange(statementDetails.copy(isSupportDefiningTheme = false)) },
                modifier = Modifier.testTag(stringResource(R.string.no))
            )
            AppMediumTitleText(text = stringResource(R.string.no))
        }
        AppLargeTextCard(
            isEnabled = statementDetails.isValid,
            text = stringResource(R.string.add_statement),
            onClick = onStatementAddingClick
        )
    }
}

@Composable
private fun DefiningThemesBody(
    definingThemes: List<DefiningTheme>,
    onDefiningThemeClick: (Int) -> Unit,
    chosenDefiningThemeId: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        if (definingThemes.isNotEmpty()) {
            if (chosenDefiningThemeId != null) {
                val definingTheme = definingThemes.first { it.id == chosenDefiningThemeId }
                AppSmallTextCard(
                    text = definingTheme.name,
                    onClick = { onDefiningThemeClick(definingTheme.id) },
                    isHasBorder = true
                )
            } else {
                val maxHeight = LocalConfiguration.current.screenHeightDp.dp / 4
                AppDataLazyList(
                    entities = definingThemes,
                    modifier = Modifier.heightIn(0.dp, maxHeight)
                ) {
                    AppSmallTextCard(
                        text = (it as DefiningTheme).name,
                        onClick = { onDefiningThemeClick(it.id) }
                    )
                }
            }
        } else {
            AppLargeTitleText(text = stringResource(R.string.no_data))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatementAddingComponentPreview() {
    AppTheme {
        val statementAddingUiState = StatementAddingUiState(
            definingThemes = FakeDataSource.definingThemes
        )

        ComponentWithRequestStatus(
            requestStatus = statementAddingUiState.requestStatus,
            onSuccess = { },
            failedText = stringResource(R.string.failed_add_statement),
            errorText = stringResource(R.string.no_internet_connection)
        ) {
            StatementDetailsBody(
                statementAddingUiState = statementAddingUiState,
                onValueChange = { },
                onStatementAddingClick = { }
            )
        }
    }
}
