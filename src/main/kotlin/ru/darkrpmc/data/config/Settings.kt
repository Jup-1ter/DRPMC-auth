package ru.darkrpmc.data.config

/** Настройки плагина */
object Settings {
    /** Настройки отвечающие за процесс авторизации */
    object Authorization {
        /** Время на ввод пароля */
        var authorizationTime = 60 * 2
        /** Штраф за неверный пароль */
        var wrongPasswordPenalty = authorizationTime / 20 * 4
    }

    /** Сообщения, отправляемые игрокам */
    object Messages {
        var register = "<gray>Чтобы зарегистрироваться, используйте команду /reg <пароль>"
        var login = "<gray>Чтобы войти в аккаунт, используйте команду /login <пароль>"
        var timeOut = "<red>Время авторизации истекло"
        var outOfBounds = "Использование читов"
        var wrongPassword = "<red>Указан неверный пароль. Время на авторизацию уменьшено"
    }
}