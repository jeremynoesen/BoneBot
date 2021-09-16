package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.util.concurrent.TimeUnit

/**
 * reactor to add reactions based on words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Reactor {

    /**
     * list of reactions loaded from the reactions file
     */
    val reactions = LinkedHashMap<String, String>()

    /**
     * cooldown for reactor, in seconds
     */
    var cooldown = 60

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * last time the reactor reacted to a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * delay before adding a reaction in seconds
     */
    var delay = 1000L

    /**
     * react to a message if a trigger phrase is said
     *
     * @param message message to check and react to
     */
    fun react(message: Message) {
        try {
            val msg = message.contentDisplay
            for (trigger in reactions.keys) {
                val editedTrigger = trigger.replace("\$NAME\$", message.author.name)
                    .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                    .replace("\$GUILD\$", message.guild.name)
                    .replace("\\n", "\n")
                if ((msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase()))
                    && (System.currentTimeMillis() - prevTime) >= cooldown * 1000
                ) {
                    prevTime = System.currentTimeMillis()
                    message.addReaction(reactions[trigger]!!).queueAfter(delay, TimeUnit.MILLISECONDS)
                }
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}