package xelagurd.socialdating.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.DataEntity
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.DataListUiState

@Composable
@ReadOnlyComposable
fun stringResourceWithColon(@StringRes id: Int) =
    stringResource(R.string.text_with_colon, stringResource(id))

@Composable
fun AppLargeText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_large))
    )
}

@Composable
fun DataListBody(
    uiState: DataListUiState,
    onEntityClick: (Int) -> Unit,
    cardContent: @Composable ColumnScope.(DataEntity) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.entities.isNotEmpty()) {
            AppDataList(
                entities = uiState.entities,
                onEntityClick = onEntityClick,
                contentPadding = contentPadding,
                cardContent = cardContent
            )
        } else {
            InternetStatusBody(
                uiState.internetStatus
            )
        }
    }
}

@Composable
private fun InternetStatusBody(
    internetStatus: InternetStatus,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        AppLargeText(
            text = stringResource(
                when (internetStatus) {
                    InternetStatus.ONLINE -> R.string.no_data
                    InternetStatus.LOADING -> R.string.loading
                    InternetStatus.OFFLINE -> R.string.no_internet_connection
                }
            )
        )
    }
}

@Composable
private inline fun AppDataList(
    entities: List<DataEntity>,
    crossinline onEntityClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    crossinline cardContent: @Composable ColumnScope.(DataEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        items(items = entities, key = { it.id }) {
            AppEntityCard(
                entity = it,
                onEntityClick = onEntityClick,
                content = cardContent
            )
        }
    }
}

@Composable
private inline fun AppEntityCard(
    entity: DataEntity,
    crossinline onEntityClick: (Int) -> Unit,
    crossinline content: @Composable ColumnScope.(DataEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onEntityClick(entity.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        content = { content(entity) }
    )
}