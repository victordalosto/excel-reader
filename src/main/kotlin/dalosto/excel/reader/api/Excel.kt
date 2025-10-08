package dalosto.excel.reader.api

/**
 * Excel Field Annotation
 * Indicates that a field should be included in Excel processing.
 * This annotation is used to map object fields to columns in an Excel file.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Excel(

    /**
     * Specifies the column header name in the Excel file that corresponds to this field.
     */
    val headerName : String = "",


    /**
     * Specifies the column header index in the Excel file that corresponds to this field.
     * This is used as precedence over the header name.
     */
    val columnIndex : Int  = -1,


    /**
     * Determines whether the header name must exactly match the field name during comparison.
     * This setting is relevant only when using EXCEL_EXACT_STRING_HEADER_COMPARISON = false.
     * If set to true, the comparison will be EXCEL_TITLE_NAME.equals(headerName).               <p>
     * If set to false, the comparison will be EXCEL_TITLE_NAME.contains(headerName).            <p>
     * @see ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON
     */
    val exactHeaderMatch : Boolean = false

)
