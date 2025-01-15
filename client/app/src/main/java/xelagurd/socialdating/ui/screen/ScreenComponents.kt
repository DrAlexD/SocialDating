package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import xelagurd.socialdating.ui.state.InternetUiState
import xelagurd.socialdating.ui.state.RequestStatus


@Composable
fun DataListComponent(
    dataListUiState: DataListUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        internetUiState = dataListUiState,
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
    content: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        internetUiState = dataEntityUiState,
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
    internetUiState: InternetUiState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (internetUiState.isDataExist()) {
        content()
    } else {
        InternetStatusComponent(internetUiState.internetStatus)
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
        when (internetStatus) {
            InternetStatus.LOADING -> AppLoadingIndicator()
            InternetStatus.OFFLINE -> AppLargeTitleText(text = stringResource(R.string.no_internet_connection))
            InternetStatus.ONLINE -> AppLargeTitleText(text = stringResource(R.string.no_data))
        }
    }
}

@Composable
fun ComponentWithRequestStatus(
    requestStatus: RequestStatus,
    onSuccess: () -> Unit,
    failedText: String,
    errorText: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        content()
        RequestStatusComponent(
            requestStatus = requestStatus,
            onSuccess = onSuccess,
            failedText = failedText,
            errorText = errorText
        )
    }
}

@Composable
private inline fun RequestStatusComponent(
    requestStatus: RequestStatus,
    onSuccess: () -> Unit,
    failedText: String,
    errorText: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.fillMaxSize()
    ) {
        when (requestStatus) {
            RequestStatus.UNDEFINED -> {}
            RequestStatus.LOADING -> AppLoadingIndicator()
            RequestStatus.FAILED -> AppLargeTitleText(failedText)
            RequestStatus.ERROR -> AppLargeTitleText(errorText)
            RequestStatus.SUCCESS -> onSuccess()
        }
    }
}