package ru.darkrpmc

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.boss.BarColor.YELLOW
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.AccountsTable.passwordColumn
import java.util.Date

@Suppress("unused")
object MainEvHandler : Listener {
    val countdowns = hashMapOf<String, Countdown>()
    val passwords = hashMapOf<String, String>()

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        val name = player.name
        if (player.isOp) event.disallow(
            PlayerLoginEvent.Result.KICK_OTHER,
            mm.deserialize("<red>Нельзя зайти на этот аккаунт")
        ) else transaction { AccountsTable
            .select(passwordColumn)
            .where(AccountsTable.selector(name))
            .singleOrNull()
        }?.let { passwords[name] = it[passwordColumn] }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        val player = event.player
        val name = player.name
        server.onlinePlayers.forEach {
            if (it != player) {
                player.hidePlayer(plugin, it)
                it.hidePlayer(plugin, player)
            }
        }
        val bossBar = server.createBossBar(
            if (name in passwords) "Введите пароль" else "Зарегистрируйтесь",
            YELLOW, BarStyle.SEGMENTED_20
        )
        bossBar.progress = 1.0
        bossBar.addPlayer(player)
        val at = Settings.Authorization.authorizationTime
        val cd = Countdown(player, bossBar, at)
        countdowns[name] = cd
        server.scheduler.runTaskTimer(plugin, cd, at.toLong(), at.toLong())
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: AsyncChatEvent) {
        if (!event.message().toString().startsWith('/')) event.isCancelled = true
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage(null)
        passwords -= event.player.name
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val to = event.to
        if (to.x !in 6.0..11.0 || to.y !in .0..2.0 || to.z !in 6.0..11.0)
            event.player.banIp(Settings.Messages.outOfBounds, null as Date?, "Анти-чит", true)
    }
}