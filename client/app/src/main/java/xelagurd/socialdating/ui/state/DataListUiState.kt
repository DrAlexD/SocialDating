package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.DataEntity

interface DataListUiState {
    val entities: List<DataEntity>
    val internetStatus: InternetStatus
}