package ru.darkrpmc.data.config

import org.bukkit.configuration.ConfigurationSection

/**
 * Получает подраздел текущего раздела.
 *
 * @param path Путь к разделу в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если раздел не найден.
 * @return [ConfigurationSection] или `null`, если раздел не найден и не указан [default].
 */
fun ConfigurationSection.getConfigurationSection(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: ConfigurationSection? = null
): ConfigurationSection? {
    val def = default ?: if (useCfgDefault) defaultSection?.getConfigurationSection(path) else null
    return if (!contains(path)) {
        errorContainer += "Отсутствует раздел $path"
        def
    } else if (!isConfigurationSection(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является разделом"
        def
    } else getConfigurationSection(path)
}

/**
 * Получает строковое значение.
 *
 * @param path Путь к строке в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param pattern Регулярное выражение для проверки строки.
 * @param default Значение по умолчанию, если строка не найдена.
 * @return Строковое значение или `null`, если строка не найдена и не указан [default].
 */
fun ConfigurationSection.getString(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    pattern: Regex = Regex(".*"),
    default: String? = null,
    forceString: Boolean = false
): String? {
    val def = default ?: if (useCfgDefault) defaultSection?.getString(path) else null
    val s = getString(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!(forceString || isString(path))) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является строкой"
        def
    } else if (s!!.matches(pattern)) s
    else {
        errorContainer += "Значение $path ($s) не соответствует шаблону: ${pattern.pattern}"
        def
    }
}

/**
 * Получает значение типа [Int].
 *
 * @param path Путь к значению в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param min Минимально допустимое значение.
 * @param max Максимально допустимое значение.
 * @param default Значение по умолчанию, если значение не найдено.
 * @return Значение типа [Int] или `null`, если значение не найдено и не указан [default].
 */
fun ConfigurationSection.getInt(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    default: Int? = null
): Int? {
    val def = default ?: if (useCfgDefault) defaultSection?.getInt(path) else null
    if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        return def
    }
    if (!isInt(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является целым числом"
        return def
    }
    val s = getInt(path)
    return if (s in min..max) s
    else {
        errorContainer += "Значение $path ($s) находится вне допустимого диапазона: [$min;$max]"
        def
    }
}