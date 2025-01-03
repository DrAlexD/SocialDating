package xelagurd.socialdating.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.fake.toUserCategoriesWithData
import xelagurd.socialdating.data.fake.toUserCategoryWithData
import xelagurd.socialdating.data.fake.toUserDefiningThemeWithData
import xelagurd.socialdating.data.fake.toUserDefiningThemesWithData
import xelagurd.socialdating.data.model.UserCategoryWithData
import xelagurd.socialdating.data.model.UserDefiningThemeWithData
import xelagurd.socialdating.ui.navigation.ProfileStatisticsDestination
import xelagurd.socialdating.ui.state.InternetStatus
import xelagurd.socialdating.ui.state.ProfileStatisticsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.ProfileStatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    profileStatisticsViewModel: ProfileStatisticsViewModel = hiltViewModel()
) {
    val profileStatisticsUiState by profileStatisticsViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(ProfileStatisticsDestination.titleRes),
                internetStatus = profileStatisticsUiState.internetStatus,
                refreshAction = { profileStatisticsViewModel.getProfileStatistics() },
                navigateUp = { onNavigateUp.invoke() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = ProfileStatisticsDestination.topLevelRoute
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        ProfileStatisticsBody(
            profileStatisticsUiState = profileStatisticsUiState,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun ProfileStatisticsBody(
    profileStatisticsUiState: ProfileStatisticsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (profileStatisticsUiState.userCategories.isNotEmpty() &&
            profileStatisticsUiState.userCategoryToDefiningThemes.isNotEmpty()
        ) {
            ProfileStatisticsList(
                userCategories = profileStatisticsUiState.userCategories,
                userCategoryToDefiningThemes = profileStatisticsUiState.userCategoryToDefiningThemes,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(
                        when (profileStatisticsUiState.internetStatus) {
                            InternetStatus.ONLINE -> R.string.no_data
                            InternetStatus.LOADING -> R.string.loading
                            InternetStatus.OFFLINE -> R.string.no_internet_connection
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
                )
            }
        }
    }
}

@Composable
private fun ProfileStatisticsList(
    userCategories: List<UserCategoryWithData>,
    userCategoryToDefiningThemes: Map<Int, List<UserDefiningThemeWithData>>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = userCategories, key = { it.id }) {
            UserCategoryCard(
                userCategory = it,
                userDefiningThemes = userCategoryToDefiningThemes.getOrDefault(it.id, listOf()),
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun UserCategoryCard(
    userCategory: UserCategoryWithData,
    userDefiningThemes: List<UserDefiningThemeWithData>,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
        modifier = modifier
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
                    UserCategoryInfo(userCategory)
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExpandButton(
                        isExpanded = isExpanded,
                        onClick = { isExpanded = !isExpanded },
                    )
                }
            }
            if (isExpanded) {
                HorizontalDivider(color = Color.Black)
                UserDefiningThemesList(userDefiningThemes)
            }
        }
    }
}

@Composable
private fun UserCategoryInfo(
    userCategory: UserCategoryWithData,
    modifier: Modifier = Modifier
) {
    Text(
        text = userCategory.categoryName,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun ExpandButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = stringResource(R.string.expand_list)
        )
    }
}

@Composable
private fun UserDefiningThemesList(
    userDefiningThemes: List<UserDefiningThemeWithData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        userDefiningThemes.forEach {
            UserDefiningThemeInfo(
                userDefiningTheme = it,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun UserDefiningThemeInfo(
    userDefiningTheme: UserDefiningThemeWithData,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider()
        Text(
            text = userDefiningTheme.definingThemeName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_small),
                top = dimensionResource(R.dimen.padding_small)
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = userDefiningTheme.definingThemeFromOpinion,
                style = MaterialTheme.typography.titleSmall
            )
            LinearProgressIndicator(
                progress = { userDefiningTheme.value.toFloat() / 100 },
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.padding_small),
                        end = dimensionResource(R.dimen.padding_small),
                        bottom = dimensionResource(R.dimen.padding_small)
                    )
                    .testTag(stringResource(R.string.progress_indicator))
            )
            Text(
                text = userDefiningTheme.definingThemeToOpinion,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileStatisticsBodyOfflineDataPreview() {
    AppTheme {
        ProfileStatisticsBody(
            profileStatisticsUiState = ProfileStatisticsUiState(
                userCategories = FakeDataSource.userCategories.toUserCategoriesWithData(),
                userCategoryToDefiningThemes = FakeDataSource.userDefiningThemes
                    .toUserDefiningThemesWithData()
                    .groupBy { it.userCategoryId },
                internetStatus = InternetStatus.OFFLINE
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserCategoryCardPreview() {
    AppTheme {
        UserCategoryCard(
            userCategory = FakeDataSource.userCategories[0].toUserCategoryWithData(),
            userDefiningThemes = FakeDataSource.userDefiningThemes.toUserDefiningThemesWithData()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDefiningThemesListPreview() {
    AppTheme {
        UserDefiningThemesList(
            userDefiningThemes = FakeDataSource.userDefiningThemes.toUserDefiningThemesWithData()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDefiningThemeInfoPreview() {
    AppTheme {
        UserDefiningThemeInfo(
            userDefiningTheme = FakeDataSource.userDefiningThemes[0].toUserDefiningThemeWithData()
        )
    }
}
