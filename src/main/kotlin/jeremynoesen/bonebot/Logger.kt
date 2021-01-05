package jeremynoesen.bonebot

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
}