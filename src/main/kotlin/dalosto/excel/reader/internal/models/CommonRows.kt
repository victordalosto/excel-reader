package dalosto.excel.reader.internal.models

/**
 * A class to manage a queue of common rows in an Excel sheet.
 * It's used to store the headers of the sheet using multiple rows in sequence to form a header.
 */
internal class CommonRows(private val maxSize: Int) {

    // A queue of maps, each representing a row of headers
    private val commonRowsQueue: ArrayDeque<MutableMap<Int, ExcelFields>> = ArrayDeque()


    fun addValue(row: Int, field: ExcelFields) {
        // Always add to the latest map
        val currentMap = commonRowsQueue.last()
        currentMap[row] = field
    }


    fun startNewRow() {
        // If at capacity, remove the oldest group
        if (commonRowsQueue.size >= maxSize) {
            commonRowsQueue.removeFirst()
        }

        // Add a new empty group at the end
        commonRowsQueue.addLast(mutableMapOf())
    }


    // Return the row values
    fun getHeadersRowGroups(): Map<Int, ExcelFields> {
        val merged = mutableMapOf<Int, ExcelFields>()
        for (group in commonRowsQueue) {
            merged.putAll(group)
        }

        return merged
    }

}
