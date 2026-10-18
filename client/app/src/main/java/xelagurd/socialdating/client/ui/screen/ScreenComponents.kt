package xelagurd.socialdating.client.ui.screen

import kotlin.math.roundToInt
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.data.model.DataUtils.NEXT_PAGE_PREFETCH_COUNT
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DATE_DIGITS_NUMBER
import xelagurd.socialdating.client.ui.form.FormFieldError
import xelagurd.socialdating.client.ui.state.RequestStatus

private val ONLINE_COLOR = Color(0xFF4CAF50)
private val LOADING_COLOR = Color(0xFFFFC107)
private val OFFLINE_COLOR = Color(0xFFF44336)
private val NOT_REQUESTED_COLOR = Color(0xFF9E9E9E)

@Composable
@ReadOnlyComposable
fun stringResourceWithColon(@StringRes id: Int) =
    stringResource(R.string.text_with_colon, stringResource(id))

@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_16dp))
            .testTag(stringResource(R.string.loading))
    )
}

@Composable
fun AppLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = progress,
        drawStopIndicator = {},
        modifier = modifier.testTag(stringResource(R.string.progress_indicator))
    )
}

@Composable
fun AppDataRequestStatusIndicator(
    dataRequestStatus: RequestStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = when (dataRequestStatus) {
        RequestStatus.SUCCESS -> ONLINE_COLOR
        RequestStatus.UNDEFINED, RequestStatus.LOADING -> LOADING_COLOR
        is RequestStatus.FAILURE, is RequestStatus.ERROR -> OFFLINE_COLOR
        RequestStatus.NOT_REQUESTED -> NOT_REQUESTED_COLOR
    }
    val statusDescription = stringResource(
        when (dataRequestStatus) {
            RequestStatus.SUCCESS -> R.string.online
            RequestStatus.UNDEFINED, RequestStatus.LOADING -> R.string.loading
            is RequestStatus.FAILURE, is RequestStatus.ERROR -> R.string.offline
            RequestStatus.NOT_REQUESTED -> R.string.not_requested
        }
    )

    Box(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_12dp))
            .size(dimensionResource(R.dimen.size_12dp))
            .background(statusColor, CircleShape)
            .semantics { contentDescription = statusDescription }
    )
}

@Composable
fun AppNotificationHost(
    notificationHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = notificationHostState,
        modifier = modifier.testTag(stringResource(R.string.notification))
    )
}

suspend fun SnackbarHostState.showNotification(text: String) =
    showSnackbar(message = text, duration = SnackbarDuration.Short)

@Composable
fun NotificationEffect(
    notification: String?,
    notificationHostState: SnackbarHostState,
    onNotificationShown: () -> Unit
) {
    val currentOnNotificationShown by rememberUpdatedState(onNotificationShown)

    LaunchedEffect(notification) {
        if (notification == null) return@LaunchedEffect

        notificationHostState.showNotification(notification)
        currentOnNotificationShown()
    }
}

@Composable
fun AppSmallBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_1dp))
    )
}

@Composable
fun AppMediumBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_2dp))
    )
}

@Composable
fun AppLargeBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_4dp))
    )
}

@Composable
fun AppSmallTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_4dp))
    )
}

@Composable
fun AppMediumTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp))
    )
}

@Composable
fun AppLargeTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        textAlign = textAlign,
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_12dp))
    )
}

@Composable
fun AppMediumTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    isEnabled: Boolean = true,
    isHasBorder: Boolean = false
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_2dp)),
        isHasBorder = isHasBorder,
        modifier = modifier,
        overrideModifier = overrideModifier
    ) {
        AppMediumTitleText(text = text)
    }
}

@Composable
fun AppLargeTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    isEnabled: Boolean = true,
    isHasBorder: Boolean = false
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        isHasBorder = isHasBorder,
        modifier = modifier,
        overrideModifier = overrideModifier
    ) {
        AppLargeTitleText(text = text)
    }
}

@Composable
fun AppRefreshTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_2dp)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_8dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppMediumTitleText(
                text = text,
                overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_4dp))
            )
            Icon(
                painter = painterResource(R.drawable.refresh),
                contentDescription = stringResource(R.string.refresh),
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_4dp))
            )
        }
    }
}

