package dalosto.excel.reader.internal.utils

import dalosto.excel.reader.internal.exception.ExcelReaderException
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.CellType
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


private class ConversorNumberUtilTest {

    @Test
    fun `should return BigDecimal for NUMBER cell type`() {
        val expected = BigDecimal("123.45")
        val cell = mock<Cell> {
            on { type } doReturn CellType.NUMBER
            on { asNumber() } doReturn expected
        }

        val result = numberValue(cell)
        assertEquals(expected, result)
    }


    @Test
    fun `should parse valid STRING number cell type to BigDecimal`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn " 1234.56 "
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("1234.56"), result)
    }


    @Test
    fun `should parse valid STRING cell type to BigDecimal`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn " 1,234.56 "
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("1234.56"), result)
    }


    @Test
    fun `should parse valid STRING cell type with 3 dots to BigDecimal`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn " 1,234,567.89 "
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("1234567.89"), result)
    }


    @Test
    fun `should parse valid STRING cell type with multiple dots to BigDecimal`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn " 1,2.34,56.7.89 "
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("1234567.89"), result)
    }


    @Test
    fun `should parse STRING with scientific notation without numerator`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn "10^3"
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("1E3"), result)
    }


    @Test
    fun `should parse STRING with scientific notation`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn "3 10^3"
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("3E3"), result)
    }


    @Test
    fun `should parse STRING with monetary in string`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn "U$ 123,00"
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("123.00"), result)
    }


    @Test
    fun `should parse STRING with some garbage string`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.STRING
            on { value } doReturn "R$ 123,00 ++"
        }

        val result = numberValue(cell)
        assertEquals(BigDecimal("123.00"), result)
    }


    @Test
    fun `should throw exception for unsupported cell type`() {
        val cell = mock<Cell> {
            on { type } doReturn CellType.BOOLEAN
            on { value } doReturn true
        }

        val exception = assertThrows(ExcelReaderException::class.java) {
            numberValue(cell)
        }
        assertTrue(exception.message!!.contains("Cannot convert cell value"))
    }

}