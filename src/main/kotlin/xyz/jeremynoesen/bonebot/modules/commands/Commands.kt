package xyz.jeremynoesen.bonebot.modules.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.emoji.Emoji
import xyz.jeremynoesen.bonebot.BoneBot
import xyz.jeremynoesen.bonebot.Config
import xyz.jeremynoesen.bonebot.Messages
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.IllegalStateException
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * command handler with simple message responses
 *
 * @author Jeremy Noesen
 */
object Commands {

    /**
     * list of commands loaded from the command file
     */
    val commands = LinkedHashMap<String, Pair<String, String>>()

    /**
     * command prefix
     */
    var prefix = "bb"

    /**
     * cooldown for commands, in seconds
     */
    var cooldown = 5

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * last time the command handler sent a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * respond to a message if it is a command
     *
     * @param message message to check and respond to
     * @return true if command was performed
     */
    fun perform(message: Message): Boolean {
        var done = false
        try {
            if (message.contentDisplay.startsWith(prefix, true)) {
                thread {
                    while (!done) {
                        message.channel.sendTyping().queue()
                        Thread.sleep(5000);
                    }
                }
                if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    val label = message.contentDisplay.split(" ", "\n")[0]
                    when {
                        label.equals(prefix + Messages.memeCommand, true) && Memes.enabled -> {
                            Memes(message).generate()
                            done = true
                            return true
                        }
                        label.equals(prefix + Messages.quoteCommand, true) && Quotes.enabled -> {
                            Quotes.sendQuote(message)
                            done = true
                            return true
                        }
                        label.equals(prefix + Messages.fileCommand, true) && Files.enabled -> {
                            Files.sendFile(message)
                            done = true
                            return true
                        }
                        label.equals(prefix + Messages.helpCommand, true) -> {
                            sendHelp(message)
                            done = true
                            return true
                        }
                        else -> {
                            for (command in commands.keys) {
                                if (label.equals("$prefix${command.lowercase()}", true)) {
                                    sendCustomCommand(command, message)
                                    done = true
                                    return true
                                }
                            }
                            Messages.sendMessage(Messages.unknownCommand, message)
                            done = true
                            return true
                        }
                    }
                } else {
                    val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                    Messages.sendMessage(Messages.commandCooldown.replace("\$TIME\$", (remaining + 1).toString()), message)
                    done = true
                    return true
                }
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
            e.printStackTrace()
            done = true
        }
        return false
    }

