package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.StatementDefiningTheme
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.NOT_SURE
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_NO_MAINTAIN

object FakeStatementsData {
    val statementsDetails = listOf(
        StatementDetails(
            text = "RemoteStatement1",
            definingThemes = listOf(DefiningThemeReactionDetails(1, true)),
            creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement2",
            definingThemes = listOf(DefiningThemeReactionDetails(1, true)),
            creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement3",
            definingThemes = listOf(DefiningThemeReactionDetails(2, true)),
            creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement4",
            definingThemes = listOf(DefiningThemeReactionDetails(2, true)),
            creatorUserId = 1
        ),
        StatementDetails(
            text = "RemoteStatement5",
            definingThemes = listOf(
                DefiningThemeReactionDetails(1, true),
                DefiningThemeReactionDetails(2, false)
            ),
            creatorUserId = 1
        )
    )

    val statements = listOf(
        Statement(id = 1, text = "RemoteStatement1", creatorUserId = 1),
        Statement(id = 2, text = "RemoteStatement2", creatorUserId = 1),
        Statement(id = 3, text = "RemoteStatement3", creatorUserId = 1),
        Statement(id = 4, text = "RemoteStatement4", creatorUserId = 1),
        Statement(id = 5, text = "RemoteStatement5", creatorUserId = 1),
        Statement(id = 6, text = "RemoteStatement6", creatorUserId = 1),
        Statement(id = 7, text = "RemoteStatement7", creatorUserId = 1),
        Statement(id = 8, text = "RemoteStatement8", creatorUserId = 1),
        Statement(id = 9, text = "RemoteStatement9", creatorUserId = 1),
        Statement(id = 10, text = "RemoteStatement10", creatorUserId = 1),
        Statement(id = 11, text = "RemoteStatement11", creatorUserId = 1),
        Statement(id = 12, text = "RemoteStatement12", creatorUserId = 1),
        Statement(id = 13, text = "RemoteStatement13", creatorUserId = 1),
        Statement(id = 14, text = "RemoteStatement14", creatorUserId = 1),
        Statement(id = 15, text = "RemoteStatement15", creatorUserId = 1),
        Statement(id = 16, text = "RemoteStatement16", creatorUserId = 1),
        Statement(id = 17, text = "RemoteStatement17", creatorUserId = 1),
        Statement(id = 18, text = "RemoteStatement18", creatorUserId = 1),
        Statement(id = 19, text = "RemoteStatement19", creatorUserId = 1),
        Statement(id = 20, text = "RemoteStatement20", creatorUserId = 1),
        Statement(id = 21, text = "RemoteStatement21", creatorUserId = 1),
        Statement(id = 22, text = "RemoteStatement22", creatorUserId = 1),
        Statement(id = 23, text = "RemoteStatement23", creatorUserId = 1),
        Statement(id = 24, text = "RemoteStatement24", creatorUserId = 1),
        Statement(id = 25, text = "RemoteStatement25", creatorUserId = 1),
        Statement(id = 26, text = "RemoteStatement26", creatorUserId = 1),
        Statement(id = 27, text = "RemoteStatement27", creatorUserId = 1),
        Statement(id = 28, text = "RemoteStatement28", creatorUserId = 1),
        Statement(id = 29, text = "RemoteStatement29", creatorUserId = 1),
        Statement(id = 30, text = "RemoteStatement30", creatorUserId = 1),
        Statement(id = 31, text = "RemoteStatement31", creatorUserId = 1),
        Statement(id = 32, text = "RemoteStatement32", creatorUserId = 1),
        Statement(id = 33, text = "RemoteStatement33", creatorUserId = 1),
        Statement(id = 34, text = "RemoteStatement34", creatorUserId = 1),
        Statement(id = 35, text = "RemoteStatement35", creatorUserId = 1),
        Statement(id = 36, text = "RemoteStatement36", creatorUserId = 1),
        Statement(id = 37, text = "RemoteStatement37", creatorUserId = 1),
        Statement(id = 38, text = "RemoteStatement38", creatorUserId = 1),
        Statement(id = 39, text = "RemoteStatement39", creatorUserId = 1),
        Statement(id = 40, text = "RemoteStatement40", creatorUserId = 1),
        Statement(id = 41, text = "RemoteStatement41", creatorUserId = 1),
        Statement(id = 42, text = "RemoteStatement42", creatorUserId = 1),
        Statement(id = 43, text = "RemoteStatement43", creatorUserId = 1),
        Statement(id = 44, text = "RemoteStatement44", creatorUserId = 1),
        Statement(id = 45, text = "RemoteStatement45", creatorUserId = 1),
        Statement(id = 46, text = "RemoteStatement46", creatorUserId = 1),
        Statement(id = 47, text = "RemoteStatement47", creatorUserId = 1),
        Statement(id = 48, text = "RemoteStatement48", creatorUserId = 1),
        Statement(id = 49, text = "RemoteStatement49", creatorUserId = 1),
        Statement(id = 50, text = "RemoteStatement50", creatorUserId = 1),
        Statement(id = 51, text = "RemoteStatement51", creatorUserId = 1),
        Statement(id = 52, text = "RemoteStatement52", creatorUserId = 1),
        Statement(id = 53, text = "RemoteStatement53", creatorUserId = 1),
        Statement(id = 54, text = "RemoteStatement54", creatorUserId = 1),
        Statement(id = 55, text = "RemoteStatement55", creatorUserId = 1),
        Statement(id = 56, text = "RemoteStatement56", creatorUserId = 1),
        Statement(id = 57, text = "RemoteStatement57", creatorUserId = 1),
        Statement(id = 58, text = "RemoteStatement58", creatorUserId = 1),
        Statement(id = 59, text = "RemoteStatement59", creatorUserId = 1),
        Statement(id = 60, text = "RemoteStatement60", creatorUserId = 1),
        Statement(id = 61, text = "RemoteStatement61", creatorUserId = 1),
        Statement(id = 62, text = "RemoteStatement62", creatorUserId = 1),
        Statement(id = 63, text = "RemoteStatement63", creatorUserId = 1),
        Statement(id = 64, text = "RemoteStatement64", creatorUserId = 1),
        Statement(id = 65, text = "RemoteStatement65", creatorUserId = 1)
    )

