package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.state.ProfileUiState
import xelagurd.socialdating.ui.state.RequestStatus
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onProfileStatisticsClick: (Int) -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileUiState by profileViewModel.uiState.collectAsState()

    ProfileScreenComponent(
        profileUiState = profileUiState,
        onProfileStatisticsClick = onProfileStatisticsClick,
        refreshAction = profileViewModel::getUser
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenComponent(
    profileUiState: ProfileUiState = ProfileUiState(),
    onProfileStatisticsClick: (Int) -> Unit = {},
    refreshAction: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(ProfileDestination.titleRes),
                dataRequestStatus = profileUiState.dataRequestStatus,
                refreshAction = refreshAction,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = ProfileDestination.topLevelRoute
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataEntityComponent(
            dataEntityUiState = profileUiState,
            contentPadding = innerPadding
        ) {
            ProfileDetailsBody(
                user = it as User,
                onProfileStatisticsClick = { onProfileStatisticsClick(it.id) }
            )
        }
    }
}

@Composable
private fun ProfileDetailsBody(
    user: User,
    onProfileStatisticsClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            UserDetailsBody(user)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(1f)
        ) {
            AppMediumTextCard(
                text = stringResource(R.string.open_profile_statistics),
                onClick = onProfileStatisticsClick
            )
        }
    }
}

@Composable
private fun UserDetailsBody(user: User) {
    UserInfoHints()
    UserData(user)
}

@Composable
private fun UserInfoHints() {
    Column {
        AppLargeBodyText(text = stringResourceWithColon(R.string.username))
        AppLargeBodyText(text = stringResourceWithColon(R.string.name))
        AppLargeBodyText(text = stringResourceWithColon(R.string.age))
        AppLargeBodyText(text = stringResourceWithColon(R.string.city))
        AppLargeBodyText(text = stringResourceWithColon(R.string.purpose))
    }
}

@Composable
private fun UserData(user: User) {
    Column {
        AppLargeBodyText(text = user.username)
        UserNameWithAvatar(user)
        AppLargeBodyText(text = "${user.age}")
        AppLargeBodyText(text = user.city)
        AppLargeBodyText(text = stringResource(user.purpose.descriptionRes))
    }
}

@Composable
private fun UserNameWithAvatar(user: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AppLargeBodyText(text = user.name)
        AvatarIcon(gender = user.gender)
    }
}

@Composable
private fun AvatarIcon(gender: Gender) {
    Box(
        modifier = Modifier
            .size(15.dp)
            .background(gender.color, CircleShape)
    )
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun ProfileComponentAllDataPreview() {
    AppTheme {
        ProfileScreenComponent(
            profileUiState = ProfileUiState(entity = FakeDataSource.users[0])
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true, locale = "ru")
@Composable
private fun ProfileComponentAllDataRuPreview() {
    AppTheme {
        ProfileScreenComponent(
            profileUiState = ProfileUiState(entity = FakeDataSource.users[0])
        )
    }
}

@Preview(showBackground = true, device = "id:small_phone", showSystemUi = true)
@Composable
private fun ProfileComponentNoDataPreview() {
    AppTheme {
        ProfileScreenComponent(
            profileUiState = ProfileUiState(dataRequestStatus = RequestStatus.ERROR("Text"))
        )
    }
}
