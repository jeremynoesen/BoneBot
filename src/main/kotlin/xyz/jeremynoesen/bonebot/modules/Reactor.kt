package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                val msg = message.contentDisplay
                for (trigger in reactions.keys) {
                    var editedTrigger = trigger.replace("\$NAME\$", message.author.name)
                        .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                        .replace("\\n", "\n")
                    try {
                        editedTrigger = editedTrigger.replace("\$GUILD\$", message.guild.name)
                    } catch (e: IllegalStateException) {}
                    if (msg.contains(Regex(editedTrigger)) || msg.lowercase().contains(editedTrigger.lowercase())) {
                        prevTime = System.currentTimeMillis()

                        val randomReactions = reactions[trigger]!!.split(" \$||\$ ")
                        val selectedReaction = randomReactions[Random.nextInt(randomReactions.size)]

                        for (reactionEmojis in selectedReaction.split(" \$&&\$ ")) {
                            message.addReaction(Emoji.fromFormatted(reactionEmojis)).queueAfter(delay, TimeUnit.MILLISECONDS)
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