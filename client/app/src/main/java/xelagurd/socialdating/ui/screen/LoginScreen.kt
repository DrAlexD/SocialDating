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
import androidx.compose.material3.TextField
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
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.additional.LoginDetails
import xelagurd.socialdating.ui.navigation.LoginDestination
import xelagurd.socialdating.ui.state.LoginUiState
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
        LoginBody(
            loginUiState = loginUiState,
            onValueChange = loginViewModel::updateUiState,
            onLoginClick = loginViewModel::login,
            onSuccessLogin = onSuccessLogin,
            onRegistrationClick = onRegistrationClick,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun LoginBody(
    loginUiState: LoginUiState,
    onValueChange: (LoginDetails) -> Unit,
    onLoginClick: () -> Unit,
    onSuccessLogin: () -> Unit,
    onRegistrationClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier.padding(contentPadding)) {
        LoginDetails(
            loginDetails = loginUiState.loginDetails,
            onValueChange = onValueChange,
            onLoginClick = onLoginClick,
            onRegistrationClick = onRegistrationClick,
            modifier = modifier
        )
        LoginStatus(
            requestStatus = loginUiState.requestStatus,
            onSuccessLogin = onSuccessLogin,
            modifier = modifier
        )
    }
}

@Composable
private fun LoginDetails(
    loginDetails: LoginDetails,
    onValueChange: (LoginDetails) -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        TextField(
            value = loginDetails.username,
            onValueChange = { onValueChange(loginDetails.copy(username = it)) },
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        )
        TextField(
            value = loginDetails.password,
            onValueChange = { onValueChange(loginDetails.copy(password = it)) },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        )
        Card(
            enabled = loginDetails.isValid,
            onClick = onLoginClick,
            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
        Text(
            text = stringResource(R.string.or),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_large))
        )
        Card(
            onClick = onRegistrationClick,
            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun LoginStatus(
    requestStatus: RequestStatus,
    onSuccessLogin: () -> Unit,
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
                    text = stringResource(R.string.no_internet_connection),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.FAILED -> {
                Text(
                    text = stringResource(R.string.failed_login),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.SUCCESS -> onSuccessLogin.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginDetailsPreview() {
    AppTheme {
        LoginDetails(
            loginDetails = LoginDetails(),
            onValueChange = {},
            onLoginClick = {},
            onRegistrationClick = {}
        )
    }
}