package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Module to add reactions based on words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Reactor {

    /**
     * List of reactions loaded from the reactions file
     */
    val reactions = LinkedHashMap<String, String>()

    /**
     * Cooldown for reactor in seconds
     */
    var cooldown = 60

    /**
     * Whether this module is enabled or not
     */
    var enabled = true

    /**
     * Last time the reactor reacted to a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * Delay before adding a reaction in milliseconds
     */
    var delay = 1000L

    /**
     * React to a message if a trigger phrase is said
     *
     * @param message Message to check and react to
     */
    fun react(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                val msg = message.contentDisplay
                for (trigger in reactions.keys) {
                    var editedTrigger = trigger.replace("\$PING\$", message.member!!.asMention)
                            .replace("\$NAME\$", message.member!!.effectiveName)
                            .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                            .replace("\\n", "\n")
                            .replace("  ", " ")
                            .trim()
                    try {
                        editedTrigger = editedTrigger.replace("\$GUILD\$", message.guild.name)
                    } catch (e: IllegalStateException) {
                    }
                    if (msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase())) {
                        prevTime = System.currentTimeMillis()
                        val randomReactions = reactions[trigger]!!.split("\$||\$")
                        val selectedReaction = randomReactions[Random.nextInt(randomReactions.size)]
                        for (reactionEmojis in selectedReaction.split("\$&&\$")) {
                            message.addReaction(Emoji.fromFormatted(reactionEmojis.trim())).queueAfter(delay, TimeUnit.MILLISECONDS)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}