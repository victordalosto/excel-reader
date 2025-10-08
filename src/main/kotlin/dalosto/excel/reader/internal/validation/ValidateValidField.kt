package dalosto.excel.reader.internal.validation

import java.lang.Byte
import java.lang.Float
import java.lang.Short
import java.lang.String
import java.lang.reflect.Field
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime


/**
 * Checks if a field is convertible from Excel.
 * This function checks if the field type is primitive or one of the specified types.
 *
 * This field validation is used in the instantiation process of the Excel reader.
 * @see dalosto.excel.reader.internal.services.setFieldValue
 *
 * @param field The field to check.
 * @return true if the field is convertible from Excel, false otherwise.
 */
internal fun isFieldConvertableFromExcel(field: Field): Boolean {
    return field.type.isPrimitive
        || field.type == String::class.java
        || field.type == java.lang.Boolean::class.java
        || field.type == Byte::class.java
        || field.type == Short::class.java
        || field.type == Int::class.java
        || field.type == Integer::class.java
        || field.type == java.lang.Long::class.java
        || field.type == Float::class.java
        || field.type == java.lang.Double::class.java
        || field.type == BigInteger::class.java
        || field.type == BigDecimal::class.java
        || field.type == LocalDateTime::class.java
        || field.type.kotlin.javaObjectType == Double::class.java
        || field.type.kotlin.javaObjectType == Integer::class.java
        || field.type.kotlin.javaObjectType == Long::class.java
}
