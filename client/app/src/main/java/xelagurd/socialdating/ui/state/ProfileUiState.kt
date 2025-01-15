package xelagurd.socialdating.ui.state

import xelagurd.socialdating.data.model.User

data class ProfileUiState(
    override val entity: User? = null,
    override val internetStatus: InternetStatus = InternetStatus.LOADING
) : DataEntityUiState