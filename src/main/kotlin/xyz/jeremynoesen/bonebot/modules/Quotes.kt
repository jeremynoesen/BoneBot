package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Messages
import java.lang.IllegalStateException
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
                    var quote = quotes[Random().nextInt(quotes.size)].replace("\\n", "\n")
                    if (quote.isNotEmpty())
                        quote = quote
                                .replace("\$PING\$", message.member!!.asMention)
                                .replace("\$NAME\$", message.member!!.effectiveName)
                                .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                                .replace("\\n", "\n")
                                .replace("  ", " ")
                                .trim()
                    try {
                        quote = quote.replace("\$GUILD\$", message.guild.name)
                    } catch (e: IllegalStateException) {
                    }
                    if (quote.contains("\$REPLY\$")) {
                        quote = quote.replace("\$REPLY\$", "")
                                .replace("  ", " ")
                        message.channel.sendMessage(quote).reference(message).queue()
                    } else {
                        message.channel.sendMessage(quote).queue()
                    }
                } else {
                    Messages.sendMessage(Messages.noQuotes, message)
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                Messages.sendMessage(Messages.quoteCooldown.replace("\$TIME\$", (remaining + 1).toString()), message)
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}