    val statementDefiningThemes = listOf(
        StatementDefiningTheme(id = 1, statementId = 1, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 2, statementId = 2, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 3, statementId = 3, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 4, statementId = 4, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 5, statementId = 5, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 6, statementId = 5, definingThemeId = 2, isSupportDefiningTheme = false),
        StatementDefiningTheme(id = 7, statementId = 6, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 8, statementId = 7, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 9, statementId = 8, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 10, statementId = 9, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 11, statementId = 10, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 12, statementId = 11, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 13, statementId = 12, definingThemeId = 1, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 14, statementId = 13, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 15, statementId = 14, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 16, statementId = 15, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 17, statementId = 16, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 18, statementId = 17, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 19, statementId = 18, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 20, statementId = 19, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 21, statementId = 20, definingThemeId = 2, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 22, statementId = 21, definingThemeId = 5, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 23, statementId = 21, definingThemeId = 8, isSupportDefiningTheme = false),
        StatementDefiningTheme(id = 24, statementId = 22, definingThemeId = 5, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 25, statementId = 23, definingThemeId = 5, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 26, statementId = 24, definingThemeId = 5, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 27, statementId = 25, definingThemeId = 5, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 28, statementId = 26, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 29, statementId = 27, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 30, statementId = 28, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 31, statementId = 29, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 32, statementId = 30, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 33, statementId = 31, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 34, statementId = 32, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 35, statementId = 33, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 36, statementId = 34, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 37, statementId = 35, definingThemeId = 8, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 38, statementId = 36, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 39, statementId = 37, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 40, statementId = 38, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 41, statementId = 39, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 42, statementId = 40, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 43, statementId = 41, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 44, statementId = 42, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 45, statementId = 43, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 46, statementId = 44, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 47, statementId = 45, definingThemeId = 9, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 48, statementId = 46, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 49, statementId = 47, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 50, statementId = 48, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 51, statementId = 49, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 52, statementId = 50, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 53, statementId = 51, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 54, statementId = 52, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 55, statementId = 53, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 56, statementId = 54, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 57, statementId = 55, definingThemeId = 11, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 58, statementId = 56, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 59, statementId = 57, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 60, statementId = 58, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 61, statementId = 59, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 62, statementId = 60, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 63, statementId = 61, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 64, statementId = 62, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 65, statementId = 63, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 66, statementId = 64, definingThemeId = 12, isSupportDefiningTheme = true),
        StatementDefiningTheme(id = 67, statementId = 65, definingThemeId = 12, isSupportDefiningTheme = true)
    )

    val statementsWithDefiningThemes = statements.map { statement ->
        statement.toStatementWithDefiningThemes(
            statementDefiningThemes.filter { it.statementId == statement.id }
        )
    }

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
                creatorUserId = it.creatorUserId
            )
        }

    fun List<StatementDefiningTheme>.toStatementDefiningThemesWithNullIds() =
        this.map {
            StatementDefiningTheme(
                statementId = it.statementId,
                definingThemeId = it.definingThemeId,
                isSupportDefiningTheme = it.isSupportDefiningTheme
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
