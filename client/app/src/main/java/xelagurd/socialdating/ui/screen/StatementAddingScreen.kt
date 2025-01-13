package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.additional.StatementDetails
import xelagurd.socialdating.ui.navigation.StatementAddingDestination
import xelagurd.socialdating.ui.state.RequestStatus
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
        StatementAddingBody(
            statementAddingUiState = statementAddingUiState,
            onValueChange = statementAddingViewModel::updateUiState,
            onStatementAddingClick = statementAddingViewModel::statementAdding,
            onSuccessStatementAdding = onSuccessStatementAdding,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun StatementAddingBody(
    statementAddingUiState: StatementAddingUiState,
    onValueChange: (StatementDetails) -> Unit,
    onStatementAddingClick: () -> Unit,
    onSuccessStatementAdding: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier.padding(contentPadding)) {
        StatementAddingDetails(
            statementAddingUiState = statementAddingUiState,
            onValueChange = onValueChange,
            onStatementAddingClick = onStatementAddingClick,
            modifier = modifier
        )
        StatementAddingStatus(
            requestStatus = statementAddingUiState.requestStatus,
            onSuccessStatementAdding = onSuccessStatementAdding,
            modifier = modifier
        )
    }
}

@Composable
private fun StatementAddingDetails(
    statementAddingUiState: StatementAddingUiState,
    onValueChange: (StatementDetails) -> Unit,
    onStatementAddingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statementDetails = statementAddingUiState.statementDetails

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TextField(
            value = statementDetails.text,
            onValueChange = { onValueChange(statementDetails.copy(text = it)) },
            label = { Text(stringResource(R.string.statement_text)) },
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_large))
        ) {
            Text(
                text = stringResourceWithColon(R.string.defining_theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_small))
            )
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
        Text(
            text = stringResource(R.string.is_support_defining_theme),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_small))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = statementDetails.isSupportDefiningTheme == true,
                onClick = { onValueChange(statementDetails.copy(isSupportDefiningTheme = true)) },
                modifier = Modifier.testTag(stringResource(R.string.yes))
            )
            Text(
                text = stringResource(R.string.yes),
                style = MaterialTheme.typography.titleMedium
            )
            RadioButton(
                selected = statementDetails.isSupportDefiningTheme == false,
                onClick = { onValueChange(statementDetails.copy(isSupportDefiningTheme = false)) },
                modifier = Modifier.testTag(stringResource(R.string.no))
            )
            Text(
                text = stringResource(R.string.no),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Card(
            enabled = statementDetails.isValid,
            onClick = onStatementAddingClick,
            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.add_statement),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
internal fun DefiningThemesBody(
    definingThemes: List<DefiningTheme>,
    onDefiningThemeClick: (Int) -> Unit,
    chosenDefiningThemeId: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (definingThemes.isNotEmpty()) {
            if (chosenDefiningThemeId != null) {
                DefiningThemeCard(
                    definingTheme = definingThemes.first { it.id == chosenDefiningThemeId },
                    onDefiningThemeClick = onDefiningThemeClick,
                    isChosen = true,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                )
            } else {
                DefiningThemesList(
                    definingThemes = definingThemes,
                    onDefiningThemeClick = onDefiningThemeClick,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(R.string.no_data),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
                )
            }
        }
    }
}

@Composable
private fun DefiningThemesList(
    definingThemes: List<DefiningTheme>,
    onDefiningThemeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp / 4

    LazyColumn(
        modifier = modifier.heightIn(0.dp, maxHeight)
    ) {
        items(items = definingThemes, key = { it.id }) {
            DefiningThemeCard(
                definingTheme = it,
                onDefiningThemeClick = onDefiningThemeClick,
                isChosen = false
            )
        }
    }
}

@Composable
fun DefiningThemeCard(
    definingTheme: DefiningTheme,
    onDefiningThemeClick: (Int) -> Unit,
    isChosen: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onDefiningThemeClick(definingTheme.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_small)),
        border = BorderStroke(1.dp, Color.Black).takeIf { isChosen },
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_very_small))
    ) {
        Text(
            text = definingTheme.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
private fun StatementAddingStatus(
    requestStatus: RequestStatus,
    onSuccessStatementAdding: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        when (requestStatus) {
            RequestStatus.UNDEFINED -> {}

            RequestStatus.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.ERROR -> {
                Text(
                    text = stringResource(R.string.no_internet_connection),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.FAILED -> {
                Text(
                    text = stringResource(R.string.failed_add_statement),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.SUCCESS -> onSuccessStatementAdding()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatementDetailsPreview() {
    AppTheme {
        StatementAddingDetails(
            statementAddingUiState = StatementAddingUiState(),
            onValueChange = { },
            onStatementAddingClick = { }
        )
    }
}
