package xelagurd.socialdating.client.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastJoinToString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.fake.toSimilarUsersWithData
import xelagurd.socialdating.client.data.model.additional.SimilarUserWithData
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.client.ui.AppBottomNavigationBar
import xelagurd.socialdating.client.ui.AppTopBar
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState
import xelagurd.socialdating.client.ui.theme.AppTheme
import xelagurd.socialdating.client.ui.viewmodel.SimilarUsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarUsersScreen(
    onSimilarUserClick: (Int) -> Unit,
    similarUsersViewModel: SimilarUsersViewModel = hiltViewModel()
) {
    val similarUsersUiState by similarUsersViewModel.uiState.collectAsState()

    SimilarUsersScreenComponent(
        similarUsersUiState = similarUsersUiState,
        onSimilarUserClick = onSimilarUserClick,
        refreshAction = similarUsersViewModel::getSimilarUsers
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarUsersScreenComponent(
    similarUsersUiState: SimilarUsersUiState = SimilarUsersUiState(),
    onSimilarUserClick: (Int) -> Unit = {},
    refreshAction: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(SimilarUsersDestination.titleRes),
                dataRequestStatus = similarUsersUiState.dataRequestStatus,
                refreshAction = refreshAction,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = SimilarUsersDestination.topLevelRoute
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataListComponent(
            dataListUiState = similarUsersUiState,
            contentPadding = innerPadding
        ) {
            AppEntityCard(
                entity = it,
                onEntityClick = onSimilarUserClick
            ) {
                SimilarUserCardContent(
                    similarUser = it as SimilarUserWithData
                )
            }
        }
    }
}

@Composable
private fun SimilarUserCardContent(
    similarUser: SimilarUserWithData
) {
    Box(contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                AvatarWithNameAndAge(
                    name = similarUser.name,
                    gender = similarUser.gender,
                    age = similarUser.age.toString()
                )
                CityWithPurpose(
                    city = similarUser.city,
                    purpose = similarUser.purpose
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(FULL_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_MAINTAIN.descriptionRes),
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = similarUser.similarNumber.toString())
                    Icon(
                        painter = painterResource(FULL_NO_MAINTAIN.iconRes),
                        contentDescription = stringResource(FULL_NO_MAINTAIN.descriptionRes),
                        modifier = Modifier.graphicsLayer {
                            this.scaleX = 0.7f
                            this.scaleY = 0.7f
                        }
                    )
                    AppMediumTitleText(text = similarUser.oppositeNumber.toString())
                }
                AppSmallBodyText(text = similarUser.similarCategories.fastJoinToString())
                AppSmallBodyText(text = similarUser.oppositeCategories.fastJoinToString())
            }
        }
    }
}

@Composable
private fun AvatarWithNameAndAge(name: String, gender: Gender, age: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.padding_12dp)
        )
    ) {
        AvatarIcon(gender = gender)
        AppLargeTitleText(text = "$name, $age")
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

@Composable
private fun CityWithPurpose(city: String, purpose: Purpose) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.padding_16dp)
        )
    ) {
        AppLargeBodyText(
            text = "$city, ${stringResource(purpose.descriptionRes)}",
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentDataPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(entities = FakeData.similarUsers.toSimilarUsersWithData())
        )
    }
}

@Preview(showBackground = true, device = "id:medium_phone", showSystemUi = true)
@Composable
private fun SimilarUsersComponentNoDataPreview() {
    AppTheme {
        SimilarUsersScreenComponent(
            similarUsersUiState = SimilarUsersUiState(dataRequestStatus = RequestStatus.ERROR("Text"))
        )
    }
}
