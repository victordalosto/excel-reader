package dalosto.excel.reader.internal.utils

import dalosto.excel.reader.internal.exception.ExcelReaderException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.CellType
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


private class ConversorBooleanUtilTest {

    @Test
    fun `should return true for BOOLEAN cell type with true`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.BOOLEAN
            on { asBoolean() } doReturn true
        }

        val result = booleanValue(cell)
        assertTrue(result)
    }


    @Test
    fun `should return false for BOOLEAN cell type with false`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.BOOLEAN
            on { asBoolean() } doReturn false
        }

        val result = booleanValue(cell)
        assertFalse(result)
    }


    @Test
    fun `should return true for supported true string values`() {
        val trueValues = listOf("true", "YES", "y", "Sim", "SiM", "1", "S", "verdadeiro", "v", "si", "sin", "positivo", "ok", "on", "Ativo")
        for (trueValue in trueValues) {
            val cell = mock<Cell> {
                on { type } doReturn CellType.STRING
                on { value } doReturn trueValue
            }

            assertTrue(booleanValue(cell), "Expected true for trueValue: $trueValue")
        }
    }


    @Test
    fun `should return false for supported false string values`() {
        val falseValues = listOf("false", "NO", "n", "0", "NÃO", "nao", "NA0", "f", "falso", "negativo", "desabilitado", "desligado", "off", "inativo")
        for (trueValue in falseValues) {
            val cell = mock<Cell> {
                on { type } doReturn CellType.STRING
                on { value } doReturn trueValue
            }

            assertFalse(booleanValue(cell), "Expected false for trueValue: $trueValue")
        }
    }


    @Test
    fun `should throw exception for unsupported string value`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn "maybe"
        }

        val exception = assertThrows<ExcelReaderException> {
            booleanValue(cell)
        }

        assertEquals("Cannot convert string value 'maybe' to Boolean.", exception.message)
    }


    @Test
    fun `should throw exception for unsupported cell type`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.NUMBER
            on { value } doReturn 123.0
        }

        val exception = assertThrows<ExcelReaderException> {
            booleanValue(cell)
        }

        assertEquals("Cannot convert cell value '123.0' to Boolean.", exception.message)
    }

}