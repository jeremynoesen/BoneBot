package jndev.bonebot

import jndev.bonebot.config.Config
import jndev.bonebot.config.Loader
import jndev.bonebot.listener.Listener
import jndev.bonebot.modules.*
import net.dv8tion.jda.api.JDABuilder
import java.awt.Toolkit
import javax.imageio.ImageIO
import javax.security.auth.login.LoginException

/**
 * BoneBot is a simple discord bot for the ISUCF'V'MB Trombone discord
 *
 * @author JNDev (Jeremaster101)
 */
object BoneBot {
    /**
     * create the bot and run it
     *
     * @param args
     * @throws LoginException when unable to log in to bot account
     */
    @Throws(LoginException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("apple.awt.UIElement", "true")
        Toolkit.getDefaultToolkit()
        ImageIO.setUseCache(false)
        Config.load()
        Loader.loadData("resources/commands.txt", Command.commands)
        Loader.loadData("resources/responses.txt", Responder.responses)
        Loader.loadData("resources/texts.txt", Meme.texts)
        Loader.loadData("resources/statuses.txt", Status.statuses)
        Loader.loadData("resources/reactions.txt", Reactor.reactions)
        val jda = JDABuilder.createLight(Config.botToken).addEventListeners(Listener()).build()
        Status.setStatus(jda)

//        Halloween halloween = new Halloween(jda);
//        jda.addEventListener(halloween);
    }
}