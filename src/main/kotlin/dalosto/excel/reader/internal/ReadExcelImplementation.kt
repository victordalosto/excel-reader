package dalosto.excel.reader.internal

import dalosto.excel.reader.api.ExcelConfigurations
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_MAX_EMPTY_SEQUENTIAL_ROWS
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_MAX_LINE_FINDER
import dalosto.excel.reader.api.ExcelReaderResponse
import dalosto.excel.reader.internal.exception.ExcelReaderException
import dalosto.excel.reader.internal.models.ExcelFields
import dalosto.excel.reader.internal.models.RowLimitConditions
import dalosto.excel.reader.internal.services.DictionaryCellToField
import dalosto.excel.reader.internal.services.instantiate
import dalosto.excel.reader.internal.services.listExcelFields
import dalosto.excel.reader.internal.validation.checkIfClassHasNoArgsConstructor
import dalosto.excel.reader.internal.validation.rowsContainsContent
import java.io.File
import java.io.FileInputStream
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.dhatim.fastexcel.reader.Row
import org.dhatim.fastexcel.reader.Sheet
import org.dhatim.fastexcel.reader.SheetVisibility


/**
 * Reads the number of sheets in an Excel file.
 */
internal fun numberOfSheetsImpl(excelFile: File): Long {
    require(excelFile.exists() && excelFile.canRead()) { "Excel must exist and be readable" }

    return try {
        FileInputStream(excelFile).use { inputStream ->
            ReadableWorkbook(inputStream).use { workbook ->
                workbook.sheets.filter {
                    if (ExcelConfigurations.EXCEL_PROCESS_HIDDEN_SHEETS) {
                        true
                    } else {
                        it.visibility == SheetVisibility.VISIBLE
                    }
                }.count()
            }
        }

    } catch (e: Exception) {
        throw ExcelReaderException("Error reading the number of sheets", e)
    }
}



/**
 * Reads data from a specific sheet in an Excel file and maps it to instances of a class.
 */
internal fun <T> readExcelImpl(
        excelFile: File,
        clazz: Class<T>,
        sheetIndex: Int
): ExcelReaderResponse<T> {
    require(excelFile.exists() && excelFile.canRead()) { "Excel must exist and be readable" }
    checkIfClassHasNoArgsConstructor(clazz)

    var sheetName: String? = null

    return try {
        FileInputStream(excelFile).use { inputStream ->
            ReadableWorkbook(inputStream).use { workbook ->
                val sheet = workbook.sheets
                    .filter {
                        if (ExcelConfigurations.EXCEL_PROCESS_HIDDEN_SHEETS) {
                            true
                        } else {
                            it.visibility == SheetVisibility.VISIBLE
                        }
                    }
                    .filter { it.index == sheetIndex }
                    .findFirst()
                    .orElse(null)
                    ?: return ExcelReaderResponse(
                        processedSheet = false,
                        sheetName = null,
                        data = emptyList(),
                        exception = ExcelReaderException("Sheet at index $sheetIndex not found")
                    )

                sheetName = sheet.name
                val fields = listExcelFields(clazz)
                val data = processSheet(sheet, fields, clazz)

                return ExcelReaderResponse(
                    processedSheet = true,
                    sheetName = sheetName,
                    data = data,
                    exception = null
                )
            }
        }

    } catch (e: Exception) {
        ExcelReaderResponse(processedSheet = false,
                            sheetName = sheetName,
                            data = emptyList(),
                            exception = e
        )
    }
}



private fun <T> processSheet(
        sheet : Sheet,
        fields: List<ExcelFields>,
        clazz: Class<T>
): List<T> {
    val conditions = RowLimitConditions()

    val list = mutableListOf<T>()
    val cellToField = DictionaryCellToField(fields)

    for (row in sheet.openStream()) {
        try {
            val shouldBreak = processRow(list, cellToField, row, conditions, clazz)
            if (shouldBreak) {
                break
            }

        } catch (e: Exception) {
            throw ExcelReaderException("Error processing row: ${row.rowNum}", e)
        }

    }

    if (cellToField.headerWasNotFound()) {
        throw ExcelReaderException("Header not found in the sheet.")
    }

    return list
}



/**
 * @return true if the processing should stop (break), false otherwise
 */
private fun <T> processRow(
    list : MutableList<T>,
    cellToField : DictionaryCellToField,
    row : Row,
    conditions : RowLimitConditions,
    clazz : Class<T>,
) : Boolean {
    if (cellToField.headerWasNotFound()) {
        if (cellToField.isRowAHeader(row)) {
            // Header found. From now on, we will map the columns to fields
            // dictionaryCellToField will contain the mapping of column index to field
            return false

        } else {
            // Header still wasn't found, we increment the number of empty rows before header
            if (conditions.amountEmptyRowsUntilHeader++ > EXCEL_MAX_LINE_FINDER) {
                // If the number of empty rows is greater than the limit, we stop the processing
                throw ExcelReaderException(
                    "Header was not found in the first $EXCEL_MAX_LINE_FINDER rows."
                )
            }
        }

    } else {
        if (rowsContainsContent(row)) {
            // Process the row and generate a new instance of the class
            val map = cellToField.dictionaryColumnExcelIndexToField(row)
            val instance = instantiate(row, map, clazz)

            if (instance != null) {
                list.add(instance)
                conditions.amountEmptyRowsAfterHeader = 0
            }

        } else {
            // If the row is empty, we increase the number of empty rows.
            if (conditions.amountEmptyRowsAfterHeader++ > EXCEL_MAX_EMPTY_SEQUENTIAL_ROWS) {
                // we stop the processing gracefully
                return true
            }
        }
    }

    return false
}

