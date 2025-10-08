package dalosto.excel.reader.api


/**
 * Configuration variables for Excel-related settings.
 * This class contains constants and settings used for this project.
 */
object ExcelConfigurations {

    /**
     * The maximum number of columns to scan in an Excel file.
     * This limit is used to prevent excessive data lookup and processing.
     */
    @JvmField
    var EXCEL_MAX_COLUMN_FINDER : Int = 500


    /**
     * The maximum number of rows to scan in an Excel file while attempting to detect the header row
     * If a valid header is not found within this number of rows, an exception will be thrown.
     */
    @JvmField
    var EXCEL_MAX_LINE_FINDER : Int = 100


    /**
     * Defines the maximum number of consecutive empty rows to scan in an Excel file
     * after the first row is read. This threshold helps prevent infinite loops when
     * processing files that contain large blocks of empty rows.
     * Once this limit is reached, scanning stops and the file is considered complete.
     */
    @JvmField
    var EXCEL_MAX_EMPTY_SEQUENTIAL_ROWS : Int = 150


    /**
     * Find header in common rows.
     * Number of consecutive rows with common content required to consider a row as the header.  <p>
     * e.g.: if the value is 3, it will look for 3 consecutive rows to mount the header.
     */
    @JvmField
    var EXCEL_HEADER_DETECTION_THRESHOLD: Int = 1


    /**
     * Flag to indicate whether to process sheets that are not visible in the Excel file.
     * This is used to determine if hidden sheets should be included in the processing.
     */
    @JvmField
    var EXCEL_PROCESS_HIDDEN_SHEETS : Boolean = false


    /**
     * Flag to indicate whether to treat certain undefined or placeholder values in cells as null.
     * If set to true, values such as "-", "N/A", "null", "" will be interpreted as null.
     * This is useful when parsing Excel data with inconsistent or placeholder values.
     * @see EXCEL_UNDEFINED_VALUES
     */
    @JvmField
    var EXCEL_TREAT_UNDEFINED_VALUES_AS_NULL: Boolean = false


    /**
     * A set of string values that are considered as undefined or empty.
     * These values are used to determine if a cell's value should be treated as null.
     * Those values are used when EXCEL_TREAT_UNDEFINED_VALUES_AS_NULL is set to true.
     */
    @JvmField
    var EXCEL_UNDEFINED_VALUES = setOf("-", "--", "---", "n/a", "n/d", "null", "undefined", "none",
                                       "\"", "\"\"", ".", ",", "?", "#div/0!")


    /**
     * Flag to indicate whether to perform an exact string comparison for Excel headers.
     * This is used to compare Excel headers with target field names.
     * If set to true, the comparison will be EXCEL_TITLE_NAME.equals(headerName).               <p>
     * If set to false, the comparison will be EXCEL_TITLE_NAME.contains(headerName).            <p>
     */
    @JvmField
    var EXCEL_EXACT_STRING_HEADER_COMPARISON : Boolean = false


    /**
     * Flag to indicate whether to normalize strings by replacing special accents.
     * This is used to compare Excel headers with target field names.
     * @see dalosto.excel.reader.internal.utils.normalizeString
     */
    @JvmField
    var STRING_NORMALIZER_UNACCENT : Boolean = true


    /**
     * Flag to indicate whether to normalize strings by using regex.
     * This is used to compare Excel headers with target field names.
     * @see dalosto.excel.reader.internal.utils.normalizeString
     */
    @JvmField
    var STRING_NORMALIZER_APPLY_REGEX : Boolean = true


    /**
     * Flag to indicate whether string normalization should be case-sensitive when comparing
     * Excel headers to target field names.
     * This is used to compare Excel headers with target field names.
     * @see dalosto.excel.reader.internal.utils.normalizeString
     */
    @JvmField
    var STRING_NORMALIZER_CONSIDER_CASE_SENSITIVE : Boolean = false


    /**
     * Regex pattern used in normalizeString to remove unwanted characters.
     * This is used to compare Excel headers with target field names.
     * @see dalosto.excel.reader.internal.utils.normalizeString
     */
    @JvmField
    var STRING_NORMALIZER_REGEX : String = "[^a-z0-9,.]"

}
