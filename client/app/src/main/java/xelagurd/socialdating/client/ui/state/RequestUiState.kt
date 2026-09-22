package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.ui.form.FormData

sealed interface RequestUiState {
    val notification: String?
}

sealed interface DataRequestUiState : RequestUiState {
    val dataRequestStatus: RequestStatus

    fun isDataExist(): Boolean
}

sealed interface DataListUiState : DataRequestUiState {
    val entities: List<DataEntity>

    override fun isDataExist() = entities.isNotEmpty()
}

sealed interface PagedDataListUiState : DataListUiState {
    val nextPageRequestStatus: RequestStatus
    val isLastPage: Boolean

    fun isAllowedNextPageLoading() =
        !isLastPage &&
                dataRequestStatus is RequestStatus.SUCCESS &&
                (nextPageRequestStatus is RequestStatus.UNDEFINED || nextPageRequestStatus is RequestStatus.SUCCESS)
}

sealed interface DataEntityUiState : DataRequestUiState {
    val entity: DataEntity?

    override fun isDataExist() = entity != null
}

sealed interface ActionRequestUiState : RequestUiState {
    val actionRequestStatus: RequestStatus
}

sealed interface FormUiState : ActionRequestUiState {
    val formData: FormData
}
