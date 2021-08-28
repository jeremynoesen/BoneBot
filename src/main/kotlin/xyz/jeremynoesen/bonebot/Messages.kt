package xyz.jeremynoesen.bonebot

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

/**
 * all messages used throughout the bot
 */
object Messages {
    var helpTitle = "\$BOT\$ Help"
    var helpFormat = "• **`\$CMD\$`**: \$DESC\$"
    var helpDescription = "Show this message."
    var memeDescription = "Generate a meme."
    var fileDescription = "Send a random file."
    var quoteDescription = "Send a random quote."
    var helpCommand = "help"
    var memeCommand = "meme"
    var fileCommand = "file"
    var quoteCommand = "quote"
    var error = "**An error occurred!** Please check the log file!"
    var unknownCommand = "**Unknown command!**"
    var noFiles = "There are **no files** to send!"
    var unknownFile = "**Unknown file!**"
    var memeTitle = "\$USER\$ generated a meme:"
    var memeInputMissing = "Please provide the missing **text** and/or **image**!"
    var noQuotes = "There are no quotes to show!"
    var memeCooldown = "Another meme can be generated in **\$TIME\$** seconds."
    var quoteCooldown = "Another quote can be sent in **\$TIME\$** seconds."
    var fileCooldown = "Another file can be sent in **\$TIME\$** seconds."
    var commandCooldown = "Commands can be used again in **\$TIME\$** seconds."

    fun loadMessages() {
        try {
            val fileScanner = Scanner(File("resources/messages.txt"))
            while (fileScanner.hasNextLine()) {
                val lineScanner = Scanner(fileScanner.nextLine())
                when (lineScanner.next()) {
                    "help-title:" -> {
                        helpTitle = lineScanner.nextLine().trim()
                    }
                    "help-format:" -> {
                        helpFormat = lineScanner.nextLine().trim()
                    }
                    "help-description:" -> {
                        helpDescription = lineScanner.nextLine().trim()
                    }
                    "meme-description:" -> {
                        memeDescription = lineScanner.nextLine().trim()
                    }
                    "file-description:" -> {
                        fileDescription = lineScanner.nextLine().trim()
                    }
                    "quote-description:" -> {
                        quoteDescription = lineScanner.nextLine().trim()
                    }
                    "help-command:" -> {
                        helpCommand = lineScanner.nextLine().trim()
                    }
                    "meme-command:" -> {
                        memeCommand = lineScanner.nextLine().trim()
                    }
                    "file-command:" -> {
                        fileCommand = lineScanner.nextLine().trim()
                    }
                    "quote-command:" -> {
                        quoteCommand = lineScanner.nextLine().trim()
                    }
                    "error:" -> {
                        error = lineScanner.nextLine().trim()
                    }
                    "unknown-command:" -> {
                        unknownCommand = lineScanner.nextLine().trim()
                    }
                    "no-files:" -> {
                        noFiles = lineScanner.nextLine().trim()
                    }
                    "unknown-file:" -> {
                        unknownFile = lineScanner.nextLine().trim()
                    }
                    "meme-title:" -> {
                        memeTitle = lineScanner.nextLine().trim()
                    }
                    "meme-input-missing:" -> {
                        memeInputMissing = lineScanner.nextLine().trim()
                    }
                    "no-quotes:" -> {
                        noQuotes = lineScanner.nextLine().trim()
                    }
                    "meme-cooldown:" -> {
                        memeCooldown = lineScanner.nextLine().trim()
                    }
                    "file-cooldown:" -> {
                        fileCooldown = lineScanner.nextLine().trim()
                    }
                    "quote-cooldown:" -> {
                        quoteCooldown = lineScanner.nextLine().trim()
                    }
                    "command-cooldown:" -> {
                        commandCooldown = lineScanner.nextLine().trim()
                    }

                }
                lineScanner.close()
            }
            fileScanner.close()

        } catch (e: FileNotFoundException) {
            val file = File("resources/messages.txt")
            val pw = PrintWriter(file)
            pw.println("help-title: $helpTitle")
            pw.println("help-format: $helpFormat")
            pw.println("help-description: $helpDescription")
            pw.println("meme-description: $memeDescription")
            pw.println("file-description: $fileDescription")
            pw.println("quote-description: $quoteDescription")
            pw.println("help-command: $helpCommand")
            pw.println("meme-command: $memeCommand")
            pw.println("file-command: $fileCommand")
            pw.println("quote-command: $quoteCommand")
            pw.println("error: $error")
            pw.println("unknown-command: $unknownCommand")
            pw.println("no-files: $noFiles")
            pw.println("unknown-file: $unknownFile")
            pw.println("meme-title: $memeTitle")
            pw.println("meme-input-missing: $memeInputMissing")
            pw.println("no-quotes: $noQuotes")
            pw.println("meme-cooldown: $memeCooldown")
            pw.println("file-cooldown: $fileCooldown")
            pw.println("quote-cooldown: $quoteCooldown")
            pw.println("command-cooldown: $commandCooldown")
            pw.close()
        }
    }
}