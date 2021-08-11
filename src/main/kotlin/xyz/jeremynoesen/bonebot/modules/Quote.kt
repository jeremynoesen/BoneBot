package xyz.jeremynoesen.bonebot.modules

import xyz.jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * quote module to send quotes from a file
 *
 * @author Jeremy Noesen
 */
object Quote {

    /**
     * list of quotes loaded from the quotes file
     */
    val quotes = ArrayList<String>()

    /**
     * cooldown for quote module, in seconds
     */
    var cooldown = 5

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * last time a quote was sent in milliseconds
     */
    private var prevTime = 0L

    /**
     * respond to a message if a trigger phrase is said
     *
     * @param message message initiating action
     */
    fun sendQuote(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                prevTime = System.currentTimeMillis()
                message.channel.sendMessage(quotes[Random().nextInt(quotes.size)].replace("\\n", "\n")).queue()
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                message.channel.sendMessage("Another quote can be sent in **$remaining** seconds.").queue()
            }
        } catch (e: Exception) {
            Logger.log(e, message.channel)
        }
    }
}