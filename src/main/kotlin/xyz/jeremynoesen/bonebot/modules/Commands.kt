package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
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
    var commandPrefix = "bb"

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
            if (message.contentDisplay.startsWith(commandPrefix, true)) {
                thread {
                    while (!done) {
                        message.channel.sendTyping().queue()
                        Thread.sleep(9000);
                    }
                }
                if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    val label = message.contentDisplay.split(" ", "\n")[0]
                    when {
                        label.equals(commandPrefix + Messages.memeCommand, true) && Memes.enabled -> {
                            Memes(message).generate()
                            done = true
                            return true
                        }
                        label.equals(commandPrefix + Messages.quoteCommand, true) && Quotes.enabled -> {
                            Quotes.sendQuote(message)
                            done = true
                            return true
                        }
                        label.equals(commandPrefix + Messages.fileCommand, true) && Files.enabled -> {
                            Files.sendFile(message)
                            done = true
                            return true
                        }
                        label.equals(commandPrefix + Messages.helpCommand, true) -> {
                            message.channel.sendMessageEmbeds(buildHelpEmbed(message)).queue()
                            done = true
                            return true
                        }
                        else -> {
                            for (command in commands.keys) {
                                if (label.equals("$commandPrefix${command.lowercase()}", true)) {
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
     * @return build help message embed
     */
    private fun buildHelpEmbed(message: Message): MessageEmbed {
        var commandList = Messages.helpAbout + "\n\n" + Messages.helpFormat
                .replace("\$CMD\$", commandPrefix + Messages.helpCommand)
                .replace("\$DESC\$", Messages.helpDescription) + "\n"

        if (Memes.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", commandPrefix + Messages.memeCommand)
                .replace("\$DESC\$", Messages.memeDescription) + "\n"

        if (Files.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", commandPrefix + Messages.fileCommand)
                .replace("\$DESC\$", Messages.fileDescription) + "\n"

        if (Quotes.enabled) commandList += Messages.helpFormat
                .replace("\$CMD\$", commandPrefix + Messages.quoteCommand)
                .replace("\$DESC\$", Messages.quoteDescription) + "\n"

        for (command in commands.keys) {
            commandList += Messages.helpFormat
                    .replace("\$CMD\$", commandPrefix + command)
                    .replace("\$DESC\$", commands[command]!!.first) + "\n"
        }

        commandList = commandList
                .replace("\$USER\$", message.author.asMention)
                .replace("\$NAME\$", message.author.name)
                .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                .replace("\\n", "\n")
        try {
            commandList = commandList.replace("\$GUILD\$", message.guild.name)
        } catch (e: IllegalStateException) {
        }

        val embedBuilder = EmbedBuilder()
        var title = Messages.helpTitle
                .replace("\$NAME\$", message.author.name)
                .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
        try {
            title = title.replace("\$GUILD\$", message.guild.name)
        } catch (e: IllegalStateException) {
        }
        embedBuilder.setAuthor(title, null, BoneBot.JDA!!.selfUser.avatarUrl)
        embedBuilder.setColor(Config.embedColor)
        embedBuilder.setDescription("$commandList\n[**Source Code**](https://github.com/jeremynoesen/BoneBot)")
        return embedBuilder.build()
    }

    /**
     * send and process a custom command
     *
     * @param command command to run
     * @param message message containing command label and arguments
     */
    private fun sendCustomCommand(command: String, message: Message) {

        val randomCommands = commands[command]!!.second.split(" \$||\$ ")
        val selectedCommand = randomCommands[Random.nextInt(randomCommands.size)]

        for (commandMessage in selectedCommand.split(" \$&&\$ ")) {

            var toSend =
                    commandMessage
                            .replace("\$USER\$", message.author.asMention)
                            .replace("\$NAME\$", message.author.name)
                            .replace("\$BOT\$", BoneBot.JDA!!.selfUser.name)
                            .replace("\\n", "\n")
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
                if (title.contains(message.author.name)) {
                    embedBuilder.setAuthor(title, null, message.author.effectiveAvatarUrl)
                } else if (title.contains(BoneBot.JDA!!.selfUser.name)) {
                    embedBuilder.setAuthor(title, null, BoneBot.JDA!!.selfUser.effectiveAvatarUrl)
                } else {
                    embedBuilder.setAuthor(title, null)
                }

                if (toSend.contains("\$REPLY\$")) {
                    toSend = toSend.replace("\$REPLY\$", "")
                            .replace("  ", " ")
                    embedBuilder.setDescription(toSend)
                    if (file != null) {
                        embedBuilder.setImage("attachment://" + file.name)
                        message.channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name).reference(message)
                                .queue()
                    } else {
                        message.channel.sendMessageEmbeds(embedBuilder.build()).reference(message).queue()
                    }
                } else {
                    embedBuilder.setDescription(toSend)
                    if (file != null) {
                        embedBuilder.setImage("attachment://" + file.name)
                        message.channel.sendMessageEmbeds(embedBuilder.build()).addFile(file, file.name).queue()
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
        env["BB_INPUT"] =
                message.contentDisplay.replace(message.contentDisplay.split(" ")[0], "").trim()
        env["BB_USER"] = message.author.name
        env["BB_ID"] = message.author.id
        env["BB_AVATAR"] = message.author.effectiveAvatarUrl + "?size=4096"
        if (message.attachments.size > 0)
            env["BB_FILE"] = message.attachments[0].url
        if (message.embeds.size > 0 && message.embeds[0].image != null)
            env["BB_EMBED"] = message.embeds[0].image!!.url!!

        if (message.mentions.users.size > 0 &&
                message.contentDisplay.split(message.mentions.users[message.mentions.users.size - 1].name).size > 1
        ) {
            env["BB_MENTION_USER"] =
                    message.mentions.users[message.mentions.users.size - 1].name
            env["BB_MENTION_ID"] =
                    message.mentions.users[message.mentions.users.size - 1].id
            env["BB_MENTION_AVATAR"] =
                    message.mentions.users[message.mentions.users.size - 1].effectiveAvatarUrl + "?size=4096"
        }

        if (message.referencedMessage != null) {
            val reply = message.referencedMessage!!
            env["BB_REPLY_INPUT"] = reply.contentDisplay
            env["BB_REPLY_USER"] = reply.author.name
            env["BB_REPLY_ID"] = reply.author.id
            env["BB_REPLY_AVATAR"] = reply.author.effectiveAvatarUrl + "?size=4096"
            if (reply.attachments.size > 0)
                env["BB_REPLY_FILE"] = reply.attachments[0].url
            if (reply.embeds.size > 0 && reply.embeds[0].image != null)
                env["BB_REPLY_EMBED"] = reply.embeds[0].image!!.url!!

            if (reply.mentions.users.size > 0 &&
                    reply.contentDisplay.split(reply.mentions.users[reply.mentions.users.size - 1].name).size > 1
            ) {
                env["BB_REPLY_MENTION_USER"] =
                        reply.mentions.users[reply.mentions.users.size - 1].name
                env["BB_REPLY_MENTION_ID"] =
                        reply.mentions.users[reply.mentions.users.size - 1].id
                env["BB_REPLY_MENTION_AVATAR"] =
                        reply.mentions.users[reply.mentions.users.size - 1].effectiveAvatarUrl + "?size=4096"
            }
        }
        return env
    }
}