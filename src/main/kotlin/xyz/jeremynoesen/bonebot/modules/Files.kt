package xyz.jeremynoesen.bonebot.modules

import net.dv8tion.jda.api.entities.Message
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

                if (message.contentRaw.trim().lowercase() == Commands.commandPrefix + "file") {
                    val r = Random()
                    val dir = File("resources/files")
                    if (dir.listFiles()!!.isNotEmpty()) {
                        var rand = r.nextInt(dir.listFiles()!!.size)
                        val prev = HashSet<Int>()
                        while (dir.listFiles()!![rand].isHidden || dir.listFiles()!![rand].isDirectory) {
                            rand = r.nextInt(dir.listFiles()!!.size)
                            if (prev.contains(rand)) continue
                            prev.add(rand)
                            if (prev.size == dir.listFiles()!!.size) {
                                message.channel.sendMessage("There are **no files** to send!").queue()
                                return
                            }
                        }
                        message.channel.sendFile(dir.listFiles()!![rand]).queue()
                    } else {
                        message.channel.sendMessage("There are **no files** to send!").queue()
                    }
                } else {
                    try {
                        val file = File("resources/files/" +
                                message.contentRaw.substring(Commands.commandPrefix.length + 4)
                                    .replace("..", "").replace("   ", " ")
                                    .replace("  ", " ").trim()
                        )
                        if (file.isDirectory || file.isHidden) {
                            message.channel.sendMessage("**Unknown file!**").queue()
                        } else {
                            message.channel.sendFile(file).queue()
                        }
                    } catch (e: Exception) {
                        message.channel.sendMessage("**Unknown file!**").queue()
                    }
                }
            } else {
                val remaining = ((cooldown * 1000) - (System.currentTimeMillis() - prevTime)) / 1000
                message.channel.sendMessage("Another file can be sent in **$remaining** seconds.").queue()
            }
        } catch (e: Exception) {
            message.channel.sendMessage("**An error occurred!** Please check the log file!").queue()
        }
    }
}