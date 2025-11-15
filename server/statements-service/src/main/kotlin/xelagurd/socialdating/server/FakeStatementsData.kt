package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.NOT_SURE
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_NO_MAINTAIN

object FakeStatementsData {
    val statementsDetails = listOf(
        StatementDetails(
            text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement5", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
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
            id = 3, text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 4, text = "RemoteStatement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 5, text = "RemoteStatement5", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 6, text = "RemoteStatement6", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 7, text = "RemoteStatement7", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 8, text = "RemoteStatement8", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 9, text = "RemoteStatement9", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 10, text = "RemoteStatement10", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 11, text = "RemoteStatement11", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 12, text = "RemoteStatement12", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 13, text = "RemoteStatement13", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 14, text = "RemoteStatement14", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 15, text = "RemoteStatement15", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 16, text = "RemoteStatement16", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 17, text = "RemoteStatement17", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 18, text = "RemoteStatement18", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 19, text = "RemoteStatement19", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 20, text = "RemoteStatement20", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 21, text = "RemoteStatement21", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 22, text = "RemoteStatement22", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 23, text = "RemoteStatement23", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 24, text = "RemoteStatement24", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 25, text = "RemoteStatement25", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 26, text = "RemoteStatement26", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 27, text = "RemoteStatement27", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 28, text = "RemoteStatement28", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 29, text = "RemoteStatement29", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 30, text = "RemoteStatement30", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 31, text = "RemoteStatement31", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 32, text = "RemoteStatement32", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 33, text = "RemoteStatement33", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 34, text = "RemoteStatement34", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 35, text = "RemoteStatement35", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 36, text = "RemoteStatement36", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 37, text = "RemoteStatement37", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 38, text = "RemoteStatement38", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 39, text = "RemoteStatement39", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 40, text = "RemoteStatement40", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 41, text = "RemoteStatement41", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 42, text = "RemoteStatement42", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 43, text = "RemoteStatement43", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 44, text = "RemoteStatement44", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 45, text = "RemoteStatement45", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 46, text = "RemoteStatement46", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 47, text = "RemoteStatement47", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 48, text = "RemoteStatement48", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 49, text = "RemoteStatement49", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 50, text = "RemoteStatement50", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 51, text = "RemoteStatement51", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 52, text = "RemoteStatement52", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 53, text = "RemoteStatement53", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 54, text = "RemoteStatement54", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 55, text = "RemoteStatement55", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 56, text = "RemoteStatement56", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 57, text = "RemoteStatement57", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 58, text = "RemoteStatement58", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 59, text = "RemoteStatement59", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 60, text = "RemoteStatement60", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 61, text = "RemoteStatement61", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 62, text = "RemoteStatement62", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 63, text = "RemoteStatement63", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 64, text = "RemoteStatement64", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 65, text = "RemoteStatement65", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
    )

    val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 2),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 1, statementId = 3),
        UserStatement(id = 4, reactionType = FULL_MAINTAIN, userId = 1, statementId = 4),
        UserStatement(id = 5, reactionType = PART_NO_MAINTAIN, userId = 1, statementId = 5),
        UserStatement(id = 6, reactionType = FULL_MAINTAIN, userId = 1, statementId = 11),
        UserStatement(id = 7, reactionType = FULL_MAINTAIN, userId = 1, statementId = 12),
        UserStatement(id = 8, reactionType = FULL_MAINTAIN, userId = 1, statementId = 13),
        UserStatement(id = 9, reactionType = FULL_MAINTAIN, userId = 1, statementId = 14),
        UserStatement(id = 10, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 15),
        UserStatement(id = 11, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 26),
        UserStatement(id = 12, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 27),
        UserStatement(id = 13, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 28),
        UserStatement(id = 14, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 29),
        UserStatement(id = 15, reactionType = NOT_SURE, userId = 1, statementId = 30),
        UserStatement(id = 16, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 46),
        UserStatement(id = 17, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 47),
        UserStatement(id = 18, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 48),
        UserStatement(id = 19, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 49),
        UserStatement(id = 20, reactionType = FULL_NO_MAINTAIN, userId = 1, statementId = 50),
        UserStatement(id = 21, reactionType = FULL_MAINTAIN, userId = 2, statementId = 1),
        UserStatement(id = 22, reactionType = FULL_MAINTAIN, userId = 2, statementId = 2),
        UserStatement(id = 23, reactionType = FULL_MAINTAIN, userId = 2, statementId = 3),
        UserStatement(id = 24, reactionType = FULL_MAINTAIN, userId = 2, statementId = 4),
        UserStatement(id = 25, reactionType = PART_NO_MAINTAIN, userId = 2, statementId = 5),
        UserStatement(id = 26, reactionType = FULL_MAINTAIN, userId = 2, statementId = 11),
        UserStatement(id = 27, reactionType = FULL_MAINTAIN, userId = 2, statementId = 12),
        UserStatement(id = 28, reactionType = FULL_MAINTAIN, userId = 2, statementId = 13),
        UserStatement(id = 29, reactionType = FULL_MAINTAIN, userId = 2, statementId = 14),
        UserStatement(id = 30, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 15),
        UserStatement(id = 31, reactionType = FULL_MAINTAIN, userId = 2, statementId = 26),
        UserStatement(id = 32, reactionType = FULL_MAINTAIN, userId = 2, statementId = 27),
        UserStatement(id = 33, reactionType = FULL_MAINTAIN, userId = 2, statementId = 28),
        UserStatement(id = 34, reactionType = FULL_MAINTAIN, userId = 2, statementId = 29),
        UserStatement(id = 35, reactionType = NOT_SURE, userId = 2, statementId = 30),
        UserStatement(id = 36, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 46),
        UserStatement(id = 37, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 47),
        UserStatement(id = 38, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 48),
        UserStatement(id = 39, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 49),
        UserStatement(id = 40, reactionType = FULL_NO_MAINTAIN, userId = 2, statementId = 50),
        UserStatement(id = 41, reactionType = FULL_MAINTAIN, userId = 3, statementId = 1),
        UserStatement(id = 42, reactionType = FULL_MAINTAIN, userId = 3, statementId = 2),
        UserStatement(id = 43, reactionType = FULL_MAINTAIN, userId = 3, statementId = 5),
        UserStatement(id = 44, reactionType = FULL_MAINTAIN, userId = 3, statementId = 11),
        UserStatement(id = 45, reactionType = NOT_SURE, userId = 3, statementId = 12),
        UserStatement(id = 46, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 3),
        UserStatement(id = 47, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 4),
        UserStatement(id = 48, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 13),
        UserStatement(id = 49, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 14),
        UserStatement(id = 50, reactionType = PART_MAINTAIN, userId = 3, statementId = 15),
        UserStatement(id = 51, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 26),
        UserStatement(id = 52, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 27),
        UserStatement(id = 53, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 28),
        UserStatement(id = 54, reactionType = FULL_NO_MAINTAIN, userId = 3, statementId = 29),
        UserStatement(id = 55, reactionType = PART_MAINTAIN, userId = 3, statementId = 30),
        UserStatement(id = 56, reactionType = FULL_MAINTAIN, userId = 3, statementId = 46),
        UserStatement(id = 57, reactionType = FULL_MAINTAIN, userId = 3, statementId = 47),
        UserStatement(id = 58, reactionType = FULL_MAINTAIN, userId = 3, statementId = 48),
        UserStatement(id = 59, reactionType = FULL_MAINTAIN, userId = 3, statementId = 49),
        UserStatement(id = 60, reactionType = PART_MAINTAIN, userId = 3, statementId = 50)
    )

    fun List<Statement>.toStatementsWithNullIds() =
        this.map {
            Statement(
                text = it.text,
                isSupportDefiningTheme = it.isSupportDefiningTheme,
                definingThemeId = it.definingThemeId,
                creatorUserId = it.creatorUserId
            )
        }

    fun List<UserStatement>.toUserStatementsWithNullIds() =
        this.map {
            UserStatement(
                reactionType = it.reactionType,
                userId = it.userId,
                statementId = it.statementId
            )
        }

}