package jndev.bonebot

import jndev.bonebot.config.Config
import jndev.bonebot.config.Loader
import jndev.bonebot.listener.Listener
import jndev.bonebot.modules.Meme
import jndev.bonebot.modules.Reactor
import jndev.bonebot.modules.Responder
import jndev.bonebot.modules.Status
import net.dv8tion.jda.api.JDABuilder
import java.awt.Toolkit
import javax.imageio.ImageIO
import javax.security.auth.login.LoginException
import kotlin.system.exitProcess

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
object BoneBot {
    /**
     * create the bot and run it
     *
     * @param args arg 0 is the bot token
     * @throws LoginException when unable to log in to bot account
     */
    @Throws(LoginException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("apple.awt.UIElement", "true")
        Toolkit.getDefaultToolkit()
        if (args.isEmpty()) {
            println("You have to provide a token as first argument!")
            exitProcess(1)
        }
        val jda = JDABuilder.createLight(args[0])
            .addEventListeners(Listener())
            .build()

//        Halloween halloween = new Halloween(jda);
//        jda.addEventListener(halloween);
        ImageIO.setUseCache(false)
        Config.load()
        Loader.loadData("resources/responses.txt", Responder.responses)
        Loader.loadData("resources/texts.txt", Meme.texts)
        Loader.loadData("resources/statuses.txt", Status.statuses)
        Loader.loadData("resources/reactions.txt", Reactor.reactions)
        Status.setStatus(jda)
    }
}