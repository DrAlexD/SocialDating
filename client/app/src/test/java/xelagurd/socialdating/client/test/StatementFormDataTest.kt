package xelagurd.socialdating.client.test

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import xelagurd.socialdating.client.data.fake.FakeData

class StatementFormDataTest {

    private val statementFormData = FakeData.statementFormData
    private val chosenDefiningThemeId = statementFormData.definingThemes.keys.first()
    private val notChosenDefiningThemeId = FakeData.definingThemes
        .first { !statementFormData.definingThemes.containsKey(it.id) }
        .id

    @Test
    fun statementFormData_allData_isValid() {
        assertTrue(statementFormData.isValid)
    }

    @Test
    fun statementFormData_emptyData_isNotValid() {
        val emptyFormData = statementFormData.copy(
            text = "", definingThemes = mapOf(), creatorUserId = null
        )

        assertFalse(emptyFormData.isValid)
    }

    @Test
    fun statementFormData_emptyText_isNotValid() {
        assertFalse(statementFormData.copy(text = "").isValid)
    }

    @Test
    fun statementFormData_blankText_isNotValid() {
        assertFalse(statementFormData.copy(text = " ").isValid)
    }

    @Test
    fun statementFormData_emptyDefiningThemes_isNotValid() {
        assertFalse(statementFormData.copy(definingThemes = mapOf()).isValid)
    }

    @Test
    fun statementFormData_definingThemeWithoutOpinion_isNotValid() {
        val formData = statementFormData.copy(
            definingThemes = statementFormData.definingThemes + (chosenDefiningThemeId to null)
        )

        assertFalse(formData.isValid)
    }

    @Test
    fun statementFormData_emptyCreatorUser_isNotValid() {
        assertFalse(statementFormData.copy(creatorUserId = null).isValid)
    }

    @Test
    fun toggleDefiningTheme_notChosenDefiningTheme_addsItWithoutOpinion() {
        val formData = statementFormData.toggleDefiningTheme(notChosenDefiningThemeId)

        assertTrue(formData.definingThemes.containsKey(notChosenDefiningThemeId))
        assertEquals(null, formData.definingThemes[notChosenDefiningThemeId])
        assertFalse(formData.isValid)
    }

    @Test
    fun toggleDefiningTheme_chosenDefiningTheme_removesIt() {
        val formData = statementFormData.toggleDefiningTheme(chosenDefiningThemeId)

        assertFalse(formData.definingThemes.containsKey(chosenDefiningThemeId))
        assertEquals(statementFormData.definingThemes.size - 1, formData.definingThemes.size)
    }

    @Test
    fun updateDefiningThemeOpinion_chosenDefiningTheme_updatesOnlyItsOpinion() {
        val previousOpinion = statementFormData.definingThemes[chosenDefiningThemeId]!!

        val formData = statementFormData
            .updateDefiningThemeOpinion(chosenDefiningThemeId, !previousOpinion)

        assertEquals(!previousOpinion, formData.definingThemes[chosenDefiningThemeId])
        assertEquals(statementFormData.definingThemes.size, formData.definingThemes.size)
        assertTrue(formData.isValid)
    }

    @Test
    fun toStatementDetails_allData_mapsEveryDefiningTheme() {
        val statementDetails = statementFormData.toStatementDetails()

        assertEquals(statementFormData.text, statementDetails.text)
        assertEquals(statementFormData.creatorUserId, statementDetails.creatorUserId)
        assertEquals(
            statementFormData.definingThemes,
            statementDetails.definingThemes.associate { it.definingThemeId to it.isSupportDefiningTheme }
        )
    }
}
