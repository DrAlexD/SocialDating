package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.enums.StatementReactionType
import xelagurd.socialdating.ui.navigation.StatementsDestination
import xelagurd.socialdating.ui.state.StatementsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementsScreen(
    onStatementClick: (Int) -> Unit,
    onStatementAddingClick: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    statementsViewModel: StatementsViewModel = hiltViewModel()
) {
    val statementsUiState by statementsViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(StatementsDestination.titleRes),
                internetStatus = statementsUiState.internetStatus,
                refreshAction = statementsViewModel::getStatements,
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
            if (statementsUiState.entities.isNotEmpty()) {
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataListBody(
            uiState = statementsUiState,
            onEntityClick = onStatementClick,
            contentPadding = innerPadding,
            cardContent = {
                StatementCardContent(
                    statement = it as Statement,
                    onStatementReactionClick = { id, type ->
                        statementsViewModel.onStatementReactionClick(id, type)
                    }
                )
            }
        )
    }
}

@Composable
fun StatementCardContent(
    statement: Statement,
    onStatementReactionClick: (Int, StatementReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    AppLargeText(text = statement.text)
    HorizontalDivider(color = Color.Black)
    ReactionsRow(
        onStatementReactionClick = { onStatementReactionClick(statement.id, it) }
    )
}

@Composable
fun ReactionsRow(
    onStatementReactionClick: (StatementReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        StatementReactionType.entries.forEachIndexed { index, statementReactionType ->
            IconButton(onClick = { onStatementReactionClick(statementReactionType) }) {
                Icon(
                    imageVector = statementReactionType.imageVector,
                    contentDescription = stringResource(statementReactionType.descriptionRes)
                )
            }
            if (index != StatementReactionType.entries.lastIndex) {
                VerticalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatementsBodyOfflineDataPreview() {
    AppTheme {
        DataListBody(
            uiState = StatementsUiState(entities = FakeDataSource.statements),
            onEntityClick = { },
            cardContent = {
                StatementCardContent(
                    statement = it as Statement,
                    onStatementReactionClick = { _, _ -> null }
                )
            }
        )
    }
}
