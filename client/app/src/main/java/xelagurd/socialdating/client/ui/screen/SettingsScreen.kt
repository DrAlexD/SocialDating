package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.SettingsDestination
import xelagurd.socialdating.client.ui.state.SettingsUiState
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSuccessLogout: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    SettingsScreenComponent(
        settingsUiState = settingsUiState,
        onSuccessLogout = onSuccessLogout,
        onLogoutClick = settingsViewModel::logout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenComponent(
    settingsUiState: SettingsUiState = SettingsUiState(),
    onSuccessLogout: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
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
        ComponentWithActionRequestStatus(
            actionRequestStatus = settingsUiState.actionRequestStatus,
            onSuccess = onSuccessLogout,
            contentPadding = innerPadding
        ) {
            SettingsDetailsBody(
                onLogoutClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun SettingsDetailsBody(
    onLogoutClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        AppLargeTextCard(
            text = stringResource(R.string.logout),
            onClick = onLogoutClick
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun SettingsComponentPreview() {
    AppTheme {
        SettingsScreenComponent()
    }
}