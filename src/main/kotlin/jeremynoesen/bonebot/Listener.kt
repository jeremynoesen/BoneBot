package jeremynoesen.bonebot

import jeremynoesen.bonebot.modules.Command
import jeremynoesen.bonebot.modules.Reactor
import jeremynoesen.bonebot.modules.Responder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * all listeners for the bot
 *
 * @author Jeremy Noesen
 */
class Listener : ListenerAdapter() {

    /**
     * respond and/or react to users when they say certain key words
     *
     * @param e message received event
     */
    override fun onMessageReceived(e: MessageReceivedEvent) {
        try {
            if (!e.author.isBot) {
                if (!Command.perform(e.message)) {
                    Responder.respond(e.message)
                    Reactor.react(e.message)
                }
            }
        } catch (e: Exception) {
            Logger.log(e)
        }
    }
}