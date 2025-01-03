package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import xelagurd.socialdating.ui.navigation.ProfileDestination
import xelagurd.socialdating.ui.state.InternetStatus
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
                refreshAction = { profileViewModel.getUser() },
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
        ProfileBody(
            profileUiState = profileUiState,
            onProfileStatisticsClick = onProfileStatisticsClick,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
internal fun ProfileBody(
    profileUiState: ProfileUiState,
    onProfileStatisticsClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(modifier = modifier) {
        if (profileUiState.user != null) {
            ProfileDetails(
                user = profileUiState.user,
                onProfileStatisticsClick = { onProfileStatisticsClick.invoke(profileUiState.user.id) },
                modifier = modifier.padding(contentPadding)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                Text(
                    text = stringResource(
                        when (profileUiState.internetStatus) {
                            InternetStatus.ONLINE -> R.string.no_data
                            InternetStatus.LOADING -> R.string.loading
                            InternetStatus.OFFLINE -> R.string.no_internet_connection
                        }
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
                )
            }
        }
    }
}

@Composable
private fun ProfileDetails(
    user: User,
    onProfileStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            UserInfo(user)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Card(onClick = onProfileStatisticsClick) {
                Text(
                    text = stringResource(R.string.open_profile_statistics),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}

@Composable
private fun UserInfo(user: User) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_very_small))) {
        Text(
            text = "Никнейм:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Имя:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Возраст:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Город:",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Цель:",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_very_small))) {
        Text(
            text = user.nickname,
            style = MaterialTheme.typography.bodyLarge
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_very_small)))
            AvatarIcon(gender = user.gender)
        }
        Text(
            text = "${user.age}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = user.city,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = user.purpose,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AvatarIcon(gender: String) {
    val avatarColor = if (gender == "female") Color(0xFFFFC1E3) else Color(0xFFADD8E6)
    Box(
        modifier = Modifier
            .size(15.dp)
            .background(avatarColor, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileCardPreview() {
    AppTheme {
        ProfileDetails(
            user = FakeDataSource.users[0],
            onProfileStatisticsClick = {}
        )
    }
}
