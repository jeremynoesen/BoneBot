package xyz.jeremynoesen.bonebot.modules

import xyz.jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.util.*

/**
 * class to handle now playing status of the bot
 *
 * @author Jeremy Noesen
 */
object Status {

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
     *
     * @param jda jda to set status for
     */
    fun setStatus(jda: JDA) {
        Thread {
            while (true) {
                try {
                    if (statuses.isNotEmpty()) {
                        val random = Random()
                        jda.presence.activity = Activity.playing(statuses[random.nextInt(statuses.size)])
                    }
                } catch (e: Exception) {
                    Logger.log(e)
                }
                try {
                    Thread.sleep(delay * 1000L)
                } catch (e: InterruptedException) {
                    Logger.log(e)
                }
            }
        }.start()
    }
}