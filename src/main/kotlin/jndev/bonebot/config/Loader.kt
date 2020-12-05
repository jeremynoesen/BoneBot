package jndev.bonebot.config

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

/**
 * handles loading string files into string lists
 */
object Loader {
    /**
     * load all data from file to array list
     *
     * @param filePath path to file holding the data
     * @param list     list to load data into
     */
    fun loadData(filePath: String, list: ArrayList<String>) {
        try {
            val fileScanner = Scanner(File(filePath))
            list.clear()
            while (fileScanner.hasNextLine()) list.add(fileScanner.nextLine())
            fileScanner.close()
            list.trimToSize()
        } catch (e: FileNotFoundException) {
            val file = File(filePath)
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        }
    }
}