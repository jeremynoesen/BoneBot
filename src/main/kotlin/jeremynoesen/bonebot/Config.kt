package jeremynoesen.bonebot

import jeremynoesen.bonebot.Logger
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
     * cooldown for responder, in seconds
     */
    var responseCooldown = 180

    /**
     * cooldown for reactor, in seconds
     */
    var reactCooldown = 60

    /**
     * cooldown for meme generator, in seconds
     */
    var memeCooldown = 5

    /**
     * cooldown for status updater, in seconds
     */
    var statusCooldown = 60

    /**
     * cooldown for commands, in seconds
     */
    var commandCooldown = 5

    /**
     * command prefix
     */
    var commandPrefix = "bb"

    /**
     * bot token
     */
    var botToken = ""

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
                        responseCooldown = lineScanner.nextInt()
                    }
                    "react-cooldown:" -> {
                        reactCooldown = lineScanner.nextInt()
                    }
                    "meme-cooldown:" -> {
                        memeCooldown = lineScanner.nextInt()
                    }
                    "status-cooldown:" -> {
                        statusCooldown = lineScanner.nextInt()
                    }
                    "command-cooldown:" -> {
                        commandCooldown = lineScanner.nextInt()
                    }
                    "command-prefix:" -> {
                        commandPrefix = lineScanner.next()
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
            pw.println("response-cooldown: $reactCooldown")
            pw.println("react-cooldown: $reactCooldown")
            pw.println("meme-cooldown: $memeCooldown")
            pw.println("status-cooldown: $statusCooldown")
            pw.println("command-cooldown: $commandCooldown")
            pw.println("command-prefix: $commandPrefix")
            pw.println("botToken: TOKEN_HERE")
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }

        try {
            loadData("resources/commands.txt", Command.commands)
            loadData("resources/responses.txt", Responder.responses)
            loadData("resources/texts.txt", Meme.texts)
            loadData("resources/statuses.txt", Status.statuses)
            loadData("resources/reactions.txt", Reactor.reactions)
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
                    list.add(fileScanner.nextLine())
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