    /**
     * build the help message embed
     *
     * @param message message initiating help embed
     */
    private fun sendHelp(message: Message) {

        var commandList = Messages.helpAbout

        var file: File? = null
        if (commandList.contains("\$FILE\$")) {
            val path = commandList.split("\$FILE\$")[1].trim()
            commandList = commandList.replace(
                    commandList.substring(
                            commandList.indexOf("\$FILE\$"),
                            commandList.lastIndexOf("\$FILE\$") + 6
                    ), ""
            )
                    .replace("  ", " ").trim()
            file = File(path)
            if (!file.exists() || file.isDirectory || file.isHidden) {
                file = null
            }
        }

        commandList += "\n\n" + Messages.helpFormat
                .replace("\$CMD\$", prefix + Messages.helpCommand)
                .replace("\$DESC\$", Messages.helpDescription) + "\n"

        if (Memes.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", prefix + Messages.memeCommand)
                .replace("\$DESC\$", Messages.memeDescription) + "\n"

        if (Files.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", prefix + Messages.fileCommand)
                .replace("\$DESC\$", Messages.fileDescription) + "\n"

        if (Quotes.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", prefix + Messages.quoteCommand)
                .replace("\$DESC\$", Messages.quoteDescription) + "\n"

        for (command in commands.keys) {
            commandList += Messages.helpFormat
                    .replace("\$CMD\$", prefix + command)
                    .replace("\$DESC\$", commands[command]!!.first) + "\n"
        }

        commandList = commandList
                .replace("\$PING\$", message.member!!.asMention)
                .replace("\$NAME\$", message.member!!.effectiveName)
                .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                .replace("\\n", "\n")
                .replace("  ", " ")
                .trim()
        try {
            commandList = commandList.replace("\$GUILD\$", message.guild.name)
        } catch (e: IllegalStateException) {
        }

        val embedBuilder = EmbedBuilder()
        var title = Messages.helpTitle
                .replace("\$NAME\$", message.member!!.effectiveName)
                .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                .replace("\\n", "\n")
                .replace("  ", " ")
                .trim()
        try {
            title = title.replace("\$GUILD\$", message.guild.name)
        } catch (e: IllegalStateException) {
        }

        embedBuilder.setAuthor(title, null, null)
        embedBuilder.setThumbnail(message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveAvatarUrl)
        embedBuilder.setColor(Config.embedColor)
        embedBuilder.setDescription("$commandList\n\n[**Source Code**](https://github.com/jeremynoesen/BoneBot)")

        if (file != null) {
            embedBuilder.setImage("attachment://" + file.name.replace(" ", "_"))
            message.channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name.replace(" ", "_")).queue()
        } else {
            message.channel.sendMessageEmbeds(embedBuilder.build()).queue()
        }
    }

    /**
     * send and process a custom command
     *
     * @param command command to run
     * @param message message containing command label and arguments
     */
    private fun sendCustomCommand(command: String, message: Message) {

        val randomCommands = commands[command]!!.second.split("\$||\$")
        val selectedCommand = randomCommands[Random.nextInt(randomCommands.size)]

        for (commandMessage in selectedCommand.split("\$&&\$")) {

            var toSend =
                    commandMessage
                            .replace("\$PING\$", message.member!!.asMention)
                            .replace("\$NAME\$", message.member!!.effectiveName)
                            .replace("\$BOT\$", message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)
                            .replace("\\n", "\n")
                            .replace("  ", " ")
                            .trim()
            try {
                toSend = toSend.replace("\$GUILD\$", message.guild.name)
            } catch (e: IllegalStateException) {
            }

            if (toSend.contains("\$CMD\$")) {
                val cmd = toSend.split("\$CMD\$")[1].trim()

                toSend = toSend.replace(
                        toSend.substring(
                                toSend.indexOf("\$CMD\$"),
                                toSend.lastIndexOf("\$CMD\$") + 5
                        ), ""
                )
                        .replace("  ", " ").trim()

                val procBuilder = if (System.getProperty("os.name").contains("windows", true)) {
                    ProcessBuilder("cmd.exe", "/c", cmd)
                } else {
                    ProcessBuilder("/bin/sh", "-c", cmd)
                }
                procBuilder.environment().putAll(setPathVariables(message))
                val stream = procBuilder.start().inputStream

                if (toSend.contains("\$CMDOUT\$")) {
                    val stdInput = BufferedReader(InputStreamReader(stream))
                    var output = ""
                    for (line in stdInput.readLines()) output += "$line\n"
                    output = output.removeSuffix("\n")
                    toSend = toSend.replace("\$CMDOUT\$", output)
                }
            }

            if (toSend.contains("\$REACT\$")) {
                val emote = toSend.split("\$REACT\$")[1].trim()
                toSend = toSend.replace(
                        toSend.substring(
                                toSend.indexOf("\$REACT\$"),
                                toSend.lastIndexOf("\$REACT\$") + 7
                        ), ""
                )
                        .replace("  ", " ").trim()
                message.addReaction(Emoji.fromFormatted(emote)).queue()
            }

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

            if (toSend.contains("\$EMBED\$")) {
                val title = toSend.split("\$EMBED\$")[1].trim()

                toSend = toSend.replace(
                        toSend.substring(
                                toSend.indexOf("\$EMBED\$"),
                                toSend.lastIndexOf("\$EMBED\$") + 7
                        ), ""
                )
                        .replace("  ", " ").trim()

                val embedBuilder = EmbedBuilder()
                embedBuilder.setColor(Config.embedColor)
                if (title.contains(message.member!!.effectiveName)) {
                    embedBuilder.setAuthor(title, null, message.member!!.effectiveAvatarUrl)
                } else if (title.contains(message.guild.getMember(BoneBot.JDA!!.selfUser)!!.effectiveName)) {
                    embedBuilder.setAuthor(title, null, BoneBot.JDA!!.selfUser.effectiveAvatarUrl)
                } else if (title.contains(message.guild.name)) {
                    embedBuilder.setAuthor(title, null, message.guild.iconUrl)
                } else {
                    embedBuilder.setAuthor(title, null)
                }

                if (toSend.contains("\$REPLY\$")) {
                    toSend = toSend.replace("\$REPLY\$", "")
                            .replace("  ", " ")
                    embedBuilder.setDescription(toSend)
                    if (file != null) {
                        embedBuilder.setImage("attachment://" + file.name.replace(" ", "_"))
                        message.channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name.replace(" ", "_")).reference(message)
                                .queue()
                    } else {
                        message.channel.sendMessageEmbeds(embedBuilder.build()).reference(message).queue()
                    }
                } else {
                    embedBuilder.setDescription(toSend)
                    if (file != null) {
                        embedBuilder.setImage("attachment://" + file.name.replace(" ", "_"))
                        message.channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name.replace(" ", "_")).queue()
                    } else {
                        message.channel.sendMessageEmbeds(embedBuilder.build()).queue()
                    }
                }
            } else {
                if (toSend.contains("\$REPLY\$")) {
                    toSend = toSend.replace("\$REPLY\$", "")
                            .replace("  ", " ")
                    if (file != null) {
                        if (toSend.isNotEmpty())
                            message.channel.sendMessage(toSend).addFile(file).reference(message)
                                    .queue()
                        else
                            message.channel.sendFile(file).reference(message).queue()
                    } else {
                        if (toSend.isNotEmpty())
                            message.channel.sendMessage(toSend).reference(message).queue()
                    }
                } else {
                    if (file != null) {
                        if (toSend.isNotEmpty())
                            message.channel.sendMessage(toSend).addFile(file).queue()
                        else
                            message.channel.sendFile(file).queue()
                    } else {
                        if (toSend.isNotEmpty())
                            message.channel.sendMessage(toSend).queue()
                    }
                }
            }
        }
    }

