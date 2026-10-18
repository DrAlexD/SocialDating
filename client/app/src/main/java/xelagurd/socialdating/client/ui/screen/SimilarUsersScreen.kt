package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DataUtils.SIMILAR_USERS_NEXT_PAGE_PREFETCH_COUNT
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.FILTER_AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DIFFERENCE_NUMBER_MIN
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.SimilarUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarUsersScreen(
    onSimilarUserClick: (Int, Set<Int>) -> Unit,
    similarUsersViewModel: SimilarUsersViewModel = hiltViewModel()
) {
    val similarUsersUiState by similarUsersViewModel.uiState.collectAsState()

    SimilarUsersScreenComponent(
        similarUsersUiState = similarUsersUiState,
        onSimilarUserClick = { onSimilarUserClick(it, similarUsersUiState.calculatedFilterDetails.categoryIds) },
        onValueChange = similarUsersViewModel::updateUiState,
        onCalculate = similarUsersViewModel::calculateSimilarUsers,
        onRefresh = similarUsersViewModel::refreshSimilarUsers,
        onLoadNextPage = similarUsersViewModel::getNextSimilarUsers,
        onNotificationShown = similarUsersViewModel::onNotificationShown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarUsersScreenComponent(
    similarUsersUiState: SimilarUsersUiState = SimilarUsersUiState(),
    onSimilarUserClick: (Int) -> Unit = {},
    onValueChange: (SimilarUsersFilterFormData) -> Unit = {},
    onCalculate: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadNextPage: () -> Unit = {},
    onNotificationShown: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val notificationHostState = remember { SnackbarHostState() }

    var isFilterSheetOpened by rememberSaveable { mutableStateOf(false) }

    NotificationEffect(
        notification = similarUsersUiState.notification,
        notificationHostState = notificationHostState,
        onNotificationShown = onNotificationShown
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(SimilarUsersDestination.titleRes),
                dataRequestStatus = similarUsersUiState.dataRequestStatus,
                scrollBehavior = scrollBehavior,
                actions = {
                    AppBadgedIconButton(
                        iconRes = R.drawable.filter_list,
                        contentDescription = stringResource(R.string.filters),
                        badgeNumber = similarUsersUiState.formData.activeFiltersNumber,
                        onClick = { isFilterSheetOpened = true }
                    )
                }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = SimilarUsersDestination.topLevelRoute
            )
        },
        floatingActionButton = {
            if (notificationHostState.currentSnackbarData == null) {
                AppExtendedFloatingActionButton(
                    text = stringResource(R.string.calculate),
                    iconRes = R.drawable.search,
                    onClick = onCalculate,
                    isEnabled = similarUsersUiState.formData.isValid
                )
            }
        },
        snackbarHost = { AppNotificationHost(notificationHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (similarUsersUiState.isCalculated()) {
            PagedDataListComponent(
                pagedDataListUiState = similarUsersUiState,
                onLoadNextPage = onLoadNextPage,
                onRefresh = onRefresh,
                contentPadding = innerPadding,
                prefetchCount = SIMILAR_USERS_NEXT_PAGE_PREFETCH_COUNT
            ) {
                AppEntityCard(
                    entity = it,
                    onEntityClick = onSimilarUserClick
                ) {
                    SimilarUserCardContent(
                        similarUser = it as SimilarUserDto
                    )
                }
            }
        } else {
            CalculationHint(contentPadding = innerPadding)
        }
    }

    if (isFilterSheetOpened) {
        AppModalBottomSheet(
            onDismissRequest = { isFilterSheetOpened = false }
        ) { hideSheet ->
            SimilarUsersFilterContent(
                categories = similarUsersUiState.categories,
                similarUsersFilterFormData = similarUsersUiState.formData,
                onValueChange = onValueChange,
                onDoneClick = hideSheet
            )
        }
    }
}

@Composable
private fun CalculationHint(contentPadding: PaddingValues) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = dimensionResource(R.dimen.padding_16dp))
    ) {
        AppLargeTitleText(
            text = stringResource(R.string.calculate_similar_users_hint),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SimilarUsersFilterContent(
    categories: List<Category>,
    similarUsersFilterFormData: SimilarUsersFilterFormData,
    onValueChange: (SimilarUsersFilterFormData) -> Unit,
    onDoneClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        AppLargeTitleText(text = stringResource(R.string.filters))
        if (categories.isNotEmpty()) {
            MultiChoosingComponent(
                titleRes = R.string.categories,
                options = categories,
                selectedOptions = categories.filter { it.id in similarUsersFilterFormData.categoryIds }.toSet(),
                onToggle = { onValueChange(similarUsersFilterFormData.toggleCategory(it.id)) },
                optionText = { it.name }
            )
        }
        TitledComponent(
            titleRes = R.string.similarity,
            hint = when (similarUsersFilterFormData.differenceRangeType) {
                DifferenceRangeType.DEFAULT -> stringResource(R.string.default_similarity_hint, DIFFERENCE_NUMBER_MIN)
                DifferenceRangeType.CUSTOM -> null
            }
        ) {
            AppFilterChipGroup(
                options = DifferenceRangeType.entries,
                selectedOptions = setOf(similarUsersFilterFormData.differenceRangeType),
                onToggle = { onValueChange(similarUsersFilterFormData.copy(differenceRangeType = it)) },
                optionText = { stringResource(it.descriptionRes) }
            )
            if (similarUsersFilterFormData.differenceRangeType == DifferenceRangeType.CUSTOM) {
                AppTextField(
                    value = similarUsersFilterFormData.minDifferenceNumber,
                    onValueChange = { onValueChange(similarUsersFilterFormData.copy(minDifferenceNumber = it)) },
                    label = stringResource(R.string.min_similarity),
                    keyboardType = KeyboardType.Number,
                    error = similarUsersFilterFormData.minDifferenceNumberError,
                    isError = similarUsersFilterFormData.minDifferenceNumberError != null ||
                            similarUsersFilterFormData.isEmptyDifferenceRange
                )
                AppTextField(
                    value = similarUsersFilterFormData.maxDifferenceNumber,
                    onValueChange = { onValueChange(similarUsersFilterFormData.copy(maxDifferenceNumber = it)) },
                    label = stringResource(R.string.max_similarity),
                    keyboardType = KeyboardType.Number,
                    error = similarUsersFilterFormData.maxDifferenceNumberError
                )
            }
        }
        MultiChoosingComponent(
            titleRes = R.string.gender,
            options = Gender.entries,
            selectedOptions = similarUsersFilterFormData.genders,
            onToggle = { onValueChange(similarUsersFilterFormData.toggleGender(it)) },
            optionText = { stringResource(it.descriptionRes) }
        )
        RangeChoosingComponent(
            titleRes = R.string.age,
            value = similarUsersFilterFormData.ageRange,
            valueRange = AGE_MIN..FILTER_AGE_MAX,
            isMaxUnlimited = true,
            onValueChange = { onValueChange(similarUsersFilterFormData.copy(ageRange = it)) }
        )
        MultiChoosingComponent(
            titleRes = R.string.purpose,
            // the users who search for everything at once are always displayed, so they are not filtered
            options = Purpose.entries - Purpose.ALL_AT_ONCE,
            selectedOptions = similarUsersFilterFormData.purposes,
            onToggle = { onValueChange(similarUsersFilterFormData.togglePurpose(it)) },
            optionText = { stringResource(it.descriptionRes) },
            hint = stringResource(R.string.all_at_once_always_displayed)
        )
        TitledComponent(titleRes = R.string.city) {
            AppSwitchOption(
                text = stringResource(R.string.only_my_city),
                isChecked = similarUsersFilterFormData.isSameCity,
                onCheckedChange = { onValueChange(similarUsersFilterFormData.copy(isSameCity = it)) }
            )
        }
        Row(horizontalArrangement = Arrangement.Center) {
            AppMediumTextCard(
                text = stringResource(R.string.reset),
                onClick = { onValueChange(SimilarUsersFilterFormData()) },
                isEnabled = similarUsersFilterFormData.activeFiltersNumber > 0
            )
            AppMediumTextCard(
                text = stringResource(R.string.done),
                onClick = onDoneClick
            )
        }
    }
}

@Composable
private fun SimilarUserCardContent(
    similarUser: SimilarUserDto
) {
    Box(contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                AvatarWithNameAndAge(
                    name = similarUser.name,
                    gender = similarUser.gender,
                    age = similarUser.age.toString()
                )
                CityWithPurpose(
                    city = similarUser.city,
                    purpose = similarUser.purpose
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(FULL_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_MAINTAIN.descriptionRes),
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = similarUser.similarNumber.toString())
                    Icon(
                        painter = painterResource(FULL_NO_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_NO_MAINTAIN.descriptionRes),
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = similarUser.oppositeNumber.toString())
                }
                AppSmallBodyText(text = similarUser.similarCategories.joinToString())
                AppSmallBodyText(text = similarUser.oppositeCategories.joinToString())
            }
        }
    }
}

