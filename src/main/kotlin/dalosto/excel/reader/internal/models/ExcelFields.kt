package dalosto.excel.reader.internal.models

import dalosto.excel.reader.api.Excel
import java.lang.reflect.Field

internal data class ExcelFields(
    val annotation: Excel,
    val field: Field
)