    /**
     * set all available path variables for custom commands
     *
     * @param message input message
     * @return map with added path variables
     */
    private fun setPathVariables(message: Message): Map<String, String> {
        val env = HashMap<String, String>()
        env["BB_CONTENT"] =
                message.contentDisplay.replace(message.contentDisplay.split(" ")[0], "").trim()
        env["BB_NAME"] = message.member!!.effectiveName
        env["BB_PING"] = message.member!!.asMention
        env["BB_ID"] = message.member!!.id
        env["BB_AVATAR"] = message.member!!.effectiveAvatarUrl + "?size=4096"

        if (message.member!!.roles.size > 0) {
            for (i in message.member!!.roles.indices) {
                env["BB_ROLE_$i"] = message.member!!.roles[i].id
            }
        }
        env["BB_ROLE_COUNT"] = message.member!!.roles.size.toString()

        if (message.attachments.size > 0) {
            for (i in message.attachments.indices) {
                env["BB_FILE_$i"] = message.attachments[i].url
            }
        }
        env["BB_FILE_COUNT"] = message.attachments.size.toString()

        var j = 0
        if (message.embeds.size > 0) {
            for (i in message.attachments.indices) {
                if (message.embeds[i].image != null) {
                    env["BB_EMBED_$j"] = message.embeds[i].image!!.url!!
                    j++
                }
            }
        }
        env["BB_EMBED_COUNT"] = j.toString()

        var k = 0
        for (word in message.contentDisplay.split(" ", "\n")) {
            if (word.contains("http://") || word.contains("https://")) {
                env["BB_URL_$k"] = word.replace("<", "").replace(">", "")
                k++
            }
        }
        env["BB_URL_COUNT"] = k.toString()

        var l = 0
        if (message.mentions.members.size > 0) {
            for (i in message.mentions.members.indices) {
                if (message.contentDisplay.split(message.mentions.members[i].user.name).size > 1 ||
                        message.contentDisplay.split(message.mentions.members[i].effectiveName).size > 1) {
                    env["BB_MENTION_${l}_NAME"] =
                            message.mentions.members[i].effectiveName
                    env["BB_MENTION_${l}_PING"] =
                            message.mentions.members[i].asMention
                    env["BB_MENTION_${l}_ID"] =
                            message.mentions.members[i].id
                    env["BB_MENTION_${l}_AVATAR"] =
                            message.mentions.members[i].effectiveAvatarUrl + "?size=4096"
                    if (message.mentions.members[i].roles.size > 0) {
                        for (r in message.mentions.members[i].roles.indices) {
                            env["BB_MENTION_${l}_ROLE_$r"] = message.mentions.members[i].roles[r].id
                        }
                    }
                    env["BB_MENTION_${l}_ROLE_COUNT"] = message.mentions.members[i].roles.size.toString()
                    l++
                }
            }
        }
        env["BB_MENTION_COUNT"] = l.toString()

        if (message.referencedMessage != null) {
            val reply = message.referencedMessage!!
            env["BB_REPLY_CONTENT"] = reply.contentDisplay
            env["BB_REPLY_NAME"] = reply.member!!.effectiveName
            env["BB_REPLY_PING"] = reply.member!!.asMention
            env["BB_REPLY_ID"] = reply.member!!.id
            env["BB_REPLY_AVATAR"] = reply.member!!.effectiveAvatarUrl + "?size=4096"

            if (reply.member!!.roles.size > 0) {
                for (i in reply.member!!.roles.indices) {
                    env["BB_REPLY_ROLE_$i"] = reply.member!!.roles[i].id
                }
            }
            env["BB_REPLY_ROLE_COUNT"] = reply.member!!.roles.size.toString()

            if (reply.attachments.size > 0) {
                for (i in reply.attachments.indices) {
                    env["BB_REPLY_FILE_$i"] = reply.attachments[i].url
                }
            }
            env["BB_REPLY_FILE_COUNT"] = reply.attachments.size.toString()

            j = 0
            if (reply.embeds.size > 0) {
                for (i in reply.attachments.indices) {
                    if (reply.embeds[i].image != null) {
                        env["BB_REPLY_EMBED_$j"] = reply.embeds[i].image!!.url!!
                        j++
                    }
                }
            }
            env["BB_REPLY_EMBED_COUNT"] = j.toString()

            k = 0
            for (word in reply.contentDisplay.split(" ", "\n")) {
                if (word.contains("http://") || word.contains("https://")) {
                    env["BB_REPLY_URL_$k"] = word.replace("<", "").replace(">", "")
                    k++
                }
            }
            env["BB_REPLY_URL_COUNT"] = k.toString()

            l = 0
            if (reply.mentions.members.size > 0) {
                for (i in reply.mentions.members.indices) {
                    if (reply.contentDisplay.split(reply.mentions.members[i].user.name).size > 1 ||
                            reply.contentDisplay.split(reply.mentions.members[i].effectiveName).size > 1) {
                        env["BB_REPLY_MENTION_${l}_NAME"] =
                                reply.mentions.members[i].effectiveName
                        env["BB_REPLY_MENTION_${l}_PING"] =
                                reply.mentions.members[i].asMention
                        env["BB_REPLY_MENTION_${l}_ID"] =
                                reply.mentions.members[i].id
                        env["BB_REPLY_MENTION_${l}_AVATAR"] =
                                reply.mentions.members[i].effectiveAvatarUrl + "?size=4096"
                        if (reply.mentions.members[i].roles.size > 0) {
                            for (r in reply.mentions.members[i].roles.indices) {
                                env["BB_REPLY_MENTION_${l}_ROLE_$r"] = reply.mentions.members[i].roles[r].id
                            }
                        }
                        env["BB_REPLY_MENTION_${l}_ROLE_COUNT"] = reply.mentions.members[i].roles.size.toString()
                        l++
                    }
                }
            }
            env["BB_REPLY_MENTION_COUNT"] = l.toString()
        }
        return env
    }
}