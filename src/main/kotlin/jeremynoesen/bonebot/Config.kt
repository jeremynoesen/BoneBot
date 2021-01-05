package jeremynoesen.bonebot

import jeremynoesen.bonebot.modules.*
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

/**
 * class to handle config values
 *
 * @author Jeremy Noesen
 */
object Config {

    /**
     * bot token
     */
    var botToken = "TOKEN"

    /**
     * load config values, write default config if missing
     */
    fun loadData() {
        try {
            File("resources").mkdir()
            File("resources/images").mkdir()

            val fileScanner = Scanner(File("resources/config.txt"))
            while (fileScanner.hasNextLine()) {
                val lineScanner = Scanner(fileScanner.nextLine())
                when (lineScanner.next()) {
                    "response-cooldown:" -> {
                        Responder.cooldown = lineScanner.nextInt()
                    }
                    "react-cooldown:" -> {
                        Reactor.cooldown = lineScanner.nextInt()
                    }
                    "meme-cooldown:" -> {
                        Meme.cooldown = lineScanner.nextInt()
                    }
                    "status-cooldown:" -> {
                        Status.cooldown = lineScanner.nextInt()
                    }
                    "command-cooldown:" -> {
                        Command.cooldown = lineScanner.nextInt()
                    }
                    "command-prefix:" -> {
                        Command.commandPrefix = lineScanner.next()
                    }
                    "bot-token:" -> {
                        botToken = lineScanner.next()
                    }
                }
                lineScanner.close()
            }
            fileScanner.close()

        } catch (e: FileNotFoundException) {
            val file = File("resources/config.txt")
            val pw = PrintWriter(file)
            pw.println("response-cooldown: ${Responder.cooldown}")
            pw.println("react-cooldown: ${Reactor.cooldown}")
            pw.println("meme-cooldown: ${Meme.cooldown}")
            pw.println("status-cooldown: ${Status.cooldown}")
            pw.println("command-cooldown: ${Command.cooldown}")
            pw.println("command-prefix: ${Command.commandPrefix}")
            pw.println("botToken: $botToken")
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }

        try {
            loadData("resources/responses.txt", Responder.responses)
            loadData("resources/texts.txt", Meme.texts)
            loadData("resources/statuses.txt", Status.statuses)
        } catch (e: Exception) {
            Logger.log(e)
        }
    }

    /**
     * load all data from file to array list
     *
     * @param filePath path to file holding the data
     * @param list     list to load data into
     */
    private fun loadData(filePath: String, list: ArrayList<String>) {
        try {
            val fileScanner = Scanner(File(filePath))
            list.clear()
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine()
                if (line.isNotBlank())
                    list.add(line)
            }
            fileScanner.close()
            list.trimToSize()
        } catch (e: FileNotFoundException) {
            val file = File(filePath)
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}