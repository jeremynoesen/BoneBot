package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * quote module to send quotes from a file
 *
 * @author Jeremy Noesen
 */
object Quotes {

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
     * send a random quote from the quotes file
     *
     * @param message message initiating action
     */
    fun sendQuote(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                prevTime = System.currentTimeMillis()
                if (quotes.size > 0) {
                    message.channel.sendMessage(quotes[Random().nextInt(quotes.size)].replace("\\n", "\n")).queue()
                } else {
                    message.channel.sendMessage("There are no quotes to show!").queue()
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                message.channel.sendMessage("Another quote can be sent in **$remaining** seconds.").queue()
            }
        } catch (e: Exception) {
            message.channel.sendMessage("**An error occurred!** Please check the log file!").queue()
            e.printStackTrace()
        }
    }
}