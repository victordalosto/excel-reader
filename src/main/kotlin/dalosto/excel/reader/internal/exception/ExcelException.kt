package dalosto.excel.reader.internal.exception

/**
 * Custom exception to handle errors specific to Excel reading operations.
 */
internal class ExcelReaderException : Exception {

    constructor(message: String? = null) : super(message)

    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause)

}
