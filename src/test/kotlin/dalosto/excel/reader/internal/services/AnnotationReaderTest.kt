package dalosto.excel.reader.internal.services

import dalosto.excel.reader.api.Excel
import dalosto.excel.reader.api.ExcelConfigurations
import dalosto.excel.reader.internal.exception.ExcelReaderException
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test


private class AnnotationReaderTest {

    @Test
    fun `should throw exception if class does not have Excel annotation`() {
        data class ClassWithoutExcelAnnotation(
            var name: String? = null,
            var age: Int? = null
        )

        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if Excel field name is empty or blank`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = " ")
            var name: String? = null,
        )

        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if Excel field name is empty or blank and index is lt 0`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = " " , columnIndex = -1)
            var name: String? = null,
        )

        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if Excel field name is duplicated`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "asd")
            var name: String? = null,
            @Excel(headerName = "asd")
            var name2: String? = null,
        )

        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if Excel field index is duplicated`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(columnIndex = 3)
            var name: String? = null,
            @Excel(columnIndex = 3)
            var name2: String? = null,
        )

        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should process compatible data class using header name`() {
        data class ExcelCompatibleDataClass(
            @Excel(headerName = "PrimitiveInt") var primitiveInt: Int = 0,
            @Excel(headerName = "String") var stringValue: String? = null,
            @Excel(headerName = "Boolean") var booleanValue: Boolean? = null,
            @Excel(headerName = "Bytez") var byteValue: Byte? = null,
            @Excel(headerName = "Shortz") var shortValue: Short? = null,
            @Excel(headerName = "Integerz") var integerValue: Int? = null,
            @Excel(headerName = "Longz") var longValue: Long? = null,
            @Excel(headerName = "Floatz") var floatValue: Float? = null,
            @Excel(headerName = "Doublez") var doubleValue: Double? = null,
            @Excel(headerName = "BigInteger") var bigIntegerValue: BigInteger? = null,
            @Excel(headerName = "BigDecimal") var bigDecimalValue: BigDecimal? = null,
            @Excel(headerName = "LocalDateTime") var localDateTimeValue: LocalDateTime? = null,
            @Excel(headerName = "DoubleObject") var doubleObject: Double? = null,
            @Excel(headerName = "IntegerObject") var integerObject: Int? = null,
            @Excel(headerName = "LongObject") var longObject: Long? = null
        )

        val fields = listExcelFields(ExcelCompatibleDataClass::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 15) { "Fields should have size 15" }
    }


    @Test
    fun `should process compatible data class using indexes`() {
        data class ExcelCompatibleDataClass(
            @Excel(columnIndex = 0) var primitiveInt: Int = 0,
            @Excel(columnIndex = 1) var stringValue: String? = null,
            @Excel(columnIndex = 2) var booleanValue: Boolean? = null,
            @Excel(columnIndex = 3) var byteValue: Byte? = null,
            @Excel(columnIndex = 4) var shortValue: Short? = null,
            @Excel(columnIndex = 5) var integerValue: Int? = null,
            @Excel(columnIndex = 6) var longValue: Long? = null,
            @Excel(columnIndex = 7) var floatValue: Float? = null,
            @Excel(columnIndex = 8) var doubleValue: Double? = null,
            @Excel(columnIndex = 9) var bigIntegerValue: BigInteger? = null,
            @Excel(columnIndex = 10) var bigDecimalValue: BigDecimal? = null,
            @Excel(columnIndex = 11) var localDateTimeValue: LocalDateTime? = null,
            @Excel(columnIndex = 12) var doubleObject: Double? = null,
            @Excel(columnIndex = 13) var integerObject: Int? = null,
            @Excel(columnIndex = 14) var longObject: Long? = null
        )

        val fields = listExcelFields(ExcelCompatibleDataClass::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 15) { "Fields should have size 15" }
    }


    @Test
    fun `should ignore from mapping, field that doesn't have Excel annotation`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name")
            var name: String? = null,
            var age: Int? = null
        )

        val fields = listExcelFields(ClassWithoutExcelAnnotation::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 1) { "Fields should have size 1" }
    }


    @Test
    fun `should throw exception if derived field name is duplicated`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name")
            var name: String? = null,
            @Excel(headerName = "SurName")
            var surName: String? = null,
        )
        ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON = false
        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if derived field name is duplicated and case insensitive`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name")
            var name: String? = null,
            @Excel(headerName = "SurName")
            var surName: String? = null,
        )
        ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON = true

        val fields = listExcelFields(ClassWithoutExcelAnnotation::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 2) { "Fields should have size 2" }
    }


    @Test
    fun `should throw exception if derived field name is duplicated with annotation`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name")
            var name: String? = null,
            @Excel(headerName = "SurName")
            var surName: String? = null,
        )
        ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON = false
        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }


    @Test
    fun `should throw exception if derived field name is duplicated and case insensitive with single annotation`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name", exactHeaderMatch = true)
            var name: String? = null,
            @Excel(headerName = "SurName")
            var surName: String? = null,
        )
        ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON = false

        val fields = listExcelFields(ClassWithoutExcelAnnotation::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 2) { "Fields should have size 2" }
    }


    @Test
    fun `should throw exception if derived field name is duplicated and case insensitive with annotation`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name", exactHeaderMatch = true)
            var name: String? = null,
            @Excel(headerName = "SurName", exactHeaderMatch = true)
            var surName: String? = null,
        )
        ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON = false

        val fields = listExcelFields(ClassWithoutExcelAnnotation::class.java)
        assert(fields.isNotEmpty()) { "Fields should not be empty" }
        assert(fields.size == 2) { "Fields should have size 2" }
    }


    @Test
    fun `should throw exception if field type is not supported`() {
        data class ClassWithoutExcelAnnotation(
            @Excel(headerName = "Name")
            var name: List<String>? = null,
        )
        assertFailsWith<ExcelReaderException> {
            listExcelFields(ClassWithoutExcelAnnotation::class.java)
        }
    }

}