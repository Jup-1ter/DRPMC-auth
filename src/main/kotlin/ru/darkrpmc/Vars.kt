@file:Suppress("UnstableApiUsage")

package ru.darkrpmc

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import java.util.logging.Logger

/** Экземпляр плагина */
lateinit var plugin: Auth

/** Экземпляр логера */
lateinit var logger: Logger

/** Экземпляр сервера */
val server = Bukkit.getServer()

/** (Де)сериалайзер MiniMessage */
val mm = MiniMessage.miniMessage()

fun setupVars(pl: Auth) {
    plugin = pl
    logger = pl.logger
}