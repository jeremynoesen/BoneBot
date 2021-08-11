package xyz.jeremynoesen.bonebot

import xyz.jeremynoesen.bonebot.modules.*
import java.awt.Color
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
     * color used for embeds
     */
    var embedColor = Color(253, 6, 5)

    /**
     * load config values, write default config if missing
     */
    fun loadData() {
        try {
            File("resources").mkdir()
            File("resources/memeimages").mkdir()
            File("resources/files").mkdir()

            val fileScanner = Scanner(File("resources/config.txt"))
            while (fileScanner.hasNextLine()) {
                val lineScanner = Scanner(fileScanner.nextLine())
                when (lineScanner.next()) {
                    "responder-enabled:" -> {
                        Responder.enabled = lineScanner.nextBoolean()
                    }
                    "reactor-enabled:" -> {
                        Reactor.enabled = lineScanner.nextBoolean()
                    }
                    "memes-enabled:" -> {
                        Memes.enabled = lineScanner.nextBoolean()
                    }
                    "statuses-enabled:" -> {
                        Statuses.enabled = lineScanner.nextBoolean()
                    }
                    "commands-enabled:" -> {
                        Commands.enabled = lineScanner.nextBoolean()
                    }
                    "quotes-enabled:" -> {
                        Quotes.enabled = lineScanner.nextBoolean()
                    }
                    "files-enabled:" -> {
                        Files.enabled = lineScanner.nextBoolean()
                    }
                    "responder-cooldown:" -> {
                        Responder.cooldown = lineScanner.nextInt()
                    }
                    "reactor-cooldown:" -> {
                        Reactor.cooldown = lineScanner.nextInt()
                    }
                    "meme-cooldown:" -> {
                        Memes.cooldown = lineScanner.nextInt()
                    }
                    "quote-cooldown:" -> {
                        Quotes.cooldown = lineScanner.nextInt()
                    }
                    "file-cooldown:" -> {
                        Files.cooldown = lineScanner.nextInt()
                    }
                    "status-delay:" -> {
                        Statuses.delay = lineScanner.nextInt()
                    }
                    "command-cooldown:" -> {
                        Commands.cooldown = lineScanner.nextInt()
                    }
                    "command-prefix:" -> {
                        Commands.commandPrefix = lineScanner.next().lowercase()
                    }
                    "embed-color:" -> {
                        embedColor = Color.decode(lineScanner.next())
                    }
                    "typing-speed:" -> {
                        Responder.typingSpeed = lineScanner.nextLong()
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
            pw.println("responder-enabled: ${Responder.enabled}")
            pw.println("responder-cooldown: ${Responder.cooldown}")
            pw.println("reactor-enabled: ${Reactor.enabled}")
            pw.println("reactor-cooldown: ${Reactor.cooldown}")
            pw.println("memes-enabled: ${Memes.enabled}")
            pw.println("meme-cooldown: ${Memes.cooldown}")
            pw.println("statuses-enabled: ${Statuses.enabled}")
            pw.println("status-delay: ${Statuses.delay}")
            pw.println("commands-enabled: ${Commands.enabled}")
            pw.println("command-cooldown: ${Commands.cooldown}")
            pw.println("command-prefix: ${Commands.commandPrefix}")
            pw.println("quotes-enabled: ${Quotes.enabled}")
            pw.println("quote-cooldown: ${Quotes.cooldown}")
            pw.println("files-enabled: ${Files.enabled}")
            pw.println("file-cooldown: ${Files.cooldown}")
            pw.println("embed-color: #fd0605")
            pw.println("typing-speed: ${Responder.typingSpeed}")
            pw.println("bot-token: TOKEN")
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }

        try {
            loadData("resources/memetexts.txt", Memes.texts)
            loadData("resources/statuses.txt", Statuses.statuses)
            loadData("resources/commands.txt", Commands.commands)
            loadData("resources/responses.txt", Responder.responses)
            loadData("resources/reactions.txt", Reactor.reactions)
            loadData("resources/quotes.txt", Quotes.quotes)
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

    /**
     * load all data from file to a hashmap, where a string points to another
     *
     * @param filePath path to file holding the data
     * @param map     hashmap to load data into
     */
    private fun loadData(filePath: String, map: LinkedHashMap<String, String>) {
        try {
            val fileScanner = Scanner(File(filePath))
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine()
                if (line.isNotBlank()) {
                    val parts = line.split(" // ")
                    map[parts[0].lowercase()] = parts[1]
                }
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File(filePath)
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }
    }

    /**
     * load all data from file to a hashmap, where a string points to a pair of strings
     *
     * @param filePath path to file holding the data
     * @param map     hashmap to load data into
     */
    @JvmName("loadData1")
    private fun loadData(filePath: String, map: LinkedHashMap<String, Pair<String, String>>) {
        try {
            val fileScanner = Scanner(File(filePath))
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine()
                if (line.isNotBlank()) {
                    val parts = line.split(" // ")
                    map[parts[0].lowercase()] = Pair(parts[1], parts[2])
                }
            }
            fileScanner.close()
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