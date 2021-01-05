package jeremynoesen.bonebot.modules

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
    val commands = HashMap<String, String>()

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
     */
    fun perform(message: Message) {
        try {
            val msg = message.contentRaw.toLowerCase()
            if (msg.startsWith(commandPrefix)) {
                when {
                    msg.startsWith(commandPrefix + "meme") -> {
                        Meme(message).generate()
                    }
                    msg.startsWith(commandPrefix + "restart") -> {
                        if (message.member!!.isOwner) {
                            message.channel.sendMessage("Restarting...").queue()
                            message.channel.sendTyping().queue()
                            message.jda.shutdown()
                            Thread.sleep(1000)
                            System.exit(0)
                        } else {
                            message.delete().queue()
                        }
                    }
                    msg.startsWith(commandPrefix + "help") -> {
                        var commandList = ""
                        for ((i, command) in commands.keys.withIndex()) {
                            commandList += "`$commandPrefix$command`"
                            if (i != commands.size - 1) commandList += ", "
                        }
                        if (commandList.isBlank()) commandList = "*No commands defined*"
                        val embedBuilder = EmbedBuilder()
                        val name = message.jda.selfUser.name
                        embedBuilder.setAuthor("$name Help", null, message.jda.selfUser.avatarUrl)
                        embedBuilder.setColor(Color(0, 151, 255))
                        embedBuilder.setDescription(
                            "**Default Commands**\n" +
                                    "• `" + commandPrefix + "meme <text> <image or user>`: Generate" +
                                    " a meme using text and image or user avatar. If configured, you" +
                                    " can skip either to randomly pick them.\n" +
                                    "• `" + commandPrefix + "restart`: Restart the bot if configured, otherwise shut down.\n\n" +
                                    "**Custom Commands**\n" + commandList + "\n\n[GitHub](https://github.com/jeremynoesen/BoneBot)"
                        )
                        message.channel.sendMessage(embedBuilder.build()).queue()
                    }
                    else -> {
                        for (command in commands.keys) {
                            if (msg.startsWith("$commandPrefix$command")) {
                                if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                                    prevTime = System.currentTimeMillis()
                                    message.channel.sendMessage(
                                        commands[command]!!.replace(
                                            "\$USER$",
                                            message.author.asMention
                                        )
                                    ).queue()
                                } else {
                                    message.delete().queue()
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
    }
}