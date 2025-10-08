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
    val seenColumnIndexes = mutableSetOf<Int>()

    for (field in clazz.declaredFields) {
        val annotation = field.getAnnotation(Excel::class.java)
        if (annotation != null) {
            val fieldName = annotation.headerName
            val columnIndex = annotation.columnIndex

            if (fieldName.isBlank() && columnIndex < 0) {
                throw ExcelReaderException(
                    "No definition for 'headerName' or 'columnIndex' in @ExcelField annotation " +
                    "'${field.name}' in class '${clazz.simpleName}'."
                )
            }

            if (!fieldName.isBlank() && !seenFieldNames.add(fieldName.trim())) {
                throw ExcelReaderException(
                    "Duplicate 'fieldName' '$fieldName' found in @ExcelField annotations " +
                    "in class '${clazz.simpleName}'."
                )
            }

            if (columnIndex >= 0 && !seenColumnIndexes.add(columnIndex)) {
                throw ExcelReaderException(
                    "Duplicate 'columnIndex' '$columnIndex' found in @ExcelField annotations " +
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
    validadeDuplicatedColumnIndexes(fields)

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
        .filter { it.annotation.columnIndex < 0 }
        .mapNotNull { normalizeString(it.annotation.headerName) }


    for (field in fieldsComparison) {
        val matches = fieldsComparison.count { it.contains(field) || field.contains(it) }

        if (matches > 1) {
            throw ExcelReaderException("Duplicated field name '${field}' found in class.")
        }
    }
}


/**
 * Validates if there are duplicated column indexes in the list of fields.
 * It checks if some fields are using the same column index.
 */
private fun validadeDuplicatedColumnIndexes(fields: List<ExcelFields>) {
    val columnIndexes = fields
        .filter { it.annotation.columnIndex >= 0 }
        .map { it.annotation.columnIndex }

    for (columnIndex in columnIndexes) {
        val matches = columnIndexes.count { it == columnIndex }

        if (matches > 1) {
            throw ExcelReaderException("Duplicated column index '${columnIndex}' found in class.")
        }
    }
}
