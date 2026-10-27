package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.darkrpmc.MainEvHandler.passwords
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.toLobby

@Suppress("UnstableApiUsage")
object RegisterCommand : RegisteredCommand {
    override fun register(commands: Commands) {
        commands.register(
            literal("register")
                .requires {
                    val player = it.sender
                    player is Player && player.name !in passwords
                }
                .then(argument("пароль", word())
                    .executes { ctx ->
                        val player = ctx.source.sender as Player
                        val password = getString(ctx, "пароль")
                        return@executes if (password.length > 119) {
                            player.sendRichMessage("<red>Максимальна длина пароля 119 символов")
                            0
                        } else {
                            val ip = player.address!!.hostString
                            transaction {
                                AccountsTable.insert {
                                    it[nameColumn] = player.name
                                    it[passwordColumn] = password
                                    it[regIPColumn] = ip
                                    it[lastIPColumn] = ip
                                }
                            }
                            player.toLobby()
                            1
                        }
                    }
                ).build(),
            "Регистрирует аккаунт",
            setOf("reg", "r")
        )
    }
}