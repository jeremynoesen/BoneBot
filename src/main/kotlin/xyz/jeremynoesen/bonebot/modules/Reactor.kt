package xyz.jeremynoesen.bonebot.modules

import xyz.jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message

/**
 * responder to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Reactor {

    /**
     * list of phrases loaded from the responses file
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
     * react to a message if a trigger phrase is said
     *
     * @param message message to check and react to
     */
    fun react(message: Message) {
        try {
            val msg = message.contentRaw.toLowerCase()
            for (trigger in reactions.keys) {
                if (msg.contains(Regex(trigger)) && (System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    message.addReaction(reactions[trigger]!!).queue()
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}