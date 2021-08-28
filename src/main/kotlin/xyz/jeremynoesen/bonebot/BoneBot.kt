package xyz.jeremynoesen.bonebot

import net.dv8tion.jda.api.JDABuilder
import xyz.jeremynoesen.bonebot.modules.Statuses
import java.awt.Toolkit
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
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
        System.setProperty("apple.awt.UIElement", "true") //hide dock icon on macOS
        Toolkit.getDefaultToolkit()
        ImageIO.setUseCache(false)
        Config.loadData()
        val log = PrintStream(FileOutputStream("log.txt", true), true)
        System.setOut(log)
        System.setErr(log)
        val jda = JDABuilder.createLight(Config.botToken).addEventListeners(Listener()).build()
        if (Statuses.enabled) Statuses.setStatus(jda)
        try {
            File("temp").deleteRecursively()
        } catch (ignored: FileNotFoundException) {
        }
    }
}