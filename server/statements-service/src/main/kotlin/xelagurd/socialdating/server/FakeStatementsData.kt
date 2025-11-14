package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.details.StatementDetails

object FakeStatementsData {
    val statementsDetails = listOf(
        StatementDetails(
            text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 2
        ),
        StatementDetails(
            text = "RemoteStatement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement5", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 2
        ),
        StatementDetails(
            text = "RemoteStatement6", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 2
        )
    )

    val statements = listOf(
        Statement(
            id = 1, text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 2, text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 3, text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 2
        ),
        Statement(
            id = 4, text = "RemoteStatement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 5, text = "RemoteStatement5", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 2
        ),
        Statement(
            id = 6, text = "RemoteStatement6", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 2
        ),
        Statement(
            id = 7, text = "RemoteStatement7", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 8, text = "RemoteStatement8", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 9, text = "RemoteStatement9", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 10, text = "RemoteStatement10", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 11, text = "RemoteStatement11", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 12, text = "RemoteStatement12", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 13, text = "RemoteStatement13", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 14, text = "RemoteStatement14", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 15, text = "RemoteStatement15", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 16, text = "RemoteStatement16", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 17, text = "RemoteStatement17", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 18, text = "RemoteStatement18", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 19, text = "RemoteStatement19", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        )
    )

    fun List<Statement>.withNullIds() =
        this.map {
            Statement(
                text = it.text,
                isSupportDefiningTheme = it.isSupportDefiningTheme,
                definingThemeId = it.definingThemeId,
                creatorUserId = it.creatorUserId
            )
        }

}