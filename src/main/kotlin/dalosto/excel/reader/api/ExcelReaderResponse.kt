package dalosto.excel.reader.api

/**
 * Data class representing the response from the Excel reader.
 *
 * @property processedSheet Indicates if the sheet was processed successfully.
 * @property sheetName The name of the sheet that was processed.
 * @property data The data read from the Excel sheet.
 * @property exception Any exception that occurred during the reading process.
 */
data class ExcelReaderResponse<T> (
    val processedSheet: Boolean,
    val sheetName: String?,
    val data: List<T>?,
    val exception: Throwable? = null
)
