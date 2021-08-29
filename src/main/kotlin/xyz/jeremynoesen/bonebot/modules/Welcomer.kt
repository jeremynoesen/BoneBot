package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import xyz.jeremynoesen.bonebot.Config
import java.io.File

/**
 * module to send a welcome message to new members
 */
object Welcomer {

    /**
     * message to show in welcome message embed
     */
    var message = "Welcome \$USER\$ to **\$GUILD\$**!"

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
                    toSend = toSend.replace("\$FILE\$", "").replace(path, "")
                    file = File(path)
                    if (file.isDirectory || file.isHidden) {
                        file = null
                    }
                }

                val embedBuilder = EmbedBuilder()
                embedBuilder.setColor(Config.embedColor)
                embedBuilder.setAuthor(guild.name, null, guild.iconUrl)
                embedBuilder.setDescription(
                    toSend.replace("\$USER\$", user.asMention).replace("\$GUILD\$", guild.name)
                )
                if (file != null) {
                    embedBuilder.setImage("attachment://welcome.png")
                    channel.sendMessage(embedBuilder.build()).addFile(file, "welcome.png").queue()
                } else {
                    channel.sendMessage(embedBuilder.build()).queue()
                }
            }
        }
    }
}