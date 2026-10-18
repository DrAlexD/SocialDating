package xelagurd.socialdating.client.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import xelagurd.socialdating.client.ui.state.isRequestedByUserOrDataExist


@Composable
fun DataListComponent(
    dataListUiState: DataListUiState,
    onRefresh: (() -> Unit)? = null,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataListUiState,
        onRefresh = onRefresh,
        contentPadding = contentPadding,
        statusModifier = Modifier.fillMaxSize()
    ) {
        AppDataList(
            entities = dataListUiState.entities,
            modifier = listModifier.fillMaxSize(),
            contentPadding = contentPadding,
            card = card
        )
    }
}

@Composable
fun PagedDataListComponent(
    pagedDataListUiState: PagedDataListUiState,
    onLoadNextPage: () -> Unit,
    onRefresh: (() -> Unit)? = null,
    listModifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    prefetchCount: Int = NEXT_PAGE_PREFETCH_COUNT,
    card: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = pagedDataListUiState,
        onRefresh = onRefresh,
        contentPadding = contentPadding,
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
            modifier = listModifier.fillMaxSize(),
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

            RequestStatus.UNDEFINED, RequestStatus.NOT_REQUESTED, RequestStatus.SUCCESS -> {
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
    onRefresh: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable (DataEntity) -> Unit
) {
    DataComponent(
        dataRequestUiState = dataEntityUiState,
        onRefresh = onRefresh,
        contentPadding = contentPadding,
        statusModifier = Modifier.fillMaxSize()
    ) {
        // the entity is not scrollable by itself, so it is placed into a scrollable container
        // to be refreshable by the gesture
        AppFullSizeScrollableContainer(contentPadding = contentPadding) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
                content = { content(dataEntityUiState.entity!!) }
            )
        }
    }
}

@Composable
private inline fun DataComponent(
    dataRequestUiState: DataRequestUiState,
    noinline onRefresh: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    statusModifier: Modifier = Modifier,
    crossinline content: @Composable () -> Unit
) {
    when {
        onRefresh != null -> {
            // the loading requested by the pull gesture is displayed by the refresh indicator, which continues it,
            // and any other one by the loading indicator in place of the absent data, so the displayed data leaves
            // only the former, while the user request of any other kind is still notified about its failure
            val isRefreshIndicatorDisplayed = dataRequestUiState.dataRequestStatus.isDataLoading() &&
                    isRequestedByUserOrDataExist(
                        isRequestedByUser = dataRequestUiState.isDataRequestedByPull,
                        isDataExist = { dataRequestUiState.isDataExist() }
                    )

            AppPullToRefreshContainer(
                isRefreshIndicatorDisplayed = isRefreshIndicatorDisplayed,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
                isAllowedDataRefresh = dataRequestUiState.dataRequestStatus.isAllowedDataRefresh(),
                contentPadding = contentPadding
            ) {
                if (dataRequestUiState.isDataExist()) {
                    content()
                } else {
                    // the status is placed into a scrollable container, so the data is refreshable
                    // by the gesture while it is absent, for example after a failed request
                    AppFullSizeScrollableContainer(contentPadding = contentPadding) {
                        DataRequestStatusComponent(
                            dataRequestStatus = dataRequestUiState.dataRequestStatus,
                            statusModifier = Modifier.fillMaxSize(),
                            isLoadingIndicatorDisplayed = !isRefreshIndicatorDisplayed
                        )
                    }
                }
            }
        }

        dataRequestUiState.isDataExist() -> content()

        else -> DataRequestStatusComponent(
            dataRequestStatus = dataRequestUiState.dataRequestStatus,
            statusModifier = statusModifier
        )
    }
}

@Composable
private fun DataRequestStatusComponent(
    dataRequestStatus: RequestStatus,
    statusModifier: Modifier = Modifier,
    isLoadingIndicatorDisplayed: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = statusModifier
    ) {
        when (dataRequestStatus) {
            RequestStatus.UNDEFINED, RequestStatus.LOADING -> {
                if (isLoadingIndicatorDisplayed) AppLoadingIndicator()
            }

            is RequestStatus.FAILURE -> AppLargeTitleText(text = dataRequestStatus.failureText)
            is RequestStatus.ERROR -> AppLargeTitleText(text = dataRequestStatus.errorText)
            RequestStatus.SUCCESS -> AppLargeTitleText(text = stringResource(R.string.no_data))
            RequestStatus.NOT_REQUESTED -> {}
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
            RequestStatus.SUCCESS, RequestStatus.UNDEFINED, RequestStatus.NOT_REQUESTED,
            is RequestStatus.FAILURE, is RequestStatus.ERROR -> {}
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

@Composable
fun TitledComponent(
    @StringRes titleRes: Int,
    hint: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_4dp))
    ) {
        AppMediumTitleText(text = stringResourceWithColon(titleRes))
        content()
        if (hint != null) {
            AppSmallBodyText(
                text = hint,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_8dp))
            )
        }
    }
}

@Composable
fun <T> MultiChoosingComponent(
    @StringRes titleRes: Int,
    options: List<T>,
    selectedOptions: Set<T>,
    onToggle: (T) -> Unit,
    optionText: @Composable (T) -> String,
    hint: String? = null
) {
    TitledComponent(titleRes = titleRes, hint = hint) {
        AppFilterChipGroup(
            options = options,
            selectedOptions = selectedOptions,
            onToggle = onToggle,
            optionText = optionText
        )
    }
}

@Composable
fun RangeChoosingComponent(
    @StringRes titleRes: Int,
    value: IntRange,
    valueRange: IntRange,
    onValueChange: (IntRange) -> Unit,
    // the max value of the range means the values without the upper limit, so the min value is less than it
    isMaxUnlimited: Boolean = false
) {
    // the value is changed continuously while dragging, so it is passed on only when the dragging is finished
    var currentValue by remember(value) { mutableStateOf(value) }

    TitledComponent(titleRes = titleRes) {
        AppLargeBodyText(
            text = if (isMaxUnlimited && currentValue.last == valueRange.last) {
                stringResource(R.string.unlimited_range, currentValue.first)
            } else {
                stringResource(R.string.range, currentValue.first, currentValue.last)
            }
        )
        AppRangeSlider(
            value = currentValue,
            valueRange = valueRange,
            onValueChange = {
                currentValue = if (isMaxUnlimited) it.first.coerceAtMost(valueRange.last - 1)..it.last else it
            },
            onValueChangeFinished = { onValueChange(currentValue) }
        )
    }
}
