package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DataEntity

sealed interface UiState {
    val internetStatus: InternetStatus

    fun isDataExist(): Boolean
}

sealed interface DataListUiState : UiState {
    val entities: List<DataEntity>

    override fun isDataExist(): Boolean {
        return entities.isNotEmpty()
    }
}

sealed interface DataEntityUiState : UiState {
    val entity: DataEntity?

    override fun isDataExist(): Boolean {
        return entity != null
    }
}