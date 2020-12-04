package jndev.bonebot

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

/**
 * class to handle config values
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
    var memeCooldown = 10

    /**
     * cooldown for status updater, in seconds
     */
    var statusCooldown = 60

    /**
     * load config values, write default config if missing
     */
    fun load() {
        try {
            val fileScanner = Scanner(File("resources/config.txt"))
            while (fileScanner.hasNextLine()) {
                val lineScanner = Scanner(fileScanner.nextLine())
                val key = lineScanner.next()
                when {
                    key.equals("response-cooldown:") -> {
                        responseCooldown = lineScanner.nextInt()
                    }
                    key.equals("react-cooldown:") -> {
                        reactCooldown = lineScanner.nextInt()
                    }
                    key.equals("meme-cooldown:") -> {
                        memeCooldown = lineScanner.nextInt();
                    }
                    key.equals("status-cooldown:") -> {
                        statusCooldown = lineScanner.nextInt();
                    }
                }
                lineScanner.close()
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File("resources/config.txt")
            val pw = PrintWriter(file)
            pw.println("response-cooldown: 180")
            pw.println("react-cooldown: 60")
            pw.println("meme-cooldown: 10")
            pw.println("status-cooldown: 60")
            pw.close()
        }
    }
}