package xelagurd.socialdating.client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.navigation.AppNavHost
import xelagurd.socialdating.client.ui.navigation.CategoriesDestination
import xelagurd.socialdating.client.ui.navigation.LoginDestination
import xelagurd.socialdating.client.ui.navigation.initializeTopLevelDestinations
import xelagurd.socialdating.client.ui.navigation.topLevelDestinations
import xelagurd.socialdating.client.ui.screen.AppMediumTitleText
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme

private const val LABEL_MAX_LINES = 2

@Composable
fun SocialDatingApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        AppNavHost()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    dataRequestStatus: RequestStatus? = null,
    refreshAction: (() -> Unit)? = null,
    navigateUp: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            navigateUp?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            if (dataRequestStatus != null) {
                val onCardStatusClick = refreshAction.takeIf { dataRequestStatus.isAllowedDataRefresh() } ?: {}
                Card(onClick = onCardStatusClick) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppMediumTitleText(
                            text = stringResource(
                                when (dataRequestStatus) {
                                    RequestStatus.SUCCESS -> R.string.online
                                    RequestStatus.UNDEFINED, RequestStatus.LOADING -> R.string.loading
                                    is RequestStatus.FAILURE, is RequestStatus.ERROR -> R.string.offline
                                }
                            ),
                            overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_4dp))
                        )
                        if (dataRequestStatus.isAllowedDataRefresh()) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh),
                                modifier = Modifier.graphicsLayer {
                                    this.scaleX = 0.8f
                                    this.scaleY = 0.8f
                                }
                            )
                        }
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun AppBottomNavigationBar(
    currentTopLevelRoute: String,
    modifier: Modifier = Modifier
) {
    val labelHeight = with(LocalDensity.current) {
        MaterialTheme.typography.labelMedium.lineHeight.toDp() * LABEL_MAX_LINES
    }

    NavigationBar(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val isSelectedRoute = currentTopLevelRoute == item.navigationDestination.topLevelRoute
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelectedRoute) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.contentDescription)
                    )
                },
                label = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(labelHeight)
                    ) {
                        Text(
                            text = stringResource(item.labelRes),
                            textAlign = TextAlign.Center,
                            maxLines = LABEL_MAX_LINES
                        )
                    }
                },
                selected = isSelectedRoute,
                onClick = item.navigateTo,
                modifier = Modifier.testTag(stringResource(item.contentDescription))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
fun AppTopBarLoadingPreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            dataRequestStatus = RequestStatus.LOADING,
            refreshAction = {},
            navigateUp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
fun AppTopBarOfflinePreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            dataRequestStatus = RequestStatus.ERROR(),
            refreshAction = {},
            navigateUp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true, locale = "ru")
@Composable
fun AppTopBarOfflineRuPreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            dataRequestStatus = RequestStatus.ERROR(),
            refreshAction = {},
            navigateUp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
fun AppTopBarOnlinePreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            dataRequestStatus = RequestStatus.SUCCESS,
            refreshAction = {},
            navigateUp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
fun AppTopBarWithoutDataRequestStatusPreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(LoginDestination.titleRes)
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
fun AppBottomNavigationBarPreview() {
    initializeTopLevelDestinations(rememberNavController())

    AppTheme {
        Scaffold(
            bottomBar = {
                AppBottomNavigationBar(
                    currentTopLevelRoute = CategoriesDestination.topLevelRoute
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
        }
    }
}