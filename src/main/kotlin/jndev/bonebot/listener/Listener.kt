package jndev.bonebot.listener

import jndev.bonebot.config.Config
import jndev.bonebot.modules.Command
import jndev.bonebot.modules.Meme
import jndev.bonebot.modules.Reactor
import jndev.bonebot.modules.Responder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

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
                    if (e.author == e.guild.owner?.user) {
                        e.channel.sendMessage("Restarting...").queue()
                        e.channel.sendTyping().queue()
                        e.jda.shutdown()
                    } else {
                        e.message.delete().queue()
                    }
                }
                e.message.contentRaw.startsWith(Config.commandPrefix + "help") -> {
                    var commandList = ""
                    var i = 0;
                    for (command in Command.commands) {
                        commandList += Config.commandPrefix + command.split(" // ")[0]
                        if (i != Command.commands.size - 1) commandList += ", "
                        i++
                    }
                    val embedBuilder = EmbedBuilder()
                    embedBuilder.setTitle("BoneBot Help")
                    embedBuilder.setDescription(
                        "The following are built-in BoneBot commands:\n\n" +
                                Config.commandPrefix + "meme <optional text> <optional image or user ping> - Generate" +
                                " a meme with input or random text and input or random image. Ping a user to use their avatar!\n\n" +
                                Config.commandPrefix + "restart - restarts the bot if set up properly, otherwise shuts down\n\n\n" +
                                "The following is a list of custom commands added to the bot by the manager of this bot:\n\n" +
                                commandList
                    )
                    embedBuilder.setColor(Color.BLUE)
                    e.channel.sendMessage(embedBuilder.build()).queue()
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