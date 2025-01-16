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
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.ui.navigation.LoginDestination
import xelagurd.socialdating.ui.state.LoginUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSuccessLogin: () -> Unit,
    onRegistrationClick: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginUiState by loginViewModel.uiState.collectAsState()

    LoginScreenComponent(
        loginUiState = loginUiState,
        onSuccessLogin = onSuccessLogin,
        onRegistrationClick = onRegistrationClick,
        onValueChange = loginViewModel::updateUiState,
        onLoginClick = loginViewModel::loginWithInput
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenComponent(
    loginUiState: LoginUiState = LoginUiState(),
    onSuccessLogin: () -> Unit = {},
    onRegistrationClick: () -> Unit = {},
    onValueChange: (LoginDetails) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(LoginDestination.titleRes)
            )
        }
    ) { innerPadding ->
        ComponentWithActionRequestStatus(
            actionRequestStatus = loginUiState.actionRequestStatus,
            onSuccess = onSuccessLogin,
            contentPadding = innerPadding
        ) {
            LoginDetailsBody(
                loginDetails = loginUiState.formDetails,
                onValueChange = onValueChange,
                onLoginClick = onLoginClick,
                onRegistrationClick = onRegistrationClick
            )
        }
    }
}

@Composable
private inline fun LoginDetailsBody(
    loginDetails: LoginDetails,
    crossinline onValueChange: (LoginDetails) -> Unit,
    noinline onLoginClick: () -> Unit,
    noinline onRegistrationClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
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
        LoginScreenComponent()
    }
}