package jeremynoesen.bonebot

import jeremynoesen.bonebot.modules.Status
import net.dv8tion.jda.api.JDABuilder
import java.awt.Toolkit
import java.io.File
import java.io.FileNotFoundException
import javax.imageio.ImageIO

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
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            System.setProperty("apple.awt.UIElement", "true") //hide dock icon on macOS
            Toolkit.getDefaultToolkit()
            ImageIO.setUseCache(false)
            Config.loadData()
            val jda = JDABuilder.createLight(Config.botToken).addEventListeners(Listener()).build()
            if (Status.enabled) Status.setStatus(jda)
            try {
                File("temp").deleteRecursively()
            } catch (ignored: FileNotFoundException) {
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}