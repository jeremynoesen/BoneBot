package jndev.bonebot.modules

import jndev.bonebot.config.Config
import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * responder to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Reactor {

    /**
     * list of phrases loaded from the responses file
     */
    val reactions = ArrayList<String>()

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
        val msg = message.contentRaw.toLowerCase()
        for (phrase in reactions) {
            val triggerAndEmotes = phrase.split(" // ").toTypedArray()
            val triggers = triggerAndEmotes[0].split(" / ").toTypedArray()
            var count = 0
            for (trigger in triggers) {
                if (msg.contains(trigger.toLowerCase())) count++
            }
            if (count == triggers.size && (System.currentTimeMillis() - prevTime) >= Config.reactCooldown * 1000) {
                prevTime = System.currentTimeMillis()
                for (i in 1 until triggerAndEmotes.size) message.addReaction(triggerAndEmotes[i]).queue()
            }
        }
    }
}