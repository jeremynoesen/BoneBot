package xyz.jeremynoesen.bonebot

import xyz.jeremynoesen.bonebot.modules.*
import xyz.jeremynoesen.bonebot.modules.commands.Commands
import xyz.jeremynoesen.bonebot.modules.commands.Files
import xyz.jeremynoesen.bonebot.modules.commands.Memes
import xyz.jeremynoesen.bonebot.modules.commands.Quotes
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
    var embedColor = Color(0xFD0605)

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
                    "responder-cooldown:" -> {
                        Responder.cooldown = lineScanner.nextInt()
                    }
                    "responder-delay:" -> {
                        Responder.delay = lineScanner.nextLong()
                    }
                    "typing-speed:" -> {
                        Responder.typingSpeed = lineScanner.nextLong()
                    }
                    "reactor-enabled:" -> {
                        Reactor.enabled = lineScanner.nextBoolean()
                    }
                    "reactor-cooldown:" -> {
                        Reactor.cooldown = lineScanner.nextInt()
                    }
                    "reactor-delay:" -> {
                        Reactor.delay = lineScanner.nextLong()
                    }
                    "memes-enabled:" -> {
                        Memes.enabled = lineScanner.nextBoolean()
                    }
                    "meme-cooldown:" -> {
                        Memes.cooldown = lineScanner.nextInt()
                    }
                    "meme-size:" -> {
                        Memes.size = lineScanner.nextInt()
                    }
                    "meme-font-scale:" -> {
                        Memes.fontScale = lineScanner.nextFloat()
                    }
                    "statuses-enabled:" -> {
                        Statuses.enabled = lineScanner.nextBoolean()
                    }
                    "status-delay:" -> {
                        Statuses.delay = lineScanner.nextInt()
                    }
                    "commands-enabled:" -> {
                        Commands.enabled = lineScanner.nextBoolean()
                    }
                    "command-cooldown:" -> {
                        Commands.cooldown = lineScanner.nextInt()
                    }
                    "command-prefix:" -> {
                        Commands.commandPrefix = lineScanner.next().lowercase()
                    }
                    "quotes-enabled:" -> {
                        Quotes.enabled = lineScanner.nextBoolean()
                    }
                    "quote-cooldown:" -> {
                        Quotes.cooldown = lineScanner.nextInt()
                    }
                    "files-enabled:" -> {
                        Files.enabled = lineScanner.nextBoolean()
                    }
                    "file-cooldown:" -> {
                        Files.cooldown = lineScanner.nextInt()
                    }
                    "welcomer-enabled:" -> {
                        Welcomer.enabled = lineScanner.nextBoolean()
                    }
                    "listen-to-bots:" -> {
                        Listener.listenToBots = lineScanner.nextBoolean()
                    }
                    "max-threads:" -> {
                        Listener.maxThreads = lineScanner.nextInt()
                    }
                    "embed-color:" -> {
                        embedColor = Color.decode(lineScanner.next())
                    }
                    "bot-token:" -> {
                        botToken = lineScanner.next()
                    }
                }
                lineScanner.close()
            }
            fileScanner.close()

        } catch (e: FileNotFoundException) {}

        val file = File("resources/config.txt")
        file.delete()
        val pw = PrintWriter(file)
        pw.println("responder-enabled: ${Responder.enabled}")
        pw.println("responder-cooldown: ${Responder.cooldown}")
        pw.println("responder-delay: ${Responder.delay}")
        pw.println("typing-speed: ${Responder.typingSpeed}")
        pw.println("reactor-enabled: ${Reactor.enabled}")
        pw.println("reactor-cooldown: ${Reactor.cooldown}")
        pw.println("reactor-delay: ${Reactor.delay}")
        pw.println("memes-enabled: ${Memes.enabled}")
        pw.println("meme-cooldown: ${Memes.cooldown}")
        pw.println("meme-size: ${Memes.size}")
        pw.println("meme-font-scale: ${Memes.fontScale}")
        pw.println("statuses-enabled: ${Statuses.enabled}")
        pw.println("status-delay: ${Statuses.delay}")
        pw.println("commands-enabled: ${Commands.enabled}")
        pw.println("command-cooldown: ${Commands.cooldown}")
        pw.println("command-prefix: ${Commands.commandPrefix}")
        pw.println("quotes-enabled: ${Quotes.enabled}")
        pw.println("quote-cooldown: ${Quotes.cooldown}")
        pw.println("files-enabled: ${Files.enabled}")
        pw.println("file-cooldown: ${Files.cooldown}")
        pw.println("welcomer-enabled: ${Welcomer.enabled}")
        pw.println("listen-to-bots: ${Listener.listenToBots}")
        pw.println("max-threads: ${Listener.maxThreads}")
        pw.println("embed-color: ${String.format("#%02x%02x%02x", embedColor.red, embedColor.green, embedColor.blue)}")
        pw.println("bot-token: $botToken")
        pw.close()

        loadData("resources/memetexts.txt", Memes.texts)
        loadData("resources/statuses.txt", Statuses.statuses)
        loadData("resources/commands.txt", Commands.commands)
        loadData("resources/responses.txt", Responder.responses)
        loadData("resources/reactions.txt", Reactor.reactions)
        loadData("resources/quotes.txt", Quotes.quotes)

        Messages.loadMessages()
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
                    val parts = line.split(": ", limit = 2)
                    map[parts[0]] = parts[1]
                }
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File(filePath)
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
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
                    val parts = line.split(": ", limit = 3)
                    map[parts[0]] = Pair(parts[1], parts[2])
                }
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File(filePath)
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        }
    }
}