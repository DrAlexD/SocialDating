package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.DataEntity
import xelagurd.socialdating.ui.state.DataEntityUiState
import xelagurd.socialdating.ui.state.DataListUiState
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.UiState


@Composable
fun DataListComponent(
    dataListUiState: DataListUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        uiState = dataListUiState,
        modifier = modifier
    ) {
        AppDataLazyList(
            entities = dataListUiState.entities,
            contentPadding = contentPadding,
            card = card
        )
    }
}

@Composable
fun DataEntityComponent(
    dataEntityUiState: DataEntityUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.(DataEntity) -> Unit
) {
    DataComponent(
        uiState = dataEntityUiState,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            content = { content(dataEntityUiState.entity!!) }
        )
    }
}

@Composable
private inline fun DataComponent(
    uiState: UiState,
    modifier: Modifier = Modifier,
    dataContent: @Composable () -> Unit
) {
    if (uiState.isDataExist()) {
        dataContent()
    } else {
        InternetStatusComponent(uiState.internetStatus)
    }
}

@Composable
private fun InternetStatusComponent(
    internetStatus: InternetStatus,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        AppLargeTitleText(
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