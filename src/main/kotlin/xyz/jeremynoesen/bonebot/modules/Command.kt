package xyz.jeremynoesen.bonebot.modules

import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message

/**
 * command handler with simple message responses
 *
 * @author Jeremy Noesen
 */
object Command {

    /**
     * list of commands loaded from the command file
     */
    val commands = LinkedHashMap<String, Pair<String, String>>()

    /**
     * command prefix
     */
    var commandPrefix = "bb"

    /**
     * cooldown for commands, in seconds
     */
    var cooldown = 5

    /**
     * whether this module is enabled or not
     */
    var enabled = true

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
                if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    when {
                        msg.startsWith(commandPrefix + "meme") && Meme.enabled -> {
                            Meme(message).generate()
                            return true
                        }
                        msg == commandPrefix + "help" -> {
                            var commandList = "• `$commandPrefix" + "help`: Show the help message.\n"
                            if (Meme.enabled) commandList += "• `$commandPrefix" + "meme <text> <image>`: Generate a meme.\n"
                            for (command in commands.keys)
                                commandList += "• `$commandPrefix$command`: ${commands[command]!!.first}\n"
                            val embedBuilder = EmbedBuilder()
                            val name = message.jda.selfUser.name
                            embedBuilder.setAuthor("$name Help", null, message.jda.selfUser.avatarUrl)
                            embedBuilder.setColor(Config.embedColor)
                            embedBuilder.setDescription("$commandList\n[GitHub](https://github.com/jeremynoesen/BoneBot)")
                            message.channel.sendMessage(embedBuilder.build()).queue()
                            return true
                        }
                        else -> {
                            for (command in commands.keys) {
                                if (msg == "$commandPrefix$command") {
                                    var toSend =
                                        commands[command]!!.second.replace("\$USER$", message.author.asMention)
                                    if (toSend.contains("\$REPLY$")) {
                                        toSend = toSend.replace("\$REPLY$", "").replace("  ", " ")
                                        message.channel.sendMessage(toSend).reference(message).queue()
                                    } else {
                                        message.channel.sendMessage(toSend).queue()
                                    }
                                    return true
                                }
                            }
                            message.channel.sendMessage("Unknown command!").queue()
                            return true
                        }
                    }
                } else {
                    val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                    message.channel.sendMessage("Commands can be used again in **$remaining** seconds.")
                        .queue()
                    return true
                }
            }
        } catch (e: Exception) {
            Logger.log(e, message.channel)
        }
        return false
    }
}