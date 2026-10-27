package ru.darkrpmc

import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.data.config.Settings
import java.util.function.Consumer
import kotlin.math.max

class Countdown(
    private val player: Player,
    private val bossBar: BossBar,
    private val time: Int
) : Consumer<BukkitTask> {
    var remaining = time.toDouble()

    override fun accept(task: BukkitTask) {
        if (player.isOnline) {
            countOff(time / 20.0)
        } else {
            bossBar.removeAll()
            task.cancel()
        }
    }

    fun countOff(amount: Double) {
        remaining -= amount
        bossBar.progress = max(remaining / time, .0)
        if (remaining <= 0) {
            player.kick(mm.deserialize(Settings.Messages.timeOut))
        }
    }
}