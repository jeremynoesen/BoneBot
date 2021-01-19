package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Config
import jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color

/**
 * command handler with simple message responses
 *
 * @author Jeremy Noesen
 */
object Command {

    /**
     * list of commands loaded from the command file
     */
    val commands = HashMap<String, Pair<String, String>>()

    /**
     * command prefix
     */
    var commandPrefix = "bb"

    /**
     * cooldown for commands, in seconds
     */
    var cooldown = 5

    /**
     * last time the command handler sent a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * respond to a message if it is a command
     *
     * @param message message to check and respond to
     * @return true if command was performed
     */
    fun perform(message: Message): Boolean {
        try {
            val msg = message.contentRaw.toLowerCase()
            if (msg.startsWith(commandPrefix)) {
                when {
                    msg.startsWith(commandPrefix + "meme") -> {
                        Meme(message).generate()
                        return true
                    }
                    msg.startsWith(commandPrefix + "help") -> {
                        var commandList = ""
                        for (command in commands.keys) {
                            commandList += "• `$commandPrefix$command`: ${commands[command]!!.first}\n"
                        }
                        if (commandList.isBlank()) commandList = "*No commands defined*"
                        val embedBuilder = EmbedBuilder()
                        val name = message.jda.selfUser.name
                        embedBuilder.setAuthor("$name Help", null, message.jda.selfUser.avatarUrl)
                        embedBuilder.setColor(Config.embedColor)
                        embedBuilder.setDescription(
                            "**Default Commands**\n" +
                                    "• `" + commandPrefix + "meme <text> <image or user>`: Generate" +
                                    " a meme using text and image or user avatar. If configured, you" +
                                    " can skip either to randomly pick them.\n" +
                                    "• `" + commandPrefix + "help`: Show this help message.\n\n" +
                                    "**Custom Commands**\n" +
                                    commandList +
                                    "\n[GitHub](https://github.com/jeremynoesen/BoneBot)"
                        )
                        message.channel.sendMessage(embedBuilder.build()).queue()
                        return true
                    }
                    else -> {
                        for (command in commands.keys) {
                            if (msg.startsWith("$commandPrefix$command")) {
                                if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                                    prevTime = System.currentTimeMillis()
                                    message.channel.sendMessage(
                                        commands[command]!!.second.replace(
                                            "\$USER$",
                                            message.author.asMention
                                        )
                                    ).queue()
                                    return true
                                } else {
                                    val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                                    message.channel.sendMessage("Custom commands can be used again in **$remaining** seconds.")
                                        .queue()
                                    return true
                                }
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
        return false
    }
}