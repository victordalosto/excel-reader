package dalosto.excel.reader.internal.services

import dalosto.excel.reader.api.ExcelConfigurations
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON
import dalosto.excel.reader.internal.exception.ExcelReaderException
import dalosto.excel.reader.internal.models.ExcelFields
import dalosto.excel.reader.internal.utils.normalizeString
import org.dhatim.fastexcel.reader.Row

/**
 * ExcelSchema is a utility class that helps in mapping Excel rows to Java/Kotlin objects.
 * It checks if the header row is present and maps the columns Index to the corresponding fields
 * in the class.
 */
internal class DictionaryCellToField(private val fields: List<ExcelFields>) {

    private var headerFound = false
    lateinit var mapColumnExcelIndexToField : Map<Int, ExcelFields>


    fun headerWasNotFound() : Boolean {
        return !headerFound
    }


    /**
     * Checks if the current row is a header.
     * This method should be called before dictionaryColumnExcelIndexToField
     */
    fun isRowAHeader(row : Row) : Boolean {
        // Temporary map to store column index and corresponding field
        val tempMap = mutableMapOf<Int, ExcelFields>()

        for ((index, cell) in row.withIndex()) {
            if (index > ExcelConfigurations.EXCEL_MAX_COLUMN_FINDER) {
                break
            }

            val key = normalizeString(cell?.rawValue) ?: continue

            val matchingFields = fields
                .filter { field ->
                    val header = normalizeString(field.annotation.headerName) ?: return@filter false
                    if (EXCEL_EXACT_STRING_HEADER_COMPARISON || field.annotation.exactHeaderMatch) {
                        return@filter key == header
                    } else {
                        return@filter key.contains(header)
                    }
                }

            if (matchingFields.isEmpty()) {
                continue
            }

            if (matchingFields.size > 1) {
                throw ExcelReaderException("Multiple fields match the header: '${cell?.rawValue}'")
            }

            tempMap[cell!!.columnIndex] = matchingFields.first()
        }

        if (tempMap.size == fields.size) {
            if (headerFound) {
                throw ExcelReaderException("Header already found. No need to call this method.")
            }

            if (tempMap.size != tempMap.values.distinct().size) {
                throw ExcelReaderException("Possible duplicate headers found.")
            }

            mapColumnExcelIndexToField = tempMap.toMap()
            headerFound = true
        }

        return headerFound
    }


    fun dictionaryColumnExcelIndexToField(row: Row) : Map<Int, ExcelFields> {
        return mapColumnExcelIndexToField
    }

}
