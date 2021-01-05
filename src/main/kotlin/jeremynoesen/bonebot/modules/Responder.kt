package jeremynoesen.bonebot.modules

import jeremynoesen.bonebot.Logger
import net.dv8tion.jda.api.entities.Message
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

/**
 * responder to words and phrases in a message
 *
 * @author Jeremy Noesen
 */
object Responder {

    /**
     * list of phrases loaded from the responses file
     */
    val responses = HashMap<String, String>()

    /**
     * cooldown for responder, in seconds
     */
    var cooldown = 180

    /**
     * last time the responder sent a message in milliseconds
     */
    private var prevTime = 0L

    /**
     * load all responses from file into the hashmap
     */
    fun loadResponses() {
        try {
            val fileScanner = Scanner(File("resources/responses.txt"))
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine()
                if (line.isNotBlank()) {
                    val parts = line.split(" // ")
                    responses[parts[0].toLowerCase()] = parts[1]
                }
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            val file = File("resources/responses.txt")
            val pw = PrintWriter(file)
            pw.println()
            pw.close()
        } catch (e: Exception) {
            Logger.log(e)
        }
    }

    /**
     * respond to a message if a trigger phrase is said
     *
     * @param message message to check and respond to
     */
    fun respond(message: Message) {
        try {
            val msg = message.contentRaw.toLowerCase()
            for (trigger in responses.keys) {
                if (msg.contains(trigger) && (System.currentTimeMillis() - prevTime) >= cooldown * 1000) {
                    prevTime = System.currentTimeMillis()
                    message.channel.sendMessage(responses[trigger]!!.replace("\$USER$", message.author.asMention)).queue()
                    break
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}