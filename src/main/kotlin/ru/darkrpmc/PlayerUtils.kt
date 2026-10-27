package ru.darkrpmc

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player

fun Player.transferViaProxy(server: String) {
    val out = ByteStreams.newDataOutput()
    out.writeUTF("Connect")
    out.writeUTF(server)
    sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
}

fun Player.toLobby() = transferViaProxy("lobby")