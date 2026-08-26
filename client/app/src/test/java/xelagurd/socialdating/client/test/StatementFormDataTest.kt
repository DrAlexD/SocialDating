package xelagurd.socialdating.client.test

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import xelagurd.socialdating.client.data.fake.FakeData

class StatementFormDataTest {

    private val statementFormData = FakeData.statementFormData

    @Test
    fun statementFormData_allData_isValid() {
        assertTrue(statementFormData.isValid)
    }

    @Test
    fun statementFormData_emptyData_isNotValid() {
        val emptyFormData = statementFormData.copy(
            text = "", isSupportDefiningTheme = null, definingThemeId = null, creatorUserId = null
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
    fun statementFormData_emptySupportDefiningTheme_isNotValid() {
        assertFalse(statementFormData.copy(isSupportDefiningTheme = null).isValid)
    }

    @Test
    fun statementFormData_emptyDefiningTheme_isNotValid() {
        assertFalse(statementFormData.copy(definingThemeId = null).isValid)
    }

    @Test
    fun statementFormData_emptyCreatorUser_isNotValid() {
        assertFalse(statementFormData.copy(creatorUserId = null).isValid)
    }
}