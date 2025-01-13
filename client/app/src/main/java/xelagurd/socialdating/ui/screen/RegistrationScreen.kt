package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.additional.RegistrationDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.ui.navigation.RegistrationDestination
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onSuccessRegistration: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    registrationViewModel: RegistrationViewModel = hiltViewModel()
) {
    val registrationUiState by registrationViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(RegistrationDestination.titleRes),
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        RegistrationBody(
            registrationUiState = registrationUiState,
            onValueChange = registrationViewModel::updateUiState,
            onRegisterClick = registrationViewModel::register,
            onSuccessRegistration = onSuccessRegistration,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun RegistrationBody(
    registrationUiState: RegistrationUiState,
    onValueChange: (RegistrationDetails) -> Unit,
    onRegisterClick: () -> Unit,
    onSuccessRegistration: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(modifier.padding(contentPadding)) {
        RegistrationDetails(
            registrationDetails = registrationUiState.registrationDetails,
            onValueChange = onValueChange,
            onRegisterClick = onRegisterClick,
            modifier = modifier
        )
        RegistrationStatus(
            requestStatus = registrationUiState.requestStatus,
            onSuccessRegistration = onSuccessRegistration,
            modifier = modifier
        )
    }
}

@Composable
private fun RegistrationDetails(
    registrationDetails: RegistrationDetails,
    onValueChange: (RegistrationDetails) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = registrationDetails.username,
                onValueChange = { onValueChange(registrationDetails.copy(username = it)) },
                label = { Text(stringResource(R.string.username)) },
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(id = R.dimen.padding_very_small))
            )
            TextField(
                value = registrationDetails.name,
                onValueChange = { onValueChange(registrationDetails.copy(name = it)) },
                label = { Text(stringResource(R.string.name)) },
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(id = R.dimen.padding_very_small))
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResourceWithColon(R.string.gender),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_small))
            )
            Gender.entries.forEach {
                RadioButton(
                    selected = registrationDetails.gender == it,
                    onClick = { onValueChange(registrationDetails.copy(gender = it)) },
                    modifier = Modifier.testTag(stringResource(it.descriptionRes))
                )
                Text(
                    text = stringResource(it.descriptionRes),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        TextField(
            value = registrationDetails.email,
            onValueChange = { onValueChange(registrationDetails.copy(email = it)) },
            label = { Text(stringResource(R.string.email_optional)) },
            singleLine = true,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = registrationDetails.age,
                onValueChange = { onValueChange(registrationDetails.copy(age = it)) },
                label = { Text(stringResource(R.string.age)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(id = R.dimen.padding_very_small))
            )
            TextField(
                value = registrationDetails.city,
                onValueChange = { onValueChange(registrationDetails.copy(city = it)) },
                label = { Text(stringResource(R.string.city)) },
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(id = R.dimen.padding_very_small))
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResourceWithColon(R.string.purpose),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_small))
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Purpose.entries.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = registrationDetails.purpose == it,
                            onClick = { onValueChange(registrationDetails.copy(purpose = it)) },
                            modifier = Modifier.testTag(stringResource(it.descriptionRes))
                        )
                        Text(
                            text = stringResource(it.descriptionRes),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                value = registrationDetails.password,
                onValueChange = { onValueChange(registrationDetails.copy(password = it)) },
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 25.dp)
            )
            TextField(
                value = registrationDetails.repeatedPassword,
                onValueChange = { onValueChange(registrationDetails.copy(repeatedPassword = it)) },
                label = { Text(stringResource(R.string.repeat_password)) },
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 25.dp)
            )
        }
        Card(
            enabled = registrationDetails.isValid,
            onClick = onRegisterClick,
            elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
private fun RegistrationStatus(
    requestStatus: RequestStatus,
    onSuccessRegistration: () -> Unit,
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
                    text = stringResource(R.string.failed_registration),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                )
            }

            RequestStatus.SUCCESS -> onSuccessRegistration.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationDetailsPreview() {
    AppTheme {
        RegistrationDetails(
            registrationDetails = RegistrationDetails(),
            onValueChange = {},
            onRegisterClick = {}
        )
    }
}
