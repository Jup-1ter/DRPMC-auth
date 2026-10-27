package ru.darkrpmc.data.config

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.PerServerDataTable
import ru.darkrpmc.logger
import ru.darkrpmc.plugin
import ru.darkrpmc.server

fun loadConfig() {
    val cfg = plugin.config
    val errors = TreeNode("config")

    /// Авторизация ///

    val aErrors = TreeNode("authorization")
    val aSection = cfg.getConfigurationSection("authorization", errors)!!

    Settings.Authorization.authorizationTime = aSection.getInt("authorization-time", aErrors, default = Settings.Authorization.authorizationTime)!!
    Settings.Authorization.wrongPasswordPenalty = aSection.getInt("wrong-password-penalty", aErrors, default = Settings.Authorization.wrongPasswordPenalty)!!

    if (!aErrors.isEmpty()) errors += aErrors

    /// Сообщения ///

    val mErrors = TreeNode("messages")
    val mSection = cfg.getConfigurationSection("messages", errors)!!

    Settings.Messages.register = mSection.getString("register", mErrors, default = Settings.Messages.register)!!
    Settings.Messages.login = mSection.getString("login", mErrors, default = Settings.Messages.login)!!
    Settings.Messages.timeOut = mSection.getString("time-out", mErrors, default = Settings.Messages.timeOut)!!
    Settings.Messages.outOfBounds = mSection.getString("out-of-bounds", mErrors, default = Settings.Messages.outOfBounds)!!
    Settings.Messages.wrongPassword = mSection.getString("wrong-password", mErrors, default = Settings.Messages.wrongPassword)!!

    if (!mErrors.isEmpty()) errors += mErrors

    /// База данных ///

    val dbProblems = TreeNode("database")
    val dbSection = cfg.getConfigurationSection("database", errors)!!

    val url = dbSection.getString("url", dbProblems, false)
    val user = dbSection.getString("user", dbProblems, false)
    val password = dbSection.getString("password", dbProblems, false)

    if (!dbProblems.isEmpty()) errors += dbProblems

    ///-///

    if (errors.isEmpty()) logger.info("Параметры успешно загружены из файла")
    else logger.severe("Проблемы при чтении файла конфигурации\n$errors")

    if (url != null && user != null && password != null) {
        try {
            Database.connect(url, user = user, password = password)
            transaction { SchemaUtils.create(AccountsTable, PerServerDataTable) }
            return
        } catch (e: ClassNotFoundException) {
            logger.severe("Не удалось подключиться к базе данных: Не найден класс драйвера ${e.message}")
        } catch (e: Exception) {
            logger.severe("Не удалось подключиться к базе данных: $e")
        }
    }

    server.shutdown()
}