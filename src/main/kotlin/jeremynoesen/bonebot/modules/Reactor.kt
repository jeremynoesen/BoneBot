package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*
import kotlin.collections.HashMap

/**
 * responder to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Reactor {

    /**
     * list of phrases loaded from the responses file
     */
    val reactions = HashMap<String, String>()

    /**
     * cooldown for reactor, in seconds
     */
    var cooldown = 60

    /**
     * last time the reactor reacted to a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * load all reactions from file into the hashmap
     */
    fun loadReactions() {
        try {
            val fileScanner = Scanner(File("resources/reactions.txt"))
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine()
                if (line.isNotBlank()) {
                    val parts = line.split(" // ")
                    reactions[parts[0].toLowerCase()] = parts[1]
                }
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File("resources/reactions.txt")
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }
    }

    /**
     * react to a message if a trigger phrase is said
     *
     * @param message message to check and react to
     */
    fun react(message: Message) {
        try {
            val msg = message.contentRaw.toLowerCase()
            for (trigger in reactions.keys) {
                if (msg.contains(trigger) && (System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    message.addReaction(reactions[trigger]!!).queue()
                    break
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}