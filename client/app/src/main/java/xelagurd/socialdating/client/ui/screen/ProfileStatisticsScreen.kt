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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarCategory
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarDefiningTheme
import xelagurd.socialdating.client.data.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.client.data.model.enums.SimilarityType.SIMILAR
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.client.data.model.toUserCategoriesWithData
import xelagurd.socialdating.client.data.model.toUserDefiningThemesWithData
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
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
                currentTopLevelRoute = ProfileStatisticsDestination.topLevelRoute,
                isCurrentUser = profileStatisticsUiState.userId == profileStatisticsUiState.anotherUserId
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
                val userCategory = entity as UserCategoryWithData
                UserCategoryCardContent(
                    userCategory = userCategory,
                    userDefiningThemes = profileStatisticsUiState.entityIdToData[userCategory.categoryId],
                    detailedSimilarCategory = profileStatisticsUiState.entitiesMask?.categories[userCategory.categoryId],
                    isExpanded = isExpanded
                )
            }
        }
    }
}

@Composable
private fun UserCategoryCardContent(
    userCategory: UserCategoryWithData,
    userDefiningThemes: List<UserDefiningThemeWithData>?,
    detailedSimilarCategory: DetailedSimilarCategory?,
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
                if (detailedSimilarCategory != null) {
                    Icon(
                        painter = painterResource(FULL_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_MAINTAIN.descriptionRes),
                        tint = detailedSimilarCategory.similarityType.takeIf { it == SIMILAR }?.color
                            ?: LocalContentColor.current,
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = detailedSimilarCategory.similarNumber.toString())
                    Icon(
                        painter = painterResource(FULL_NO_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_NO_MAINTAIN.descriptionRes),
                        tint = detailedSimilarCategory.similarityType.takeIf { it == OPPOSITE }?.color
                            ?: LocalContentColor.current,
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = detailedSimilarCategory.oppositeNumber.toString())
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.expand_list),
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_8dp))
                )
            }
        }
        if (isExpanded && userDefiningThemes != null && userDefiningThemes.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
            AppList(entities = userDefiningThemes) {
                val userDefiningTheme = it as UserDefiningThemeWithData
                UserDefiningThemeDetailsBody(
                    userDefiningTheme = userDefiningTheme,
                    detailedSimilarDefiningTheme = detailedSimilarCategory?.definingThemes[userDefiningTheme.definingThemeNumberInCategory]
                )
            }
        }
    }
}

@Composable
private fun UserDefiningThemeDetailsBody(
    userDefiningTheme: UserDefiningThemeWithData,
    detailedSimilarDefiningTheme: DetailedSimilarDefiningTheme?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_8dp))
    ) {
        AppMediumTitleText(
            text = userDefiningTheme.definingThemeName,
            overrideModifier = Modifier
        )
        if (detailedSimilarDefiningTheme != null) {
            val image = if (detailedSimilarDefiningTheme.similarityType == SIMILAR) FULL_MAINTAIN else FULL_NO_MAINTAIN
            Icon(
                painter = painterResource(image.iconRes),
                contentDescription = stringResource(image.descriptionRes),
                tint = detailedSimilarDefiningTheme.similarityType.color,
                modifier = Modifier.graphicsLayer {
                    this.scaleX = 0.7f
                    this.scaleY = 0.7f
                }
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_8dp))
    ) {
        AppSmallTitleText(
            text = userDefiningTheme.definingThemeFromOpinion,
            overrideModifier = Modifier.padding(end = dimensionResource(R.dimen.padding_8dp))
        )
        LinearProgressIndicator(
            progress = { userDefiningTheme.value.toFloat() / 100 },
            modifier = Modifier.testTag(stringResource(R.string.progress_indicator))
        )
        AppSmallTitleText(
            text = userDefiningTheme.definingThemeToOpinion,
            overrideModifier = Modifier.padding(start = dimensionResource(R.dimen.padding_8dp))
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun ProfileStatisticsComponentDataPreview() {
    AppTheme {
        ProfileStatisticsScreenComponent(
            profileStatisticsUiState = ProfileStatisticsUiState(
                entities = FakeData.userCategories.toUserCategoriesWithData(FakeData.categories),
                entityIdToData = FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)
                    .groupBy { it.categoryId },
                entitiesMask = null
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun ProfileStatisticsComponentDataWithSimilarityPreview() {
    AppTheme {
        ProfileStatisticsScreenComponent(
            profileStatisticsUiState = ProfileStatisticsUiState(
                entities = FakeData.userCategories.toUserCategoriesWithData(FakeData.categories),
                entityIdToData = FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)
                    .groupBy { it.categoryId },
                entitiesMask = FakeData.detailedSimilarUser
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
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
                userDefiningTheme = FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)[0],
                detailedSimilarDefiningTheme = null
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun UserDefiningThemeDetailsBodyWithSimilarityPreview() {
    AppTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalDivider()
            UserDefiningThemeDetailsBody(
                userDefiningTheme = FakeData.userDefiningThemes.toUserDefiningThemesWithData(FakeData.definingThemes)[0],
                detailedSimilarDefiningTheme = FakeData.detailedSimilarUser.categories[1]?.definingThemes[1]
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
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