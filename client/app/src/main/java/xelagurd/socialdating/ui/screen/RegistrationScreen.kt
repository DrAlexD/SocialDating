package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.details.RegistrationDetails
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose
import xelagurd.socialdating.ui.navigation.RegistrationDestination
import xelagurd.socialdating.ui.state.RegistrationUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onSuccessRegistration: () -> Unit,
    onNavigateUp: () -> Unit,
    registrationViewModel: RegistrationViewModel = hiltViewModel()
) {
    val registrationUiState by registrationViewModel.uiState.collectAsState()

    RegistrationScreenComponent(
        registrationUiState = registrationUiState,
        onSuccessRegistration = onSuccessRegistration,
        onNavigateUp = onNavigateUp,
        onValueChange = registrationViewModel::updateUiState,
        onRegisterClick = registrationViewModel::register
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenComponent(
    registrationUiState: RegistrationUiState = RegistrationUiState(),
    onSuccessRegistration: () -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onValueChange: (RegistrationDetails) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(RegistrationDestination.titleRes),
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ComponentWithActionRequestStatus(
            actionRequestStatus = registrationUiState.actionRequestStatus,
            onSuccess = onSuccessRegistration,
            contentPadding = innerPadding
        ) {
            RegistrationDetailsBody(
                registrationDetails = registrationUiState.formDetails,
                onValueChange = onValueChange,
                onRegisterClick = onRegisterClick
            )
        }
    }
}

@Composable
private inline fun RegistrationDetailsBody(
    registrationDetails: RegistrationDetails,
    crossinline onValueChange: (RegistrationDetails) -> Unit,
    noinline onRegisterClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = registrationDetails.username,
                onValueChange = { onValueChange(registrationDetails.copy(username = it)) },
                label = stringResource(R.string.username),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
            AppTextField(
                value = registrationDetails.name,
                onValueChange = { onValueChange(registrationDetails.copy(name = it)) },
                label = stringResource(R.string.name),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppMediumTitleText(text = stringResourceWithColon(R.string.gender))
            Gender.entries.forEach {
                RadioButton(
                    selected = registrationDetails.gender == it,
                    onClick = { onValueChange(registrationDetails.copy(gender = it)) },
                    modifier = Modifier.testTag(stringResource(it.descriptionRes))
                )
                AppMediumTitleText(stringResource(it.descriptionRes))
            }
        }
        AppTextField(
            value = registrationDetails.email,
            onValueChange = { onValueChange(registrationDetails.copy(email = it)) },
            label = stringResource(R.string.email_optional),
            overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = registrationDetails.age,
                onValueChange = { onValueChange(registrationDetails.copy(age = it)) },
                label = stringResource(R.string.age),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
            AppTextField(
                value = registrationDetails.city,
                onValueChange = { onValueChange(registrationDetails.copy(city = it)) },
                label = stringResource(R.string.city),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppMediumTitleText(text = stringResourceWithColon(R.string.purpose))
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
                        AppMediumTitleText(text = stringResource(it.descriptionRes))
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = registrationDetails.password,
                onValueChange = { onValueChange(registrationDetails.copy(password = it)) },
                label = stringResource(R.string.password),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 25.dp)
            )
            AppTextField(
                value = registrationDetails.repeatedPassword,
                onValueChange = { onValueChange(registrationDetails.copy(repeatedPassword = it)) },
                label = stringResource(R.string.repeat_password),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 25.dp)
            )
        }
        AppLargeTextCard(
            isEnabled = registrationDetails.isValid,
            text = stringResource(R.string.register),
            onClick = onRegisterClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationComponentPreview() {
    AppTheme {
        RegistrationScreenComponent()
    }
}
