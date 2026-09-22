package xelagurd.socialdating.client.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.data.model.DataUtils.NEXT_PAGE_PREFETCH_COUNT
import xelagurd.socialdating.client.ui.state.DataEntityUiState
import xelagurd.socialdating.client.ui.state.DataListUiState
import xelagurd.socialdating.client.ui.state.DataRequestUiState
import xelagurd.socialdating.client.ui.state.PagedDataListUiState
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
fun PagedDataListComponent(
    pagedDataListUiState: PagedDataListUiState,
    onLoadNextPage: () -> Unit,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    prefetchCount: Int = NEXT_PAGE_PREFETCH_COUNT,
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = pagedDataListUiState,
        statusModifier = Modifier.fillMaxSize()
    ) {
        val listState = rememberLazyListState()

        LoadNextPageEffect(
            listState = listState,
            entitiesCount = pagedDataListUiState.entities.size,
            isEnabled = pagedDataListUiState.isAllowedNextPageLoading(),
            prefetchCount = prefetchCount,
            onLoadNextPage = onLoadNextPage
        )

        AppDataList(
            entities = pagedDataListUiState.entities,
            modifier = listModifier,
            contentPadding = contentPadding,
            listState = listState,
            footer = {
                NextPageRequestStatusComponent(
                    pagedDataListUiState = pagedDataListUiState,
                    onLoadNextPage = onLoadNextPage
                )
            },
            card = card
        )
    }
}

@Composable
private fun NextPageRequestStatusComponent(
    pagedDataListUiState: PagedDataListUiState,
    onLoadNextPage: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (val nextPageRequestStatus = pagedDataListUiState.nextPageRequestStatus) {
            RequestStatus.LOADING -> AppLoadingIndicator()

            is RequestStatus.FAILURE -> AppRefreshTextCard(
                text = nextPageRequestStatus.failureText,
                onClick = onLoadNextPage
            )

            is RequestStatus.ERROR -> AppRefreshTextCard(
                text = nextPageRequestStatus.errorText,
                onClick = onLoadNextPage
            )

            RequestStatus.UNDEFINED, RequestStatus.SUCCESS -> {
                if (pagedDataListUiState.isLastPage) {
                    AppMediumTitleText(text = stringResource(R.string.no_more_data))
                }
            }
        }
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
fun DataMultiChoosingListComponent(
    dataListUiState: DataListUiState,
    chosenEntityIds: Set<Int>,
    maxHeight: Dp,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity, Boolean) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataListUiState
    ) {
        AppDataMultiChoosingList(
            entities = dataListUiState.entities,
            chosenEntityIds = chosenEntityIds,
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
    onSuccess: () -> Unit = {},
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
private fun ActionRequestStatusComponent(
    actionRequestStatus: RequestStatus,
    onSuccess: () -> Unit
) {
    val currentOnSuccess by rememberUpdatedState(onSuccess)

    // a success is a one-time event, so it is handled by an effect instead of the composition,
    // which would repeat the action on every recomposition with the same status
    LaunchedEffect(actionRequestStatus) {
        if (actionRequestStatus == RequestStatus.SUCCESS) currentOnSuccess()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        when (actionRequestStatus) {
            RequestStatus.LOADING -> AppLoadingIndicator()
            // the success is handled by the effect, the failed statuses by the notification
            RequestStatus.SUCCESS, RequestStatus.UNDEFINED, is RequestStatus.FAILURE,
            is RequestStatus.ERROR -> {}
        }
    }
}

@Composable
fun <T> SettingChoosingComponent(
    @StringRes titleRes: Int,
    options: List<T>,
    selectedOption: T?,
    onSelect: (T) -> Unit,
    optionDescriptionRes: (T) -> Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_8dp))
    ) {
        AppLargeTitleText(text = stringResourceWithColon(titleRes))
        AppRadioGroup(
            options = options,
            selectedOption = selectedOption,
            onSelect = onSelect,
            optionDescriptionRes = optionDescriptionRes,
            testTagSuffix = stringResource(titleRes)
        )
    }
}
