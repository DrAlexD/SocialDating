package xelagurd.socialdating

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import xelagurd.socialdating.ui.navigation.AppNavHost
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.navigation.topLevelDestinations
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme

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
                val onCardStatusClick =
                    refreshAction.takeIf { dataRequestStatus.isAllowedRefresh() } ?: {}
                Card(onClick = onCardStatusClick) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(
                                when (dataRequestStatus) {
                                    RequestStatus.SUCCESS -> R.string.online
                                    RequestStatus.UNDEFINED, RequestStatus.LOADING -> R.string.loading
                                    RequestStatus.FAILED, RequestStatus.ERROR -> R.string.offline
                                }
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
                        )
                        if (dataRequestStatus.isAllowedRefresh()) {
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
    NavigationBar(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val selectedRoute = currentTopLevelRoute == item.navigationDestination.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedRoute) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.contentDescription)
                    )
                },
                label = { Text(item.navigationDestination.route) },
                selected = selectedRoute,
                onClick = item.navigateTo,
                modifier = Modifier.testTag(stringResource(item.contentDescription))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppTopBarOfflinePreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            dataRequestStatus = RequestStatus.ERROR,
            refreshAction = {},
            navigateUp = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
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