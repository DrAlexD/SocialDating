package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.DataEntity
import xelagurd.socialdating.data.model.additional.FormDetails
import xelagurd.socialdating.ui.state.DataEntityUiState
import xelagurd.socialdating.ui.state.DataListUiState
import xelagurd.socialdating.ui.state.FormUiState
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
    content: @Composable ColumnScope.(DataEntity) -> Unit
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

@Composable
fun ComponentWithRequestStatus(
    formUiState: FormUiState,
    onSuccess: () -> Unit,
    failedText: String,
    errorText: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (FormDetails) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        content(formUiState.formDetails)
        RequestStatusComponent(
            requestStatus = formUiState.requestStatus,
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
            RequestStatus.LOADING ->
                CircularProgressIndicator(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )

            RequestStatus.FAILED -> AppLargeTitleText(failedText)
            RequestStatus.ERROR -> AppLargeTitleText(errorText)
            RequestStatus.SUCCESS -> onSuccess()
        }
    }
}