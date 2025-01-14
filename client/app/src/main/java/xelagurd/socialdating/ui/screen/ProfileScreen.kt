package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.dimensionResource
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
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onProfileStatisticsClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileUiState by profileViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(ProfileDestination.titleRes),
                internetStatus = profileUiState.internetStatus,
                refreshAction = profileViewModel::getUser,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = ProfileDestination.topLevelRoute
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
fun ProfileDetailsBody(
    user: User,
    onProfileStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppLargeBodyText(text = user.name)
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_very_small)))
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

@Preview(showBackground = true)
@Composable
private fun ProfileComponentPreview() {
    AppTheme {
        DataEntityComponent(
            dataEntityUiState = ProfileUiState(entity = FakeDataSource.users[0])
        ) {
            ProfileDetailsBody(
                user = it as User,
                onProfileStatisticsClick = { }
            )
        }
    }
}
