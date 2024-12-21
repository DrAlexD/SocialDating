package xelagurd.socialdating

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.AutoMirrored
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import xelagurd.socialdating.ui.navigation.AppNavHost
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.state.Status
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
    currentStatus: Status,
    modifier: Modifier = Modifier,
    navigateUp: (() -> Unit)? = null,
    refreshAction: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            navigateUp?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            Card(onClick = refreshAction ?: {}) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            when (currentStatus) {
                                Status.ONLINE -> R.string.online
                                Status.LOADING -> R.string.loading
                                Status.OFFLINE -> R.string.offline
                            }
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
                    )
                    refreshAction?.let {
                        Icon(
                            imageVector = Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            modifier = Modifier.graphicsLayer {
                                this.scaleX = 0.8f
                                this.scaleY = 0.8f
                            }
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppTopBarOfflinePreview() {
    AppTheme {
        AppTopBar(
            title = stringResource(CategoriesDestination.titleRes),
            currentStatus = Status.OFFLINE,
            navigateUp = {},
            refreshAction = {}
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
            currentStatus = Status.ONLINE,
            navigateUp = {}
        )
    }
}