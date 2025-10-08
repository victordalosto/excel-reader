package dalosto.excel.reader.internal.services

import dalosto.excel.reader.api.ExcelConfigurations
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON
import dalosto.excel.reader.internal.exception.ExcelReaderException
import dalosto.excel.reader.internal.models.CommonRows
import dalosto.excel.reader.internal.models.ExcelFields
import dalosto.excel.reader.internal.utils.normalizeString
import org.dhatim.fastexcel.reader.Row

/**
 * ExcelSchema is a utility class that helps in mapping Excel rows to Java/Kotlin objects.
 * It checks if the header row is present and maps the columns Index to the corresponding fields
 * in the class.
 */
internal class DictionaryCellToField(private val fields: List<ExcelFields>) {

    private val mapFieldWIthName = fields
        .filter { it.annotation.columnIndex < 0 }
        .groupBy { normalizeString(it.annotation.headerName) }

    private val mapColumnIndex = fields
        .filter { it.annotation.columnIndex >= 0 }
        .groupBy { it.annotation.columnIndex }

    private var headerFound = false
    lateinit var mapColumnExcelIndexToField : Map<Int, ExcelFields>


    fun headerWasNotFound() : Boolean {
        return !headerFound
    }


    /**
     * Checks if the current row is a header.
     * This method should be called before dictionaryColumnExcelIndexToField
     */
    fun isRowAHeader(row : Row, commonRows : CommonRows) : Boolean {
        // Temporary map to store column index and corresponding field
        commonRows.startNewRow()

        for ((index, cell) in row.withIndex()) {
            if (index > ExcelConfigurations.EXCEL_MAX_COLUMN_FINDER) {
                break
            }

            if (mapColumnIndex.contains(cell?.columnIndex)) {
                // Process based on column index
                commonRows.addValue(cell.columnIndex, mapColumnIndex[cell.columnIndex]?.first()
                                                                    ?: continue)
            } else {
                // Process based on headerName value
                val key = normalizeString(cell?.rawValue) ?: continue

                val matchingFields : Map<String?, List<ExcelFields>> =
                    mapFieldWIthName.filter { groupFields ->
                        val header = groupFields.key!!
                        val field = groupFields.value.first() // Values are unique (only one exists)

                        if (EXCEL_EXACT_STRING_HEADER_COMPARISON
                            || field.annotation.exactHeaderMatch
                        ) {
                            return@filter key == header
                        } else {
                            return@filter key.contains(header)
                        }
                    }

                if (matchingFields.isEmpty()) {
                    continue
                }

                if (matchingFields.size > 1) {
                    // Possible unmatched duplicate headers found
                    break
                }

                commonRows.addValue(cell.columnIndex, matchingFields.values.first().first())
            }

        }

        val mapHeaders = commonRows.getHeadersRowGroups()
        if (mapHeaders.size == fields.size) {
            if (headerFound) {
                throw ExcelReaderException("Header already found. No need to call this method.")
            }

            if (mapHeaders.size != mapHeaders.values.distinct().size) {
                throw ExcelReaderException("Possible duplicate headers found.")
            }

            mapColumnExcelIndexToField = mapHeaders.toMap()
            headerFound = true
        }

        return headerFound
    }


    fun dictionaryColumnExcelIndexToField() : Map<Int, ExcelFields> {
        return mapColumnExcelIndexToField
    }

}
