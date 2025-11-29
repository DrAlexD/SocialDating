package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.enums.StatementReactionType
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.StatementsDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.StatementsUiState
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementsScreen(
    onStatementClick: (Int) -> Unit,
    onStatementAddingClick: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    statementsViewModel: StatementsViewModel = hiltViewModel()
) {
    val statementsUiState by statementsViewModel.uiState.collectAsState()

    StatementsScreenComponent(
        statementsUiState = statementsUiState,
        onStatementClick = onStatementClick,
        onStatementAddingClick = onStatementAddingClick,
        onNavigateUp = onNavigateUp,
        refreshAction = statementsViewModel::getStatements,
        onStatementReactionClick = { statement, reactionType ->
            statementsViewModel.onStatementReactionClick(statement, reactionType)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementsScreenComponent(
    statementsUiState: StatementsUiState = StatementsUiState(),
    onStatementClick: (Int) -> Unit = {},
    onStatementAddingClick: (Int) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    refreshAction: () -> Unit = {},
    onStatementReactionClick: (Statement, StatementReactionType) -> Unit = { _, _ -> null }
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(StatementsDestination.titleRes),
                dataRequestStatus = statementsUiState.dataRequestStatus,
                refreshAction = refreshAction,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = StatementsDestination.topLevelRoute
            )
        },
        floatingActionButton = {
            if (statementsUiState.isDataExist()) {
                FloatingActionButton(
                    onClick = { onStatementAddingClick(statementsUiState.categoryId) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_statement)
                    )
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataListComponent(
            dataListUiState = statementsUiState,
            contentPadding = innerPadding
        ) {
            AppEntityCard(
                entity = it,
                onEntityClick = { } // TODO
            ) {
                StatementCardContent(
                    statement = it as Statement,
                    onStatementReactionClick = onStatementReactionClick
                )
            }
        }
    }
}

@Composable
private inline fun StatementCardContent(
    statement: Statement,
    crossinline onStatementReactionClick: (Statement, StatementReactionType) -> Unit
) {
    AppLargeTitleText(text = statement.text)
    HorizontalDivider()
    ReactionsRow(
        onStatementReactionClick = { onStatementReactionClick(statement, it) }
    )
}

@Composable
private inline fun ReactionsRow(
    crossinline onStatementReactionClick: (StatementReactionType) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        StatementReactionType.entries.forEachIndexed { index, statementReactionType ->
            IconButton(onClick = { onStatementReactionClick(statementReactionType) }) {
                Icon(
                    painter = painterResource(statementReactionType.iconRes),
                    contentDescription = stringResource(statementReactionType.descriptionRes),
                    modifier = Modifier.graphicsLayer {
                        this.scaleX = 0.9f
                        this.scaleY = 0.9f
                    }
                )
            }
            if (index != StatementReactionType.entries.lastIndex) {
                VerticalDivider(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementsComponentDataPreview() {
    AppTheme {
        StatementsScreenComponent(
            statementsUiState = StatementsUiState(entities = FakeData.statements)
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun StatementsComponentNoDataPreview() {
    AppTheme {
        StatementsScreenComponent(
            statementsUiState = StatementsUiState(dataRequestStatus = RequestStatus.ERROR("Text"))
        )
    }
}
