package xelagurd.socialdating.client.ui.viewmodel

import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.local.repository.LocalCategoriesRepository
import xelagurd.socialdating.client.data.remote.repository.RemoteCategoriesRepository
import xelagurd.socialdating.client.ui.state.CategoriesUiState
import xelagurd.socialdating.client.ui.state.RequestStatus

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteRepository: RemoteCategoriesRepository,
    private val localRepository: LocalCategoriesRepository
) : ViewModel() {
    private val dataRequestStatusFlow = MutableStateFlow<RequestStatus>(RequestStatus.UNDEFINED)
    private val categoriesFlow = localRepository.getCategories().distinctUntilChanged()

    val uiState = combine(categoriesFlow, dataRequestStatusFlow) { categories, dataRequestStatus ->
        CategoriesUiState(
            entities = categories,
            dataRequestStatus = dataRequestStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = CategoriesUiState()
    )

    init {
        getCategories()
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                dataRequestStatusFlow.update { RequestStatus.LOADING }

                val remoteCategories = remoteRepository.getCategories()

                if (remoteCategories.isNotEmpty()) {
                    localRepository.insertCategories(remoteCategories)

                    dataRequestStatusFlow.update { RequestStatus.SUCCESS }
                } else {
                    dataRequestStatusFlow.update {
                        RequestStatus.FAILURE(
                            failureText = context.getString(R.string.no_data)
                        )
                    }
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException, is HttpException -> {
                        if (localRepository.getCategories().first().isEmpty()) {
                            localRepository.insertCategories(FakeDataSource.categories) // FixMe: remove after implementing server
                        }

                        dataRequestStatusFlow.update {
                            RequestStatus.ERROR(
                                errorText = context.getString(R.string.no_internet_connection)
                            )
                        }
                    }

                    else -> throw e
                }
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}