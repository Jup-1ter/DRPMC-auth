package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.entity.Player
import ru.darkrpmc.MainEvHandler.countdowns
import ru.darkrpmc.MainEvHandler.passwords
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.toLobby

@Suppress("UnstableApiUsage")
object LoginCommand : RegisteredCommand {
    override fun register(commands: Commands) {
        commands.register(
            literal("login")
                .requires {
                    val player = it.sender
                    player is Player && player.name in passwords
                }
                .then(argument("пароль", word())
                    .executes { ctx ->
                        val player = ctx.source.sender as Player
                        val name = player.name
                        val password = getString(ctx, "пароль")
                        return@executes if (password.length > 119) {
                            player.sendRichMessage("<red>Максимальна длина пароля 119 символов")
                            0
                        } else if (password == passwords[name]) {
                            player.toLobby()
                            1
                        } else {
                            player.sendRichMessage(Settings.Messages.wrongPassword)
                            countdowns[name]?.countOff(Settings.Authorization.wrongPasswordPenalty.toDouble())
                            0
                        }
                    }
                ).build(),
            "Служит для входа в аккаунт",
            setOf("log", "l")
        )
    }
}