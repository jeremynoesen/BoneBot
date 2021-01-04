package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * command handler with simple message responses
 *
 * @author Jeremy Noesen
 */
object Command {

    /**
     * list of commands loaded from the command file
     */
    val commands = ArrayList<String>()

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
     * respond to a message if a command
     *
     * @param message message to check and respond to
     */
    fun perform(message: Message) {
        try {
            var msg = message.contentRaw.toLowerCase()
            if (msg.startsWith(commandPrefix)) {
                msg = msg.replaceFirst(commandPrefix, "")
                for (command in commands) {
                    val commandAndMessage = command.split(" // ").toTypedArray()
                    if (msg.startsWith(commandAndMessage[0])) {
                        if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                            prevTime = System.currentTimeMillis()
                            for (i in 1 until commandAndMessage.size) message.channel.sendMessage(
                                commandAndMessage[1].replace("\$USER$", message.author.asMention)
                            ).queue()
                        } else {
                            message.delete().queue()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}