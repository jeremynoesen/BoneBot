package jndev.bonebot

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
                e.message.contentRaw.startsWith("bbmeme") -> {
                    Meme.generate(e.message)
                    Runtime.getRuntime().gc()
                }
                e.message.contentRaw.startsWith("bbrestart") -> {
                    e.channel.sendMessage("Restarting...").queue()
                    e.channel.sendTyping().queue()
                    System.exit(0)
                }
                else -> {
                    Responder.respond(e.message)
                    Reactor.react(e.message)
                }
            }
        }
    }
}