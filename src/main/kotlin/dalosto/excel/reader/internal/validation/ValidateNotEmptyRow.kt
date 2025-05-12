package dalosto.excel.reader.internal.validation

import org.dhatim.fastexcel.reader.Row


/**
 * Checks if the row contains any content.
 * This function checks if the row is empty or not.
 */
internal fun rowsContainsContent(row: Row): Boolean {
    return row.take(10).any { it?.value != null }
}