@Composable
private fun AvatarWithNameAndAge(name: String, gender: Gender, age: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.padding_12dp)
        )
    ) {
        AvatarIcon(gender = gender)
        AppLargeTitleText(text = "$name, $age")
    }
}

@Composable
private fun AvatarIcon(gender: Gender) {
    Box(
        modifier = Modifier
            .size(15.dp)
            .background(gender.color, CircleShape)
    )
}

@Composable
private fun CityWithPurpose(city: String, purpose: Purpose) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.padding_16dp)
        )
    ) {
        AppLargeBodyText(
            text = "$city, ${stringResource(purpose.descriptionRes)}",
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentNotCalculatedPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(formData = FakeData.similarUsersFilterFormData)
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentInvalidFormPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(
                formData = FakeData.similarUsersFilterFormData.copy(minDifferenceNumber = "", maxDifferenceNumber = "")
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentDataPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(
                entities = FakeData.similarUsers,
                dataRequestStatus = RequestStatus.SUCCESS
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true, locale = "ru")
@Composable
private fun SimilarUsersComponentDataRuPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(
                entities = FakeData.similarUsers,
                dataRequestStatus = RequestStatus.SUCCESS
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentDataErrorPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(dataRequestStatus = RequestStatus.ERROR("Text"))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimilarUsersFilterContentPreview() {
    AppTheme {
        SimilarUsersFilterContent(
            categories = FakeData.categories,
            similarUsersFilterFormData = FakeData.similarUsersFilterFormData,
            onValueChange = {},
            onDoneClick = {}
        )
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun SimilarUsersFilterContentRuPreview() {
    AppTheme {
        SimilarUsersFilterContent(
            categories = FakeData.categories,
            similarUsersFilterFormData = FakeData.similarUsersFilterFormData,
            onValueChange = {},
            onDoneClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SimilarUsersFilterContentInvalidFormPreview() {
    AppTheme {
        SimilarUsersFilterContent(
            categories = FakeData.categories,
            similarUsersFilterFormData = FakeData.similarUsersFilterFormData.copy(maxDifferenceNumber = "-5"),
            onValueChange = {},
            onDoneClick = {}
        )
    }
}
