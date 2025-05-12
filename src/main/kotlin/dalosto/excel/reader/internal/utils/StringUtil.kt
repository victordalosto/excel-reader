package dalosto.excel.reader.internal.utils

import dalosto.excel.reader.api.ExcelConfigurations
import java.text.Normalizer
import java.util.regex.Pattern


/**
 * Normalizes a string to a standard format for comparison.
 *
 * @param input the value to normalize
 * @return the normalized string, or null if input is null
 */
internal fun normalizeString(input: Any?): String? {
    return input?.toString()
        ?.trim()
        ?.replace(Regex("[\n\r\t]"), " ")
        ?.let {
            if (ExcelConfigurations.STRING_NORMALIZER_CONSIDER_CASE_SENSITIVE) {
                it
            }
            else {
                it.lowercase()
            }
        }
        ?.let {
            if (ExcelConfigurations.STRING_NORMALIZER_UNACCENT) {
                removeAccents(it)
            } else {
                it
            }
        }
        ?.let {
            if (ExcelConfigurations.STRING_NORMALIZER_APPLY_REGEX) {
                it.replace(ExcelConfigurations.STRING_NORMALIZER_REGEX.toRegex(), "")
            }
            else {
                it
            }
        }
}



/**
 * Removes accents from a string using Unicode normalization.
 *
 * @param input the string to process
 * @return the string without diacritical marks
 */
private fun removeAccents(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    return pattern.matcher(normalized).replaceAll("")
}
