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
    val headerName : String,


    /**
     * Indicates whether this field is required during processing.
     * If set to true, missing or null values for this field will result in an error.
     *
     * @return true if all field values are mandatory to be present in the Excel; false otherwise
     */
    val required : Boolean = false,


    /**
     * Determines whether the header name must exactly match the field name during comparison.
     * This setting is relevant only when using EXCEL_EXACT_STRING_HEADER_COMPARISON = false
     * If set to true, the comparison will be headerName.equals(EXCEL_TITLE_NAME)
     * If set to false, the comparison will be headerName.contains(EXCEL_TITLE_NAME)
     * @see dalosto.excel.reader.api.ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON
     */
    val exactHeaderMatch : Boolean = false

)