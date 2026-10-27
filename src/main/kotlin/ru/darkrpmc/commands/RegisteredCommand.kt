package ru.darkrpmc.commands

import io.papermc.paper.command.brigadier.Commands

@Suppress("UnstableApiUsage")
interface RegisteredCommand {
    fun register(commands: Commands)
}