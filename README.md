# Excel Reader for Unstructured Data

A lightweight Kotlin library for reading **unstructured or unpredictable Excel files** and mapping their contents to Java/Kotlin data classes.

Built on top of the high-performance **[fastexcel](https://github.com/dhatim/fastexcel)** library, this tool provides a flexible mechanism to handle dynamic Excel files.

## Features
- **Handles Unstructured Data**: Reads Excel files with unpredictable, unordered titles, or varying header names.
- **Dynamic Mapping**: Maps Excel column headers to Java/Kotlin class fields using customizable annotations.
- **High Performance**: Leverages `org.dhatim.fastexcel` for fast and efficient Excel processing.
- **Type Safety**: Supports mapping to strongly-typed Kotlin/Java data classes.
- **Lightweight**: Minimal dependencies and easy integration into existing projects.
- **Multi-Sheet Support**: Reads data from specific sheets in multi-sheet Excel files.


### Installation

#### Maven
Add the following dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>



<dependency>
    <groupId>dalosto.excel.reader</groupId>
    <artifactId>excel-reader</artifactId>
    <version>1.0</version>
</dependency>
```

## Usage

This project is compatible with Java 8+ and Kotlin 1.3+.

### Step 1: Define Your Data Model

Create a Java/Kotlin data class to represent the structure of your Excel data.
Use the @Excel annotation to map class fields to Excel column headers.

```kotlin
import dalosto.excel.reader.api.Excel

data class ExampleClass(
    @Excel(headerName = "Name", required = true)
    var name: String? = null,

    @Excel(headerName = "Alias")
    var alias: String? = null,

    @Excel(headerName = "Age")
    var age: Int? = null,

    @Excel(headerName = "Cost")
    var cost: Double? = null,

    @Excel(headerName = "Data")
    var data: String? = null
)
```

Where:
* headerName: The expected column header in the Excel file.
* required: Marks a field as mandatory (throws an error if the column value is null).

### Step 2: Read Excel File

Use the readExcel function to parse the Excel file and map its data to your data class.

```kotlin
import dalosto.excel.reader.api.Excel
import dalosto.excel.reader.api.numberOfSheets
import dalosto.excel.reader.api.readExcel
import java.io.File

fun main() {
    // Load the Excel file
    val excelFile = File("path/to/your/tests.xlsx")

    // Check the number of sheets
    val sheetCount = numberOfSheets(excelFile)
    println("Number of sheets: $sheetCount")

    // Read data from the first sheet (index 0)
    val response = readExcel(excelFile, ExampleClass::class.java, 0)

    // Print the mapped data
    response.data?.forEach { println(it) }
}
```

### Limitations
Assumes the excel contains a header row and that the data is structured in a tabular format.
