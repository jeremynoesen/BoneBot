package jndev.bonebot.listener

import jndev.bonebot.config.Config
import jndev.bonebot.modules.Command
import jndev.bonebot.modules.Meme
import jndev.bonebot.modules.Reactor
import jndev.bonebot.modules.Responder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * all listeners for the bot
 *
 * @author JNDev (Jeremaster101)
 */
class Listener : ListenerAdapter() {
    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (!e.author.isBot) {
            when {
                e.message.contentRaw.startsWith(Config.commandPrefix + "meme") -> {
                    Meme.generate(e.message)
                    Runtime.getRuntime().gc()
                }
                e.message.contentRaw.startsWith(Config.commandPrefix + "restart") -> {
                    e.channel.sendMessage("Restarting...").queue()
                    e.channel.sendTyping().queue()
                    Thread.sleep(100L)
                    System.exit(0)
                }
                else -> {
                    Responder.respond(e.message)
                    Command.perform(e.message)
                    Reactor.react(e.message)
                }
            }
        }
    }
}