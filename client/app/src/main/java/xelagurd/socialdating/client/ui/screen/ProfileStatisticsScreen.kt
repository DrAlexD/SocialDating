package xelagurd.socialdating.client.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.fake.toUserCategoriesWithData
import xelagurd.socialdating.client.data.fake.toUserDefiningThemeWithData
import xelagurd.socialdating.client.data.fake.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.client.data.model.additional.UserDefiningThemeWithData
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.client.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.ProfileStatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticsScreen(
    onNavigateUp: () -> Unit,
    profileStatisticsViewModel: ProfileStatisticsViewModel = hiltViewModel()
) {
    val profileStatisticsUiState by profileStatisticsViewModel.uiState.collectAsState()

    ProfileStatisticsScreenComponent(
        profileStatisticsUiState = profileStatisticsUiState,
        onNavigateUp = onNavigateUp,
        refreshAction = profileStatisticsViewModel::getProfileStatistics
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticsScreenComponent(
    profileStatisticsUiState: ProfileStatisticsUiState = ProfileStatisticsUiState(),
    onNavigateUp: () -> Unit = {},
    refreshAction: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(ProfileStatisticsDestination.titleRes),
                dataRequestStatus = profileStatisticsUiState.dataRequestStatus,
                refreshAction = refreshAction,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = ProfileStatisticsDestination.topLevelRoute
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataListComponent(
            dataListUiState = profileStatisticsUiState,
            contentPadding = innerPadding
        ) {
            AppExpandedEntityCard(
                entity = it
            ) { entity, isExpanded ->
                UserCategoryCardContent(
                    userCategory = entity as UserCategoryWithData,
                    userDefiningThemes = profileStatisticsUiState.entityIdToData
                        .getOrDefault(entity.categoryId, listOf()),
                    isExpanded = isExpanded
                )
            }
        }
    }
}

@Composable
private fun UserCategoryCardContent(
    userCategory: UserCategoryWithData,
    userDefiningThemes: List<UserDefiningThemeWithData>,
    isExpanded: Boolean
) {
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AppLargeTitleText(text = userCategory.categoryName)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand_list),
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                )
            }
        }
        if (isExpanded) {
            HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
            AppList(entities = userDefiningThemes) {
                UserDefiningThemeDetailsBody(userDefiningTheme = it as UserDefiningThemeWithData)
            }
        }
    }
}

@Composable
private fun UserDefiningThemeDetailsBody(
    userDefiningTheme: UserDefiningThemeWithData
) {
    AppMediumTitleText(
        text = userDefiningTheme.definingThemeName,
        overrideModifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small))
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
    ) {
        AppSmallTitleText(
            text = userDefiningTheme.definingThemeFromOpinion,
            overrideModifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
        LinearProgressIndicator(
            progress = { userDefiningTheme.value.toFloat() / 100 },
            modifier = Modifier.testTag(stringResource(R.string.progress_indicator))
        )
        AppSmallTitleText(
            text = userDefiningTheme.definingThemeToOpinion,
            overrideModifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun ProfileStatisticsComponentFewDataPreview() {
    AppTheme {
        ProfileStatisticsScreenComponent(
            profileStatisticsUiState = ProfileStatisticsUiState(
                entities = FakeDataSource.userCategories.toUserCategoriesWithData(),
                entityIdToData = FakeDataSource.userDefiningThemes
                    .toUserDefiningThemesWithData()
                    .groupBy { it.categoryId }
            )
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun ProfileStatisticsComponentNoDataPreview() {
    AppTheme {
        ProfileStatisticsScreenComponent(
            profileStatisticsUiState = ProfileStatisticsUiState(
                dataRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun UserDefiningThemeDetailsBodyPreview() {
    AppTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalDivider()
            UserDefiningThemeDetailsBody(
                userDefiningTheme = FakeDataSource.userDefiningThemes[0].toUserDefiningThemeWithData()
            )
            HorizontalDivider()
        }
    }
}