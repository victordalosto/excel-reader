package dalosto.excel.reader.internal.validation

import dalosto.excel.reader.internal.exception.ExcelReaderException


/**
 * Validates if the provided class has a no-argument constructor.
 */
internal fun checkIfClassHasNoArgsConstructor(clazz: Class<*>) {
    clazz.declaredConstructors.find { it.parameterCount == 0 }
        ?: throw ExcelReaderException(
            "Class ${clazz.name} does not have a no-argument constructor."
        )
}
