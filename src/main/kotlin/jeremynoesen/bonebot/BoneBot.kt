package jeremynoesen.bonebot

import jeremynoesen.bonebot.Config
import jeremynoesen.bonebot.modules.*
import net.dv8tion.jda.api.JDABuilder
import java.io.File
import javax.imageio.ImageIO
import javax.security.auth.login.LoginException

/**
 * Main class, initializes bot, loads all data, and initializes modules
 *
 * @author Jeremy Noesen
 */
object BoneBot {
    /**
     * create the bot and run it
     *
     * @param args
     * @throws LoginException when unable to log in to bot account
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            ImageIO.setUseCache(false)
            Config.loadData()
            val jda = JDABuilder.createLight(Config.botToken).addEventListeners(Listener()).build()
            Status.setStatus(jda)
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}