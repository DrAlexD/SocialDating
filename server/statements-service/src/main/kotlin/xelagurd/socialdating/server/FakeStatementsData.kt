package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN

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

    val userStatementsDetails = listOf(
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 4),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 5),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 6),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 2, statementId = 5)
    )

    val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 4),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 1, statementId = 5),
        UserStatement(id = 4, reactionType = FULL_MAINTAIN, userId = 1, statementId = 6),
        UserStatement(id = 5, reactionType = FULL_MAINTAIN, userId = 2, statementId = 5)
    )

    val statementReactionDetails = StatementReactionDetails(
        userId = 1,
        statementId = 1,
        categoryId = 1,
        definingThemeId = 1,
        reactionType = FULL_MAINTAIN,
        isSupportDefiningTheme = true
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

    fun List<Statement>.findUnreacted(
        userId: Int,
        definingThemeIds: List<Int>,
        userStatements: List<UserStatement>
    ): List<Statement> {
        val expected = mutableListOf<Statement>()
        forEach { statement ->
            if (statement.definingThemeId in (definingThemeIds) &&
                userStatements.none { it.statementId == statement.id && it.userId == userId }
            ) {
                expected.add(statement)
            }
        }
        return expected.toList()
    }

    fun List<UserStatement>.filterByUserIdAndDefiningThemeIds(
        userId: Int,
        definingThemeIds: List<Int>,
        statements: List<Statement>
    ): List<UserStatement> {
        val expected = mutableListOf<UserStatement>()
        forEach { userStatement ->
            if (userStatement.userId == userId &&
                statements.first { it.id == userStatement.statementId }.definingThemeId in (definingThemeIds)
            ) {
                expected.add(userStatement)
            }
        }
        return expected.toList()
    }
}