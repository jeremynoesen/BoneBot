package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.config.Config
import jeremynoesen.bonebot.util.Logger
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.lang.Exception
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
     * set the status for the bot. this only needs to be called once
     *
     * @param jda jda to set status for
     */
    fun setStatus(jda: JDA) {
        Thread {
            while (true) {
                try {
                    Thread.sleep(Config.statusCooldown * 1000L)
                } catch (e: InterruptedException) {
                    Logger.log(e)
                }
                try {
                    val random = Random()
                    jda.presence.activity = Activity.playing(statuses[random.nextInt(statuses.size)])
                } catch (e: Exception) {
                    Logger.log(e)
                }
            }
        }.start()
    }
}