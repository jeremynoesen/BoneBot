package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Activity
import xyz.jeremynoesen.bonebot.BoneBot
import java.util.*

/**
 * module to handle now playing status of the bot
 *
 * @author Jeremy Noesen
 */
object Statuses {

    /**
     * list of now playing statuses loaded from the statuses file
     */
    val statuses = ArrayList<String>()

    /**
     * delay for status updater, in seconds
     */
    var delay = 60

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * set the status for the bot. this only needs to be called once
     */
    fun setStatus() {
        Thread {
            while (true) {
                if (statuses.isNotEmpty()) {
                    var status = statuses[Random().nextInt(statuses.size)]
                    when {
                        status.lowercase().startsWith("playing") -> {
                            status = status.substring(7, status.length).trim()
                            BoneBot.JDA!!.presence.activity = Activity.playing(status)
                        }
                        status.lowercase().startsWith("watching") -> {
                            status = status.substring(8, status.length).trim()
                            BoneBot.JDA!!.presence.activity = Activity.watching(status)
                        }
                        status.lowercase().startsWith("listening to") -> {
                            status = status.substring(12, status.length).trim()
                            BoneBot.JDA!!.presence.activity = Activity.listening(status)
                        }
                    }
                }
                try {
                    Thread.sleep(delay * 1000L)
                } catch (e: InterruptedException) {
                }
            }
        }.start()
    }
}