package dalosto.excel.reader.internal.services

import dalosto.excel.reader.api.Excel
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_EXACT_STRING_HEADER_COMPARISON
import dalosto.excel.reader.internal.exception.ExcelReaderException
import dalosto.excel.reader.internal.models.ExcelFields
import dalosto.excel.reader.internal.utils.normalizeString
import dalosto.excel.reader.internal.validation.isFieldConvertableFromExcel


/**
 * Utility function to extract fields from a class annotated with ExcelField.
 * Obs: This project is not using Kotlin reflection but Java reflection due to compatibility
 *
 * @return a map of ExcelField to its field in the class
 * @throws ExcelReaderException if any validation fails
 */
internal fun <T> listExcelFields(clazz : Class<T>) : List<ExcelFields> {
    val fields = mutableListOf<ExcelFields>()
    val seenFieldNames = mutableSetOf<String>()

    for (field in clazz.declaredFields) {
        val annotation = field.getAnnotation(Excel::class.java)
        if (annotation != null) {
            val fieldName = annotation.headerName

            if (fieldName.isBlank()) {
                throw ExcelReaderException(
                    "The 'fieldName' in @ExcelField cannot be blank on field " +
                            "'${field.name}' in class '${clazz.simpleName}'."
                )
            }

            if (!seenFieldNames.add(fieldName.trim())) {
                throw ExcelReaderException(
                    "Duplicate 'fieldName' '$fieldName' found in @ExcelField annotations " +
                    "in class '${clazz.simpleName}'."
                )
            }

            if (!isFieldConvertableFromExcel(field)) {
                throw ExcelReaderException(
                    "The field '${field.name}' in class '${clazz.simpleName}' is not " +
                    "convertible from Excel. Please check the type."
                )
            }

            field.isAccessible = true
            fields.add(ExcelFields(annotation, field))
        }
    }

    if (fields.isEmpty()) {
        throw ExcelReaderException(
            "No fields annotated with @ExcelField found in class '${clazz.simpleName}'."
        )
    }

    validateDuplicatedFieldNames(fields)

    return fields
}


/**
 * Validates if there are duplicated field names in the list of fields.
 * It checks if some fields can be derived from the same header name.
 */
private fun validateDuplicatedFieldNames(fields: List<ExcelFields>) {
    if (EXCEL_EXACT_STRING_HEADER_COMPARISON) {
        return
    }

    val fieldsComparison = fields
        .filter { !it.annotation.exactHeaderMatch }
        .mapNotNull { normalizeString(it.annotation.headerName) }


    for (field in fieldsComparison) {
        val matches = fieldsComparison.count { it.contains(field) || field.contains(it) }

        if (matches > 1) {
            throw ExcelReaderException("Duplicated field name '${field}' found in class.")
        }
    }
}