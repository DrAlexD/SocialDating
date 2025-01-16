package xelagurd.socialdating.ui.screen

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
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.ui.navigation.SettingsDestination
import xelagurd.socialdating.ui.state.SettingsUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.SettingsViewModel

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
            failedText = "",
            errorText = stringResource(R.string.unknown_error),
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

@Preview(showBackground = true)
@Composable
private fun SettingsComponentPreview() {
    AppTheme {
        SettingsScreenComponent()
    }
}