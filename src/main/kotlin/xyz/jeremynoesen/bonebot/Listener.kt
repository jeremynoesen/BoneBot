package xyz.jeremynoesen.bonebot

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import xyz.jeremynoesen.bonebot.modules.Commands
import xyz.jeremynoesen.bonebot.modules.Reactor
import xyz.jeremynoesen.bonebot.modules.Responder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.jeremynoesen.bonebot.modules.Welcomer

/**
 * all listeners for the bot
 *
 * @author Jeremy Noesen
 */
class Listener : ListenerAdapter() {

    /**
     * respond and/or react to users when they say certain keywords or type commands
     *
     * @param e message received event
     */
    override fun onMessageReceived(e: MessageReceivedEvent) {
        try {
            if (!e.author.isBot || (Config.listenToBots && e.author != BoneBot.JDA!!.selfUser)) {
                if (!Commands.enabled || !Commands.perform(e.message)) {
                    if (Responder.enabled) Responder.respond(e.message)
                    if (Reactor.enabled) Reactor.react(e.message)
                }
            }
        } catch (ex: Exception) {
            Messages.sendMessage(Messages.error, e.message)
            ex.printStackTrace()
        }
    }

    /**
     * listen for message edits for fixing typos
     *
     * @param e message update event
     */
    override fun onMessageUpdate(e: MessageUpdateEvent) {
        try {
            if (!e.author.isBot || (Config.listenToBots && e.author != BoneBot.JDA!!.selfUser)) {
                if (!Commands.enabled || !Commands.perform(e.message)) {
                    if (Responder.enabled) Responder.respond(e.message)
                    if (Reactor.enabled) Reactor.react(e.message)
                }
            }
        } catch (ex: Exception) {
            Messages.sendMessage(Messages.error, e.message)
            ex.printStackTrace()
        }
    }

    /**
     * listen for member joins so they can be welcomes
     *
     * @param e guild member join event
     */
    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        if (Welcomer.enabled)
            Welcomer.welcome(e.user, e.guild)
    }
}