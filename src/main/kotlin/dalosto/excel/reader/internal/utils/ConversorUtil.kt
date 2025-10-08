package dalosto.excel.reader.internal.utils

import dalosto.excel.reader.internal.exception.ExcelReaderException
import java.math.BigDecimal
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.CellType


private val TRUE_VALUES = setOf("true", "yes", "y", "1", "sim", "s", "verdadeiro", "v", "si", "sin",
    "positivo", "ok", "on", "ativo")

private val FALSE_VALUES = setOf("false", "no", "n", "0", "n0", "não", "nao", "na0", "f", "falso",
    "negativo", "desabilitado", "desligado", "off", "inativo"
)



/**
 * Converts a cell value to a Boolean.
 * It supports BOOLEAN and STRING cell types.
 * For STRING, it checks against predefined true/false values.
 */
internal fun booleanValue(cell: Cell): Boolean {
    return when (cell.type) {
        CellType.BOOLEAN -> cell.asBoolean()
        CellType.STRING -> {
            when (val value = (cell.value).toString().trim().lowercase()) {
                in TRUE_VALUES -> true
                in FALSE_VALUES -> false
                else -> throw ExcelReaderException("Cannot convert string value '$value' to Boolean.")
            }
        }
        CellType.FORMULA -> {
            when (val value = (cell.value).toString().trim().lowercase()) {
                in TRUE_VALUES -> true
                in FALSE_VALUES -> false
                else -> throw ExcelReaderException("Cannot convert string value '$value' to Boolean.")
            }
        }
        else -> throw ExcelReaderException("Cannot convert cell value '${cell.value}' to Boolean.")
    }
}



/**
 * Converts a cell value to a BigDecimal.
 * It supports NUMBER and STRING cell types.
 * For STRING, it cleans the string to extract a valid number format.
 */
internal fun numberValue(cell: Cell): BigDecimal {
    return when (cell.type) {
        CellType.NUMBER -> cell.asNumber()
        CellType.STRING -> cleanNumberString(cell.value)
        CellType.FORMULA -> cleanNumberString(cell.value)
        else -> throw ExcelReaderException("Cannot convert cell value '${cell.value}' to Number.")
    }
}



private fun cleanNumberString(input: Any): BigDecimal {
    try {
        var value = input.toString().uppercase()
                         .replace("\\s".toRegex(), "")
                         .replace(",", ".")
                         .replace("10^", "E")
                         .replace("10\\^".toRegex(), "E")
                         .replace("[^\\dE\\-.]".toRegex(), "")

        // Handle "10^x" notation by converting to "1E3" (if no coefficient before 10^)
        if (value.matches(Regex("^E\\d+$"))) { // e.g.: E3
            value = "1$value"  // Add a 1 in front, so it's like "1E3"
        }

        // Handle multiple dots: keep only the last one
        val lastDotIndex = value.lastIndexOf('.')
        if (lastDotIndex != -1) {
            // This logic handles cases like "1,234.56" where we want to keep the last dot
            value = value.withIndex()
                         .filter { (idx, ch) -> ch != '.' || idx == lastDotIndex }
                         .joinToString("") { it.value.toString() }
        }


        // Fix multiple 'E's (keep the first occurrence only)
        val parts = value.split("E", ignoreCase = true, limit = 2)
        value = parts[0] + if (parts.size > 1) "E" + parts[1].replace("E", "") else ""

        return BigDecimal(value)

    } catch (e: NumberFormatException) {
        throw ExcelReaderException("Cannot convert string value '$input' to Number.", e)
    }
}
