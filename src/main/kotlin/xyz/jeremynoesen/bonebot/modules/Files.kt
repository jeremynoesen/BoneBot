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

                if (message.contentDisplay.trim()
                        .lowercase() == Commands.commandPrefix + Messages.fileCommand.lowercase()
                ) {
                    sendRandomFile(message, File("resources/files"))
                } else {
                    try {
                        val file = File(
                            "resources/files/" +
                                    message.contentDisplay.substring(Commands.commandPrefix.length + Messages.fileCommand.length)
                                        .replace("..", "").replace("   ", " ")
                                        .replace("  ", " ").trim()
                        )
                        if (!file.exists() || file.isHidden) {
                            Messages.sendMessage(Messages.unknownFile, message)
                            return
                        }
                        if (file.isDirectory) {
                            sendRandomFile(message, file)
                        } else {
                            message.channel.sendFile(file).queue()
                        }
                    } catch (e: Exception) {
                        Messages.sendMessage(Messages.unknownFile, message)
                    }
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                Messages.sendMessage(Messages.fileCooldown.replace("\$TIME\$", remaining.toString()), message)
            }
        } catch (e: Exception) {
            Messages.sendMessage(Messages.error, message)
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
                    Messages.sendMessage(Messages.noFiles, message)
                    return
                }
            }
            message.channel.sendFile(dir.listFiles()!![rand]).queue()
        } else {
            Messages.sendMessage(Messages.noFiles, message)
        }
    }
}