package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Logger
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
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}