@Composable
private fun AppTextCard(
    isEnabled: Boolean,
    onClick: () -> Unit,
    elevation: CardElevation,
    isHasBorder: Boolean,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        enabled = isEnabled,
        onClick = onClick,
        elevation = elevation,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary).takeIf { isHasBorder },
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = content
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    textModifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    // the value of the date contains only the digits, which are shown in the DD.MM.YYYY mask
    isDate: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: FormFieldError? = null,
    // the field can be marked by the error of the several fields, whose message is shown by another field
    isError: Boolean = error != null
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = if (isDate) {
            { onValueChange(it.filter(Char::isDigit).take(DATE_DIGITS_NUMBER)) }
        } else {
            onValueChange
        },
        label = {
            AppMediumTitleText(
                text = label,
                overrideModifier = textModifier
            )
        },
        placeholder = if (isDate) {
            {
                AppMediumBodyText(
                    text = stringResource(R.string.date_placeholder),
                    overrideModifier = Modifier
                )
            }
        } else null,
        isError = isError,
        supportingText = error?.let {
            {
                AppSmallBodyText(
                    text = stringResource(it.messageRes, *it.formatArgs.toTypedArray())
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                AppPasswordVisibilityToggle(
                    isPasswordVisible = isPasswordVisible,
                    onClick = { isPasswordVisible = !isPasswordVisible }
                )
            }
        } else null,
        singleLine = singleLine,
        visualTransformation = when {
            isPassword && !isPasswordVisible -> PasswordVisualTransformation()
            isDate -> DateVisualTransformation
            else -> VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                isPassword -> KeyboardType.Password
                isDate -> KeyboardType.Number
                else -> keyboardType
            }
        ),
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp))
    )
}

private object DateVisualTransformation : VisualTransformation {
    private const val SEPARATOR = '.'
    private const val NOT_ENTERED_DIGIT = '_'

    // the numbers of the digits before the separators, which follow the day and the month
    private val separatorOffsets = listOf(2, 4)
    private val transformedSeparatorOffsets = separatorOffsets.mapIndexed { index, offset -> offset + index }

    override fun filter(text: AnnotatedString): TransformedText {
        // the empty value is shown as is, so that the label and the placeholder are shown instead of the mask
        if (text.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val maskedText = buildString {
            text.text.padEnd(DATE_DIGITS_NUMBER, NOT_ENTERED_DIGIT).forEachIndexed { index, digit ->
                if (index in separatorOffsets) append(SEPARATOR)
                append(digit)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) =
                offset + separatorOffsets.count { it <= offset }

            // the cursor is never placed among the not entered digits
            override fun transformedToOriginal(offset: Int) =
                (offset - transformedSeparatorOffsets.count { it < offset }).coerceAtMost(text.length)
        }

        return TransformedText(AnnotatedString(maskedText), offsetMapping)
    }
}

@Composable
private fun AppPasswordVisibilityToggle(
    isPasswordVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(
                if (isPasswordVisible) R.drawable.visibility_off else R.drawable.visibility
            ),
            contentDescription = stringResource(
                if (isPasswordVisible) R.string.hide_password else R.string.show_password
            )
        )
    }
}

@Composable
fun AppBadgedIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    badgeNumber: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                // the zero number means that there is nothing to notice
                if (badgeNumber > 0) {
                    Badge { Text(text = badgeNumber.toString()) }
                }
            }
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppExtendedFloatingActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    // the floating action button has no disabled state, so it gets the colors of the disabled button
    // and loses the ripple, and the container is opaque, because the button is placed over the content
    val containerColor = when {
        isEnabled -> FloatingActionButtonDefaults.containerColor
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    }
    val contentColor = when {
        isEnabled -> contentColorFor(containerColor)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }   
    // the ripple is made transparent instead of the switching off by the null configuration,
    // because the repeated switching off fails in Material3 1.3
    val rippleConfiguration = when {
        isEnabled -> LocalRippleConfiguration.current
        else -> RippleConfiguration(rippleAlpha = RippleAlpha(0f, 0f, 0f, 0f))
    }

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        // the overload with the separate text hides it from the semantics, so the button content is set here
        ExtendedFloatingActionButton(
            onClick = { if (isEnabled) onClick() },
            containerColor = containerColor,
            contentColor = contentColor,
            modifier = modifier.semantics { if (!isEnabled) disabled() }
        ) {
            // the button is described by its text
            Icon(painter = painterResource(iconRes), contentDescription = null)
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_12dp)))
            Text(text = text)
        }
    }
}

