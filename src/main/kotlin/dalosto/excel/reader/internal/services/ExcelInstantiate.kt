package dalosto.excel.reader.internal.services

import dalosto.excel.reader.api.ExcelConfigurations
import dalosto.excel.reader.api.ExcelConfigurations.EXCEL_UNDEFINED_VALUES
import dalosto.excel.reader.internal.exception.ExcelReaderException
import dalosto.excel.reader.internal.models.ExcelFields
import dalosto.excel.reader.internal.utils.booleanValue
import dalosto.excel.reader.internal.utils.numberValue
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.CellType
import org.dhatim.fastexcel.reader.Row


/**
 * Instantiates an object of the specified class type using the provided row and mapping.
 * This function uses reflection to create a new instance of the class and populate its fields
 * based on the provided mapping.
 */
internal fun <T> instantiate(row: Row, mapFields: Map<Int, ExcelFields>, clazz: Class<T>): T {
    val instance = instantiate(clazz)

    mapFields.forEach { (columnIndex, field) ->
        val cell = row.firstOrNull { it?.columnIndex == columnIndex }

        try {
            setFieldValue(instance, field, cell)
        } catch (e: Exception) {
            throw ExcelReaderException("Failed to set field: '${field.annotation.headerName}' "
                    + "with type '${field.field.genericType}' and Excel value: ${cell?.value}", e)
        }
    }

    return instance
}



/**
 * Instantiates an object of the specified class type.
 * It assumes that the class has a no-argument constructor.
 */
private fun <T> instantiate(clazz: Class<T>): T {
    val constructor = clazz.declaredConstructors.find { it.parameterCount == 0 }
        ?: throw ExcelReaderException(
            "Class ${clazz.name} does not have a no-argument constructor."
        )
    constructor.isAccessible = true

    @Suppress("UNCHECKED_CAST")
    return constructor.newInstance() as T
}



/**
 * Sets the value of a field in the specified instance.
 * The validator checks the possible values
 * @see dalosto.excel.reader.internal.validation.isFieldConvertableFromExcel
 */
private fun <T> setFieldValue(instance: T, field: ExcelFields, cell: Cell?) {

    if (cell?.value == null || cell.type == CellType.EMPTY) {
        if (field.field.type.isPrimitive) {
            throw ExcelReaderException(
                "Field '${field.annotation.headerName}' of type '${field.field.type}' " +
                "is primitive and cannot be null."
            )
        }
        field.field.set(instance, null)
        return
    }

    val fieldType = field.field.type

    // A special case for non-String fields that are categorically nullable
    if (ExcelConfigurations.EXCEL_TREAT_UNDEFINED_VALUES_AS_NULL) {

        // Check if the field type is not String and the cell content is one of the special cases
        if (fieldType != String::class.java && cell.value is String) {
            val value = (cell.value as String).trim().lowercase()
            if (value.isBlank() || value in EXCEL_UNDEFINED_VALUES) {
                field.field.set(instance, null)
                return
            }
        }

    }

    when (field.field.type) {
        String::class.java -> field.field.set(instance, cell.text)

        Boolean::class.java, java.lang.Boolean::class.java -> field.field.set(instance, booleanValue(cell))

        Byte::class.java, java.lang.Byte::class.java -> field.field.set(instance, numberValue(cell).toByte())
        Short::class.java, java.lang.Short::class.java -> field.field.set(instance, numberValue(cell).toShort())
        Int::class.java, Integer::class.java -> field.field.set(instance, numberValue(cell).toInt())
        Integer::class.java -> field.field.set(instance, numberValue(cell).toInt())
        Long::class.java, java.lang.Long::class.java -> field.field.set(instance, numberValue(cell).toLong())
        Float::class.java, java.lang.Float::class.java -> field.field.set(instance, numberValue(cell).toFloat())
        Double::class.java, java.lang.Double::class.java -> field.field.set(instance, numberValue(cell).toDouble())

        BigInteger::class.java -> field.field.set(instance, numberValue(cell).toBigInteger())
        BigDecimal::class.java -> field.field.set(instance, numberValue(cell))

        LocalDateTime::class.java -> field.field.set(instance, cell.asDate())

        else -> throw ExcelReaderException(
            "Field '${field.annotation.headerName}' of type '$fieldType' is not supported."
        )
    }
}
