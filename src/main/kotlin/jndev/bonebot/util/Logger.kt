package jndev.bonebot.util

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * logger to write errors to a log file
 *
 * @author JNDev (Jeremaster101)
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
            pw.println(now.format(dtf))
            pw.println(exception.message)
            for (ste in exception.stackTrace) {
                pw.println(ste.toString())
            }
            pw.println()
            pw.close()
        } catch (fileNotFoundException: FileNotFoundException) {
            fileNotFoundException.printStackTrace()
        }
    }
}