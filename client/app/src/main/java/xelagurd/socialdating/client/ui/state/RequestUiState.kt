package xelagurd.socialdating.client.ui.state

import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.ui.form.FormData

sealed interface RequestUiState

sealed interface DataRequestUiState : RequestUiState {
    val dataRequestStatus: RequestStatus

    fun isDataExist(): Boolean
}

sealed interface DataListUiState : DataRequestUiState {
    val entities: List<DataEntity>

    override fun isDataExist(): Boolean {
        return entities.isNotEmpty()
    }
}

sealed interface DataEntityUiState : DataRequestUiState {
    val entity: DataEntity?

    override fun isDataExist(): Boolean {
        return entity != null
    }
}

sealed interface ActionRequestUiState : RequestUiState {
    val actionRequestStatus: RequestStatus
}

sealed interface FormUiState : ActionRequestUiState {
    val formData: FormData
}