package jeremynoesen.bonebot

import jeremynoesen.bonebot.config.Config
import jeremynoesen.bonebot.config.Loader
import jeremynoesen.bonebot.listener.Listener
import jeremynoesen.bonebot.modules.*
import jeremynoesen.bonebot.util.Logger
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
            File("resources").mkdir()
            File("resources/images").mkdir()
            Config.loadData()
            Loader.loadData("resources/commands.txt", Command.commands)
            Loader.loadData("resources/responses.txt", Responder.responses)
            Loader.loadData("resources/texts.txt", Meme.texts)
            Loader.loadData("resources/statuses.txt", Status.statuses)
            Loader.loadData("resources/reactions.txt", Reactor.reactions)
            val jda = JDABuilder.createLight(Config.botToken).addEventListeners(Listener()).build()
            Status.setStatus(jda)
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}