// the sheet is hidden by the animation before the dismiss, so the content gets the function to hide it
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(hideSheet: () -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        content {
            coroutineScope
                .launch { sheetState.hide() }
                .invokeOnCompletion { currentOnDismissRequest() }
        }
    }
}

// the content must be scrollable, otherwise the gesture is not delivered to the container,
// and the indicator is displaced by the top content padding, so it is not hidden by a top bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun AppPullToRefreshContainer(
    isRefreshIndicatorDisplayed: Boolean,
    noinline onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isAllowedDataRefresh: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline content: @Composable () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val refreshRequestsCount = remember { mutableIntStateOf(0) }

    // the indicator stays at the threshold after the gesture until the refreshing ends, so it is hidden here
    // when the refreshing has not started at all, for example when the refresh is ignored in the offline mode
    LaunchedEffect(refreshRequestsCount.intValue) {
        if (refreshRequestsCount.intValue > 0 && !isRefreshIndicatorDisplayed) pullToRefreshState.animateToHidden()
    }

    Box(
        modifier = modifier.pullToRefresh(
            isRefreshing = isRefreshIndicatorDisplayed,
            state = pullToRefreshState,
            enabled = isAllowedDataRefresh,
            onRefresh = {
                refreshRequestsCount.intValue++
                // the gesture is not blocked once it has started, so the refresh can be disallowed by its end
                if (isAllowedDataRefresh) onRefresh()
            }
        )
    ) {
        content()
        PullToRefreshDefaults.Indicator(
            state = pullToRefreshState,
            isRefreshing = isRefreshIndicatorDisplayed,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = contentPadding.calculateTopPadding())
                .testTag(stringResource(R.string.refresh_indicator))
        )
    }
}

@Composable
inline fun AppFullSizeScrollableContainer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        item {
            Box(modifier = Modifier.fillParentMaxSize()) {
                content()
            }
        }
    }
}

@Composable
inline fun AppDataList(
    entities: List<DataEntity>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    listState: LazyListState = rememberLazyListState(),
    noinline footer: (@Composable () -> Unit)? = null,
    crossinline card: @Composable (DataEntity) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_8dp))
    ) {
        items(items = entities, key = { it.id }) {
            card(it)
        }
        footer?.let { footerContent ->
            item { footerContent() }
        }
    }
}

@Composable
fun LoadNextPageEffect(
    listState: LazyListState,
    entitiesCount: Int,
    isEnabled: Boolean,
    prefetchCount: Int = NEXT_PAGE_PREFETCH_COUNT,
    onLoadNextPage: () -> Unit
) {
    val currentOnLoadNextPage by rememberUpdatedState(onLoadNextPage)

    LaunchedEffect(listState, entitiesCount, isEnabled, prefetchCount) {
        if (!isEnabled) return@LaunchedEffect

        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && entitiesCount - lastVisibleIndex <= prefetchCount) {
                    currentOnLoadNextPage()
                }
            }
    }
}

@Composable
inline fun AppDataChoosingList(
    entities: List<DataEntity>,
    chosenEntityId: Int?,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline card: @Composable (DataEntity, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
        )
    ) {
        if (chosenEntityId != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_8dp))
            ) {
                card(entities.first { it.id == chosenEntityId }, true)
            }
        } else {
            AppDataList(
                entities = entities,
                modifier = modifier.heightIn(Dp.Unspecified, maxHeight),
                contentPadding = contentPadding,
                card = { card(it, false) }
            )
        }
    }
}

@Composable
inline fun AppDataMultiChoosingList(
    entities: List<DataEntity>,
    chosenEntityIds: Set<Int>,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline card: @Composable (DataEntity, Boolean) -> Unit
) {
    AppDataList(
        entities = entities,
        modifier = modifier.heightIn(Dp.Unspecified, maxHeight),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        card = { card(it, chosenEntityIds.contains(it.id)) }
    )
}

