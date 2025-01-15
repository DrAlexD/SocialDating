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
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.ui.navigation.LoginDestination
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSuccessLogin: () -> Unit,
    onRegistrationClick: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginUiState by loginViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(LoginDestination.titleRes)
            )
        }
    ) { innerPadding ->
        ComponentWithRequestStatus(
            requestStatus = loginUiState.requestStatus,
            onSuccess = onSuccessLogin,
            failedText = stringResource(R.string.failed_login),
            errorText = stringResource(R.string.no_internet_connection),
            contentPadding = innerPadding
        ) {
            LoginDetailsBody(
                loginDetails = loginUiState.formDetails,
                onValueChange = loginViewModel::updateUiState,
                onLoginClick = loginViewModel::loginWithInput,
                onRegistrationClick = onRegistrationClick
            )
        }
    }
}

@Composable
fun LoginDetailsBody(
    loginDetails: LoginDetails,
    onValueChange: (LoginDetails) -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        AppTextField(
            value = loginDetails.username,
            onValueChange = { onValueChange(loginDetails.copy(username = it)) },
            label = stringResource(R.string.username)
        )
        AppTextField(
            value = loginDetails.password,
            onValueChange = { onValueChange(loginDetails.copy(password = it)) },
            label = stringResource(R.string.password)
        )
        AppLargeTextCard(
            isEnabled = loginDetails.isValid,
            text = stringResource(R.string.login),
            onClick = onLoginClick
        )
        AppMediumTitleText(text = stringResource(R.string.or))
        AppMediumTextCard(
            text = stringResource(R.string.register),
            onClick = onRegistrationClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginComponentPreview() {
    AppTheme {
        ComponentWithRequestStatus(
            requestStatus = RequestStatus.UNDEFINED,
            onSuccess = { },
            failedText = stringResource(R.string.failed_login),
            errorText = stringResource(R.string.no_internet_connection)
        ) {
            LoginDetailsBody(
                loginDetails = LoginDetails(),
                onValueChange = { },
                onLoginClick = { },
                onRegistrationClick = { }
            )
        }
    }
}