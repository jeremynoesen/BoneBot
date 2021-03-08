package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Config
import jeremynoesen.bonebot.Logger
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
                when {
                    msg.startsWith(commandPrefix + "meme") -> {
                        if (Meme.enabled) {
                            Meme(message).generate()
                            return true
                        }
                        return false
                    }
                    msg.startsWith(commandPrefix + "help") -> {
                        if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                            prevTime = System.currentTimeMillis()
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
                        } else {
                            val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                            message.channel.sendMessage("Commands can be used again in **$remaining** seconds.")
                                .queue()
                            return true
                        }
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
                                    message.channel.sendMessage("Commands can be used again in **$remaining** seconds.")
                                        .queue()
                                    return true
                                }
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