@Composable
fun AppYesNoRadioGroup(
    isSelectedYes: Boolean?,
    onSelect: (Boolean) -> Unit,
    testTagSuffix: String = ""
) {
    AppRadioGroup(
        options = listOf(true, false),
        selectedOption = isSelectedYes,
        onSelect = onSelect,
        optionDescriptionRes = { if (it) R.string.yes else R.string.no },
        testTagSuffix = testTagSuffix
    )
}

@Composable
fun <T> AppRadioGroup(
    options: List<T>,
    selectedOption: T?,
    onSelect: (T) -> Unit,
    optionDescriptionRes: (T) -> Int,
    testTagSuffix: String = ""
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            AppRadioOption(
                descriptionRes = optionDescriptionRes(option),
                isSelected = selectedOption == option,
                onSelect = { onSelect(option) },
                testTagSuffix = testTagSuffix
            )
        }
    }
}

@Composable
fun AppRadioOption(
    @StringRes descriptionRes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTagSuffix: String = ""
) {
    val description = stringResource(descriptionRes)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = dimensionResource(R.dimen.padding_8dp))
            .testTag(description + testTagSuffix)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        AppMediumTitleText(
            text = description,
            overrideModifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_8dp),
                end = dimensionResource(R.dimen.padding_8dp)
            )
        )
    }
}

@Composable
fun <T> AppFilterChipGroup(
    options: List<T>,
    selectedOptions: Set<T>,
    onToggle: (T) -> Unit,
    optionText: @Composable (T) -> String
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.padding_8dp),
            Alignment.CenterHorizontally
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.padding_8dp))
    ) {
        options.forEach { option ->
            AppFilterChip(
                text = optionText(option),
                isSelected = option in selectedOptions,
                onClick = { onToggle(option) }
            )
        }
    }
}

@Composable
fun AppFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // the chip keeps its layout on the selection, otherwise the chips around it are moved,
    // so the icon is only hidden, and the text is centered by the offset, which doesn't affect the layout
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            AppMediumBodyText(
                text = text,
                modifier = when {
                    isSelected -> Modifier
                    else -> Modifier.offset(x = -FilterChipDefaults.IconSize / 2)
                }
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.check),
                contentDescription = null,
                modifier = Modifier
                    .size(FilterChipDefaults.IconSize)
                    .alpha(if (isSelected) 1f else 0f)
            )
        },
        modifier = modifier
    )
}

@Composable
fun AppSwitchOption(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                role = Role.Switch
            )
            .padding(horizontal = dimensionResource(R.dimen.padding_16dp))
    ) {
        AppMediumTitleText(text = text)
        Switch(
            checked = isChecked,
            onCheckedChange = null
        )
    }
}

// the slider is continuous, because the tick marks of every integer value overload the track,
// and its thumbs are near the screen edges, so they are excluded from the system back gesture
@Composable
fun AppRangeSlider(
    value: IntRange,
    valueRange: IntRange,
    onValueChange: (IntRange) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    RangeSlider(
        value = value.first.toFloat()..value.last.toFloat(),
        onValueChange = { onValueChange(it.start.roundToInt()..it.endInclusive.roundToInt()) },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        onValueChangeFinished = onValueChangeFinished,
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.padding_16dp))
            .systemGestureExclusion()
            .testTag(stringResource(R.string.range_slider))
    )
}

@Composable
inline fun AppList(
    entities: List<DataEntity>,
    content: @Composable (DataEntity) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_8dp))
    ) {
        entities.forEachIndexed { index, entity ->
            content(entity)
            if (index != entities.lastIndex)
                HorizontalDivider()
        }
    }
}

@Composable
inline fun AppEntityCard(
    entity: DataEntity,
    crossinline onEntityClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (DataEntity) -> Unit
) {
    Card(
        onClick = { onEntityClick(entity.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = { content(entity) }
    )
}

@Composable
inline fun AppExpandedEntityCard(
    entity: DataEntity,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (DataEntity, Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        onClick = { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = { content(entity, isExpanded) }
    )
}