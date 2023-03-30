package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.utils.FileUpload
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.io.File

/**
 * Module to send a welcome message to new members
 *
 * @author Jeremy Noesen
 */
object Welcomer {

    /**
     * Whether this module is enabled or not
     */
    var enabled = true

    /**
     * Send the welcome message embed
     *
     * @param member Member to welcome
     * @param guild Guild to welcome them to
     */
    fun welcome(member: Member, guild: Guild) {
        member.user.openPrivateChannel().queue { channel ->
            run {
                var toSend = Messages.welcomeMessage
                var file: File? = null
                if (toSend.contains("\$FILE\$")) {
                    val path = toSend.split("\$FILE\$")[1].trim()
                    toSend = toSend.replace(
                            toSend.substring(
                                    toSend.indexOf("\$FILE\$"),
                                    toSend.lastIndexOf("\$FILE\$") + 6
                            ), ""
                    )
                            .replace("  ", " ").trim()
                    file = File(path)
                    if (!file.exists() || file.isDirectory || file.isHidden) {
                        file = null
                    }
                }
                val embedBuilder = EmbedBuilder()
                embedBuilder.setColor(Config.embedColor)
                embedBuilder.setAuthor(
                        Messages.welcomeTitle
                                .replace("\$GUILD\$", guild.name)
                                .replace("\$NAME\$", member.effectiveName)
                                .replace("\$BOT\$", guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                                .replace("\\n", "\n")
                                .replace("  ", " ")
                                .trim(),
                        null, null
                )
                embedBuilder.setThumbnail(guild.iconUrl)
                embedBuilder.setDescription(
                        toSend.replace("\$PING\$", member.asMention)
                                .replace("\$NAME\$", member.effectiveName)
                                .replace("\$GUILD\$", guild.name)
                                .replace("\$BOT\$", guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                                .replace("\\n", "\n")
                                .replace("  ", " ")
                                .trim()
                )
                if (file != null) {
                    embedBuilder.setImage("attachment://" + file.name.replace(" ", "_"))
                    channel.sendMessageEmbeds(embedBuilder.build()).addFiles(FileUpload.fromData(file,
                            file.name.replace(" ", "_"))).queue()
                } else {
                    channel.sendMessageEmbeds(embedBuilder.build()).queue()
                }
            }
        }
    }
}