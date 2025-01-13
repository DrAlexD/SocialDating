package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.ui.navigation.SettingsDestination
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.state.SettingsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSuccessLogout: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(SettingsDestination.titleRes)
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = SettingsDestination.topLevelRoute
            )
        }
    ) { innerPadding ->
        SettingsBody(
            settingsUiState = settingsUiState,
            onLogoutClick = settingsViewModel::logout,
            onSuccessLogout = onSuccessLogout,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun SettingsBody(
    settingsUiState: SettingsUiState,
    onLogoutClick: () -> Unit,
    onSuccessLogout: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier.padding(contentPadding)) {
        SettingsDetails(
            onLogoutClick = onLogoutClick,
            modifier = modifier
        )
        SettingsStatus(
            requestStatus = settingsUiState.requestStatus,
            onSuccessLogout = onSuccessLogout,
            modifier = modifier
        )
    }
}

@Composable
private fun SettingsDetails(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Card(
            onClick = onLogoutClick,
            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
private fun SettingsStatus(
    requestStatus: RequestStatus,
    onSuccessLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        when (requestStatus) {
            RequestStatus.UNDEFINED -> {}

            RequestStatus.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.ERROR -> {
                Text(
                    text = stringResource(R.string.unknown_error),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.FAILED -> {}

            RequestStatus.SUCCESS -> onSuccessLogout()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsDetailsPreview() {
    AppTheme {
        SettingsDetails(
            onLogoutClick = {}
        )
    }
}