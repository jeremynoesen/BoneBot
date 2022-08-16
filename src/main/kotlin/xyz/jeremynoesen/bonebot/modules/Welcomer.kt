package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.io.File

/**
 * module to send a welcome message to new members
 *
 * @author Jeremy Noesen
 */
object Welcomer {

    /**
     * message to show in welcome message embed
     */
    var message = "Welcome \$PING\$ to **\$GUILD\$**!"

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * send the welcome message embed
     *
     * @param user user to welcome
     * @param guild guild to welcome them to
     */
    fun welcome(user: User, guild: Guild) {
        user.openPrivateChannel().queue { channel ->
            run {
                var toSend = message
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
                        Messages.welcomeTitle.replace("\$GUILD\$", guild.name)
                                .replace("\$NAME\$", user.name)
                                .replace("\$BOT\$", guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName),
                        null, null
                )
                embedBuilder.setThumbnail(guild.iconUrl)
                embedBuilder.setDescription(
                        toSend.replace("\$PING\$", user.asMention)
                                .replace("\$NAME\$", user.name)
                                .replace("\$GUILD\$", guild.name)
                                .replace("\$BOT\$", guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                                .replace("\\n", "\n")
                                .replace("  ", " ")
                                .trim()
                )
                if (file != null) {
                    embedBuilder.setImage("attachment://" + file.name)
                    channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name).queue()
                } else {
                    channel.sendMessageEmbeds(embedBuilder.build()).queue()
                }
            }
        }
    }
}