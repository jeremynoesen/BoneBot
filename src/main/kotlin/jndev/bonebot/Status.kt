package jndev.bonebot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.util.*

/**
 * class to handle now playing status of the bot
 *
 * @author JNDev (Jeremaster101)
 */
object Status {
    /**
     * list of now playing statuses loaded from the statuses file
     */
    val statuses = ArrayList<String>()

    /**
     * set the status for the bot. this only needs to be called once
     *
     * @param jda jda to set status for
     */
    fun setStatus(jda: JDA) {
        Thread {
            while (true) {
                try {
                    Thread.sleep(Config.statusCooldown * 1000L)
                } catch (ignored: InterruptedException) {
                }
                val random = Random()
                jda.presence.activity = Activity.playing(statuses[random.nextInt(statuses.size)])
            }
        }.start()
    }
}