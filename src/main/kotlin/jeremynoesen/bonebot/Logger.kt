package jeremynoesen.bonebot

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
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
            val pw = PrintWriter(log)
            val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val now = LocalDateTime.now()
            pw.appendLine(now.format(dtf))
            pw.appendLine(exception.message)
            for (ste in exception.stackTrace) {
                pw.appendLine(ste.toString())
            }
            pw.appendLine()
            pw.close()
        } catch (fileNotFoundException: FileNotFoundException) {
            fileNotFoundException.printStackTrace()
        }
    }
}