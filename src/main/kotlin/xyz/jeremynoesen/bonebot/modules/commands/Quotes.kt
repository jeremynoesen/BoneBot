package xyz.jeremynoesen.bonebot.modules.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.lang.IllegalStateException
import java.util.*

/**
 * Module to send quotes from a file
 *
 * @author Jeremy Noesen
 */
object Quotes {

    /**
     * List of quotes loaded from the quotes file
     */
    val quotes = ArrayList<String>()

    /**
     * Cooldown for quote module in seconds
     */
    var cooldown = 5

    /**
     * Whether this module is enabled or not
     */
    var enabled = true

    /**
     * Last time a quote was sent in milliseconds
     */
    private var prevTime = 0L

    /**
     * Send a random quote from the quotes file
     *
     * @param message Message initiating action
     */
    fun sendQuote(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                prevTime = System.currentTimeMillis()
                if (quotes.size > 0) {
                    var quote = quotes[Random().nextInt(quotes.size)].replace("\\n", "\n")
                    if (quote.isNotEmpty())
                        quote = quote
                                .replace("\$AUTHORMENTION\$", message.member!!.asMention)
                                .replace("\$AUTHORDISPLAYNAME\$", message.member!!.effectiveName)
                                .replace("\$AUTHORUSERNAME\$", message.author.name)
                                .replace("\$BOTMENTION\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.asMention)
                                .replace("\$BOTDISPLAYNAME\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!
                                        .effectiveName)
                                .replace("\$BOTUSERNAME\$", BoneBot.JDA!!.selfUser.name)
                                .replace("\$CHANNELMENTION\$", message.channel.asMention)
                                .replace("\$CHANNELNAME\$", message.channel.name)
                                .replace("\\n", "\n")
                                .replace("  ", " ")
                                .trim()
                    try {
                        quote = quote.replace("\$GUILD\$", message.guild.name)
                    } catch (_: IllegalStateException) {
                    }
                    val embedBuilder = EmbedBuilder()
                    var title = Messages.quoteTitle
                            .replace("\$AUTHORDISPLAYNAME\$", message.member!!.effectiveName)
                            .replace("\$AUTHORUSERNAME\$", message.author.name)
                            .replace("\$BOTDISPLAYNAME\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!
                                    .effectiveName)
                            .replace("\$BOTUSERNAME\$", BoneBot.JDA!!.selfUser.name)
                            .replace("\$CHANNELNAME\$", message.channel.name)
                            .replace("\\n", "\n")
                            .replace("  ", " ")
                            .trim()
                    try {
                        title = title.replace("\$GUILD\$", message.guild.name)
                    } catch (_: IllegalStateException) {
                    }
                    if (title.contains(message.member!!.effectiveName)) {
                        embedBuilder.setAuthor(title, null, message.member!!.effectiveAvatarUrl)
                    } else if (title.contains(message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)) {
                        embedBuilder.setAuthor(title, null, BoneBot.JDA!!.selfUser.effectiveAvatarUrl)
                    } else if (title.contains(message.guild.name)) {
                        embedBuilder.setAuthor(title, null, message.guild.iconUrl)
                    } else {
                        embedBuilder.setAuthor(title, null)
                    }
                    embedBuilder.setDescription(quote)
                    embedBuilder.setColor(Config.embedColor)
                    message.channel.sendMessageEmbeds(embedBuilder.build()).queue()
                } else {
                    Messages.sendMessage(Messages.noQuotes, message)
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                Messages.sendMessage(Messages.quoteCooldown
                        .replace("\$TIME\$", (remaining + 1).toString()), message)
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
        }
    }
}