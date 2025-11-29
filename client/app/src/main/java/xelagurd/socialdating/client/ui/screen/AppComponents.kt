package xelagurd.socialdating.client.ui.screen

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.ui.state.DataEntityUiState
import xelagurd.socialdating.client.ui.state.DataListUiState
import xelagurd.socialdating.client.ui.state.DataRequestUiState
import xelagurd.socialdating.client.ui.state.RequestStatus


@Composable
fun DataListComponent(
    dataListUiState: DataListUiState,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataListUiState,
        statusModifier = Modifier.fillMaxSize()
    ) {
        AppDataList(
            entities = dataListUiState.entities,
            modifier = listModifier,
            contentPadding = contentPadding,
            card = card
        )
    }
}

@Composable
fun DataChoosingListComponent(
    dataListUiState: DataListUiState,
    chosenEntityId: Int?,
    maxHeight: Dp,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity, Boolean) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataListUiState
    ) {
        AppDataChoosingList(
            entities = dataListUiState.entities,
            chosenEntityId = chosenEntityId,
            maxHeight = maxHeight,
            modifier = listModifier,
            contentPadding = contentPadding,
            card = card
        )
    }
}

@Composable
fun DataEntityComponent(
    dataEntityUiState: DataEntityUiState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataEntityUiState,
        statusModifier = Modifier.fillMaxSize()
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
    dataRequestUiState: DataRequestUiState,
    statusModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (dataRequestUiState.isDataExist()) {
        content()
    } else {
        DataRequestStatusComponent(
            dataRequestStatus = dataRequestUiState.dataRequestStatus,
            statusModifier = statusModifier
        )
    }
}

@Composable
private fun DataRequestStatusComponent(
    dataRequestStatus: RequestStatus,
    statusModifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = statusModifier
    ) {
        when (dataRequestStatus) {
            RequestStatus.UNDEFINED, RequestStatus.LOADING -> AppLoadingIndicator()
            is RequestStatus.FAILURE -> AppLargeTitleText(text = dataRequestStatus.failureText)
            is RequestStatus.ERROR -> AppLargeTitleText(text = dataRequestStatus.errorText)
            RequestStatus.SUCCESS -> AppLargeTitleText(text = stringResource(R.string.no_data))
        }
    }
}

@Composable
fun ComponentWithActionRequestStatus(
    actionRequestStatus: RequestStatus,
    onSuccess: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        content()
        ActionRequestStatusComponent(
            actionRequestStatus = actionRequestStatus,
            onSuccess = onSuccess
        )
    }
}

@Composable
private inline fun ActionRequestStatusComponent(
    actionRequestStatus: RequestStatus,
    onSuccess: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        when (actionRequestStatus) {
            RequestStatus.UNDEFINED -> {}
            RequestStatus.LOADING -> AppLoadingIndicator()
            is RequestStatus.FAILURE -> AppLargeTitleText(actionRequestStatus.failureText)
            is RequestStatus.ERROR -> AppLargeTitleText(actionRequestStatus.errorText)
            RequestStatus.SUCCESS -> onSuccess()
        }
    }
}