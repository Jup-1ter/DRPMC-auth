package ru.darkrpmc

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import ru.darkrpmc.commands.LoginCommand
import ru.darkrpmc.commands.RegisterCommand
import ru.darkrpmc.data.config.loadConfig

class Auth : JavaPlugin() {
    override fun onEnable() {
        setupVars(this)
        server.getWorld("world")!!.isAutoSave = false
        saveDefaultConfig()
        loadConfig()
        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        server.pluginManager.registerEvents(MainEvHandler, this)
        regCommands()
    }

    @Suppress("UnstableApiUsage")
    private fun regCommands() {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { e ->
            val commands = e.registrar()
            setOf(
                RegisterCommand,
                LoginCommand
            ).forEach { it.register(commands) }
        }
    }
}