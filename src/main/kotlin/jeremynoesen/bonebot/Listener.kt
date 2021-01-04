package jeremynoesen.bonebot

import jeremynoesen.bonebot.modules.Command
import jeremynoesen.bonebot.modules.Meme
import jeremynoesen.bonebot.modules.Reactor
import jeremynoesen.bonebot.modules.Responder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.lang.Exception

/**
 * all listeners for the bot
 *
 * @author Jeremy Noesen
 */
class Listener : ListenerAdapter() {

    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    override fun onMessageReceived(e: MessageReceivedEvent) {
        try {
            if (!e.author.isBot) {
                when {
                    e.message.contentRaw.startsWith(Command.commandPrefix + "meme") -> {
                        Meme.generate(e.message)
                        Runtime.getRuntime().gc()
                    }
                    e.message.contentRaw.startsWith(Command.commandPrefix + "restart") -> {
                        if (e.author.idLong == 393939920177070100L || e.member!!.isOwner) {
                            e.channel.sendMessage("Restarting...").queue()
                            e.channel.sendTyping().queue()
                            e.jda.shutdown()
                            Thread.sleep(1000)
                            System.exit(0)
                        } else {
                            e.message.delete().queue()
                        }
                    }
                    e.message.contentRaw.startsWith(Command.commandPrefix + "help") -> {
                        var commandList = ""
                        for ((i, command) in Command.commands.withIndex()) {
                            commandList += "`" + Command.commandPrefix + command.split(" // ")[0] + "`"
                            if (i != Command.commands.size - 1) commandList += ", "
                        }
                        if (commandList.isBlank()) commandList = "No commands defined"
                        val embedBuilder = EmbedBuilder()
                        val name = e.jda.selfUser.name
                        embedBuilder.setAuthor("$name Help", null, e.jda.selfUser.avatarUrl)
                        embedBuilder.setColor(Color(0, 151, 255))
                        embedBuilder.setDescription(
                            "**Default Commands:**\n" +
                                    "• `" + Command.commandPrefix + "meme <optional text> <optional image or mention>`: Generate" +
                                    " a meme with input or random text, input or random image, or user avatar.\n" +
                                    "• `" + Command.commandPrefix + "restart`: Restart the bot if set up properly, otherwise shut down.\n\n" +
                                    "**Custom Commands:**\n" + commandList + "\n\n[GitHub](https://github.com/Jeremaster101/BoneBot)"
                        )
                        e.channel.sendMessage(embedBuilder.build()).queue()
                    }
                    else -> {
                        Responder.respond(e.message)
                        Command.perform(e.message)
                        Reactor.react(e.message)
                    }
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}