package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
import xyz.jeremynoesen.bonebot.Messages
import java.io.File
import java.util.*
import kotlin.collections.HashSet

/**
 * file module to send files from a directory
 *
 * @author Jeremy Noesen
 */
object Files {

    /**
     * cooldown for file module, in seconds
     */
    var cooldown = 5

    /**
     * whether this module is enabled or not
     */
    var enabled = true

    /**
     * last time a file was sent in milliseconds
     */
    private var prevTime = 0L

    /**
     * send a random file from the files folder
     *
     * @param message message initiating action
     */
    fun sendFile(message: Message) {
        try {
            if ((System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                prevTime = System.currentTimeMillis()

                if (message.contentRaw.trim()
                        .lowercase() == Commands.commandPrefix + Messages.fileCommand.lowercase()
                ) {
                    sendRandomFile(message, File("resources/files"))
                } else {
                    try {
                        val file = File(
                            "resources/files/" +
                                    message.contentRaw.substring(Commands.commandPrefix.length + Messages.fileCommand.length)
                                        .replace("..", "").replace("   ", " ")
                                        .replace("  ", " ").trim()
                        )
                        if (!file.exists() || file.isHidden) {
                            message.channel.sendMessage(Messages.unknownFile).queue()
                            return
                        }
                        if (file.isDirectory) {
                            sendRandomFile(message, file)
                        }
                    } catch (e: Exception) {
                        message.channel.sendMessage(Messages.unknownFile).queue()
                    }
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                message.channel.sendMessage(Messages.fileCooldown.replace("\$TIME\$", remaining.toString())).queue()
            }
        } catch (e: Exception) {
            message.channel.sendMessage(Messages.error).queue()
            e.printStackTrace()
        }
    }

    /**
     * send a random file from a directory
     *
     * @param message message initiating action
     * @param dir directory to grab files from
     */
    private fun sendRandomFile(message: Message, dir: File) {
        val r = Random()
        if (dir.listFiles()!!.isNotEmpty()) {
            var rand = r.nextInt(dir.listFiles()!!.size)
            val prev = HashSet<Int>()
            while (dir.listFiles()!![rand].isHidden || dir.listFiles()!![rand].isDirectory) {
                rand = r.nextInt(dir.listFiles()!!.size)
                if (prev.contains(rand)) continue
                prev.add(rand)
                if (prev.size == dir.listFiles()!!.size) {
                    message.channel.sendMessage(Messages.noFiles).queue()
                    return
                }
            }
            message.channel.sendFile(dir.listFiles()!![rand]).queue()
        } else {
            message.channel.sendMessage(Messages.noFiles).queue()
        }
    }
}