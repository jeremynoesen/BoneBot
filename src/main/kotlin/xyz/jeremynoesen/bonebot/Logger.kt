package xyz.jeremynoesen.bonebot

import net.dv8tion.jda.api.entities.MessageChannel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * logger to write errors to a log file
 *
 * @author Jeremy Noesen
 */
object Logger {

    /**
     * write exception to a log file with time stamp
     *
     * @param exception exception to log
     */
    fun log(exception: Exception) {
        val log = File("log.txt")
        try {
            exception.printStackTrace()
            val fw = FileWriter(log, true)
            val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val now = LocalDateTime.now()
            fw.write(now.format(dtf) + "\n")
            fw.write(exception.message + "\n")
            for (ste in exception.stackTrace)
                for (line in ste.toString().split("\n"))
                    fw.write("$line\n")
            fw.write("\n")
            fw.close()
        } catch (fileNotFoundException: FileNotFoundException) {
            fileNotFoundException.printStackTrace()
        }
    }

    /**
     * write exception to a log file with time stamp, and send a message to a channel that an error occurred
     *
     * @param exception exception to log
     */
    fun log(exception: Exception, messageChannel: MessageChannel) {
        log(exception)
        messageChannel.sendMessage("An error occurred. Please check the log file!").queue()
    }
}