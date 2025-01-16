package xelagurd.socialdating.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.fake.toUserCategoriesWithData
import xelagurd.socialdating.data.fake.toUserDefiningThemesWithData
import xelagurd.socialdating.data.model.additional.UserCategoryWithData
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData
import xelagurd.socialdating.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.ProfileStatisticsViewModel

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
                internetStatus = profileStatisticsUiState.internetStatus,
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
                        .getOrDefault(entity.id, listOf()),
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
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppLargeTitleText(text = userCategory.categoryName)
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand_list)
                )
            }
        }
        if (isExpanded) {
            HorizontalDivider(color = Color.Black)
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
    HorizontalDivider()
    AppMediumTitleText(text = userDefiningTheme.definingThemeName)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppSmallTitleText(text = userDefiningTheme.definingThemeFromOpinion)
        LinearProgressIndicator(
            progress = { userDefiningTheme.value.toFloat() / 100 },
            modifier = Modifier.testTag(stringResource(R.string.progress_indicator))
        )
        AppSmallTitleText(text = userDefiningTheme.definingThemeToOpinion)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileStatisticsComponentPreview() {
    AppTheme {
        ProfileStatisticsScreenComponent(
            profileStatisticsUiState = ProfileStatisticsUiState(
                entities = FakeDataSource.userCategories.toUserCategoriesWithData(),
                entityIdToData = FakeDataSource.userDefiningThemes
                    .toUserDefiningThemesWithData()
                    .groupBy { it.userCategoryId }
            )
        )
    }
}