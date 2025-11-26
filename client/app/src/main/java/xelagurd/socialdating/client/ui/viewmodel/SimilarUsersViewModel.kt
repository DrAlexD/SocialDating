package xelagurd.socialdating.client.ui.viewmodel

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.additional.SimilarUserWithData
import xelagurd.socialdating.client.data.model.toSimilarUsersWithData
import xelagurd.socialdating.client.data.remote.repository.RemoteUserCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteUsersRepository
import xelagurd.socialdating.client.data.remote.safeApiCall
import xelagurd.socialdating.client.ui.navigation.SimilarUsersDestination
import xelagurd.socialdating.client.ui.navigation.TIMEOUT_MILLIS
import xelagurd.socialdating.client.ui.state.RequestStatus
import xelagurd.socialdating.client.ui.state.SimilarUsersUiState

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SimilarUsersViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val remoteUserCategoriesRepository: RemoteUserCategoriesRepository,
    private val remoteUsersRepository: RemoteUsersRepository
) : ViewModel() {
    private val userId: Int = checkNotNull(savedStateHandle[SimilarUsersDestination.userId])

    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val similarUsersFlow = MutableStateFlow<List<SimilarUserWithData>>(listOf())

    val uiState = combine(similarUsersFlow, dataRequestStatusFlow)
    { similarUsers, dataRequestStatus ->
        SimilarUsersUiState(
            entities = similarUsers,
            dataRequestStatus = dataRequestStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SimilarUsersUiState()
    )

    init {
        getSimilarUsers()
    }

    fun getSimilarUsers() {
        viewModelScope.launch {
            var globalStatus: RequestStatus = RequestStatus.LOADING

            dataRequestStatusFlow.update { globalStatus }

            val (remoteSimilarUsers, statusSimilarUsers) = safeApiCall(context) {
                remoteUserCategoriesRepository.getSimilarUsers(userId)
            }

            if (remoteSimilarUsers != null) {
                val remoteSimilarUsersIds = remoteSimilarUsers.map { it.id }
                val (remoteUsers, statusUsers) = safeApiCall(context) {
                    remoteUsersRepository.getUsers(remoteSimilarUsersIds)
                }

                if (remoteUsers != null) {
                    similarUsersFlow.update { remoteSimilarUsers.toSimilarUsersWithData(remoteUsers) }
                }

                globalStatus = statusUsers
            } else {
                globalStatus = statusSimilarUsers
            }

            if (globalStatus is RequestStatus.ERROR) { // FixMe: remove after adding server hosting
                similarUsersFlow.update { FakeData.similarUsers.toSimilarUsersWithData(FakeData.users) }
            }

            dataRequestStatusFlow.update { globalStatus }
        }
    }
}