@file:JvmName("ExcelReader")

package dalosto.excel.reader.api

import dalosto.excel.reader.internal.numberOfSheetsImpl
import dalosto.excel.reader.internal.readExcelImpl
import java.io.File


/**
 * Retrieves the number of sheets in an Excel file.
 *
 * @param excelFile The Excel file to analyze. Must exist and be readable.
 * @return The number of sheets in the Excel file.
 */
fun numberOfSheets(excelFile: File): Long = numberOfSheetsImpl(excelFile)



/**
 * Reads data from a specific sheet in an Excel file and maps it to instances of a class.
 *
 * @param T The type of objects to be created from the Excel data.
 * @param excelFile The Excel file to read. Must exist and be readable.
 * @param clazz The class type to which the Excel data will be mapped.
 * @param sheetIndex The zero-based index of the sheet to read.
 * @return An [ExcelReaderResponse] containing the parsed data or error information.
 */
fun <T> readExcel(excelFile : File, clazz : Class<T>, sheetIndex: Int) : ExcelReaderResponse<T>
        = readExcelImpl(excelFile, clazz, sheetIndex)
