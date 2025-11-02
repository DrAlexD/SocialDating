package xelagurd.socialdating.client.ui.screen

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.navigation.RegistrationDestination
import xelagurd.socialdating.client.ui.state.RegistrationUiState
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.RegistrationViewModel

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
    onValueChange: (RegistrationFormData) -> Unit = {},
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
                registrationFormData = registrationUiState.formData,
                onValueChange = onValueChange,
                onRegisterClick = onRegisterClick
            )
        }
    }
}

@Composable
private inline fun RegistrationDetailsBody(
    registrationFormData: RegistrationFormData,
    crossinline onValueChange: (RegistrationFormData) -> Unit,
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
                value = registrationFormData.username,
                onValueChange = { onValueChange(registrationFormData.copy(username = it)) },
                label = stringResource(R.string.username),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
            AppTextField(
                value = registrationFormData.name,
                onValueChange = { onValueChange(registrationFormData.copy(name = it)) },
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
            AppMediumTitleText(
                text = stringResourceWithColon(R.string.gender),
                overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
            )
            Gender.entries.forEach {
                RadioButton(
                    selected = registrationFormData.gender == it,
                    onClick = { onValueChange(registrationFormData.copy(gender = it)) },
                    modifier = Modifier.testTag(stringResource(it.descriptionRes))
                )
                AppMediumTitleText(
                    text = stringResource(it.descriptionRes),
                    overrideModifier = Modifier.padding(
                        end = dimensionResource(R.dimen.padding_very_small)
                    )
                )
            }
        }
        AppTextField(
            value = registrationFormData.email,
            onValueChange = { onValueChange(registrationFormData.copy(email = it)) },
            label = stringResource(R.string.email_optional),
            overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = registrationFormData.age,
                onValueChange = { onValueChange(registrationFormData.copy(age = it)) },
                label = stringResource(R.string.age),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 - dimensionResource(R.dimen.padding_very_small))
            )
            AppTextField(
                value = registrationFormData.city,
                onValueChange = { onValueChange(registrationFormData.copy(city = it)) },
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
            AppMediumTitleText(
                text = stringResourceWithColon(R.string.purpose),
                overrideModifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
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
                            selected = registrationFormData.purpose == it,
                            onClick = { onValueChange(registrationFormData.copy(purpose = it)) },
                            modifier = Modifier.testTag(stringResource(it.descriptionRes))
                        )
                        AppMediumTitleText(
                            text = stringResource(it.descriptionRes),
                            overrideModifier = Modifier.padding(
                                end = dimensionResource(R.dimen.padding_very_small)
                            )
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = registrationFormData.password,
                onValueChange = { onValueChange(registrationFormData.copy(password = it)) },
                label = stringResource(R.string.password),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 35.dp)
            )
            AppTextField(
                value = registrationFormData.repeatedPassword,
                onValueChange = { onValueChange(registrationFormData.copy(repeatedPassword = it)) },
                label = stringResource(R.string.repeat_password),
                overrideModifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_very_small))
                    .width(TextFieldDefaults.MinWidth / 2 + 35.dp)
            )
        }
        AppLargeTextCard(
            isEnabled = registrationFormData.isValid,
            text = stringResource(R.string.register),
            onClick = onRegisterClick
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun RegistrationComponentEmptyFormPreview() {
    AppTheme {
        RegistrationScreenComponent()
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun RegistrationComponentFullFormPreview() {
    AppTheme {
        RegistrationScreenComponent(
            registrationUiState = RegistrationUiState(
                formData = RegistrationFormData(
                    "name", Gender.MALE, "username", "password",
                    "password", "email", "40", "Moscow", Purpose.RELATIONSHIPS
                )
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true, locale = "ru")
@Composable
private fun RegistrationComponentFullFormRuPreview() {
    AppTheme {
        RegistrationScreenComponent(
            registrationUiState = RegistrationUiState(
                formData = RegistrationFormData(
                    "name", Gender.MALE, "username", "password",
                    "password", "email", "40", "Moscow", Purpose.RELATIONSHIPS
                )
            )
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun RegistrationComponentWrongDataPreview() {
    AppTheme {
        RegistrationScreenComponent(
            registrationUiState = RegistrationUiState(
                formData = RegistrationFormData(
                    "name", Gender.MALE, "username", "password",
                    "password", "email", "40", "Moscow", Purpose.RELATIONSHIPS
                ),
                actionRequestStatus = RequestStatus.ERROR("Text")
            )
        )
    }
}
