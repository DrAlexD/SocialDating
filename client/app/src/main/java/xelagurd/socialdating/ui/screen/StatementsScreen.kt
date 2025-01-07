package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.enums.StatementReactionType
import xelagurd.socialdating.ui.navigation.StatementsDestination
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.StatementsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.StatementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementsScreen(
    onStatementClick: (Int) -> Unit,
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
                refreshAction = { statementsViewModel.getStatements() },
                navigateUp = { onNavigateUp.invoke() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = StatementsDestination.topLevelRoute
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        StatementsBody(
            statementsUiState = statementsUiState,
            onStatementClick = onStatementClick,
            onStatementReactionClick = { id, type ->
                statementsViewModel.onStatementReactionClick(id, type)
            },
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun StatementsBody(
    statementsUiState: StatementsUiState,
    onStatementClick: (Int) -> Unit,
    onStatementReactionClick: (Int, StatementReactionType) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (statementsUiState.statements.isNotEmpty()) {
            StatementsList(
                statements = statementsUiState.statements,
                onStatementClick = onStatementClick,
                onStatementReactionClick = onStatementReactionClick,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(
                        when (statementsUiState.internetStatus) {
                            InternetStatus.ONLINE -> R.string.no_data
                            InternetStatus.LOADING -> R.string.loading
                            InternetStatus.OFFLINE -> R.string.no_internet_connection
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
                )
            }
        }
    }
}

@Composable
private fun StatementsList(
    statements: List<Statement>,
    onStatementClick: (Int) -> Unit,
    onStatementReactionClick: (Int, StatementReactionType) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = statements, key = { it.id }) {
            StatementCard(
                statement = it,
                onStatementClick = onStatementClick,
                onStatementReactionClick = onStatementReactionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun StatementCard(
    statement: Statement,
    onStatementClick: (Int) -> Unit,
    onStatementReactionClick: (Int, StatementReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onStatementClick(statement.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
        modifier = modifier
    ) {
        Text(
            text = statement.text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
        )
        HorizontalDivider(color = Color.Black)
        ReactionsBody(
            onStatementReactionClick = { onStatementReactionClick.invoke(statement.id, it) }
        )
    }
}

@Composable
fun ReactionsBody(
    onStatementReactionClick: (StatementReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        StatementReactionType.entries.forEachIndexed { index, statementReactionType ->
            IconButton(onClick = { onStatementReactionClick.invoke(statementReactionType) }) {
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
        StatementsBody(
            statementsUiState = StatementsUiState(
                statements = FakeDataSource.statements,
                internetStatus = InternetStatus.OFFLINE
            ),
            onStatementClick = {},
            onStatementReactionClick = { _, _ -> null }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatementCardPreview() {
    AppTheme {
        StatementCard(
            statement = FakeDataSource.statements[0],
            onStatementClick = {},
            onStatementReactionClick = { _, _ -> null }
        )
    }
}
