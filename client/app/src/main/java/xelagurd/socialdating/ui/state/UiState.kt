package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DataEntity
import xelagurd.socialdating.data.model.additional.FormDetails

sealed interface InternetUiState {
    val internetStatus: InternetStatus

    fun isDataExist(): Boolean
}

sealed interface DataListUiState : InternetUiState {
    val entities: List<DataEntity>

    override fun isDataExist(): Boolean {
        return entities.isNotEmpty()
    }
}

sealed interface DataEntityUiState : InternetUiState {
    val entity: DataEntity?

    override fun isDataExist(): Boolean {
        return entity != null
    }
}

sealed interface RequestUiState {
    val requestStatus: RequestStatus
}

sealed interface FormUiState : RequestUiState {
    val formDetails: FormDetails
}