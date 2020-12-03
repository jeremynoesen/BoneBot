package jndev.bonebot

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.Month
import java.util.*

/**
 * Halloween trick or treating virtual event
 *
 * @author JNDev (Jeremaster101)
 */
class Halloween(jda: JDA) : ListenerAdapter() {
    /**
     * user candy counts
     */
    private val data: HashMap<User, Int>

    /**
     * trick or treat cooldown
     */
    private val totCooldowns: HashMap<User, Long>

    /**
     * give cooldown
     */
    private val giveCooldowns: HashMap<User, Long>

    /**
     * give cooldown
     */
    private val stealCooldowns: HashMap<User, Long>

    /**
     * respond to users when they say certain key words
     *
     * @param e message received event
     */
    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (!e.author.isBot) {
            val msg = e.message.contentRaw
            if (msg.startsWith("!halloween")) {
                showInfo(e.channel)
            } else {
                val month = LocalDateTime.now().month
                val day = LocalDateTime.now().dayOfMonth
                if (month == Month.OCTOBER && day == 31) {
                    if (msg.startsWith("!trickortreat") || msg.startsWith("!tot")) {
                        if (!totCooldowns.containsKey(e.author) ||
                                System.currentTimeMillis() - totCooldowns[e.author]!! >= 10000) {
                            totCooldowns[e.author] = System.currentTimeMillis()
                            val random = Random()
                            if (random.nextInt(5) < 2) {
                                takeCandy(e.author, e.channel)
                            } else {
                                giveCandy(e.author, e.channel)
                            }
                        } else {
                            val messageEmbed = createEmbed("Wait!",
                                    e.author.asMention + " can trick or treat in " + (10 -
                                            (System.currentTimeMillis() - totCooldowns[e.author]!!) / 1000) + " seconds",
                                    Color.ORANGE)
                            e.channel.sendMessage(messageEmbed).queue()
                        }
                    } else if (msg.startsWith("!give")) {
                        if (!giveCooldowns.containsKey(e.author) ||
                                System.currentTimeMillis() - giveCooldowns[e.author]!! >= 30000) {
                            if (e.message.mentionedUsers.size == 1) {
                                giveCandy(e.author,
                                        e.message.mentionedUsers[0], e.channel)
                                giveCooldowns[e.author] = System.currentTimeMillis()
                            } else {
                                val messageEmbed = createEmbed("Error!",
                                        "That command requires a mentioned user",
                                        Color.ORANGE)
                                e.channel.sendMessage(messageEmbed).queue()
                            }
                        } else {
                            val messageEmbed = createEmbed("Wait!",
                                    e.author.asMention + " can give 🍫 in " + (30 -
                                            (System.currentTimeMillis() - giveCooldowns[e.author]!!) / 1000) + " seconds",
                                    Color.ORANGE)
                            e.channel.sendMessage(messageEmbed).queue()
                        }
                    } else if (msg.startsWith("!steal")) {
                        if (!stealCooldowns.containsKey(e.author) ||
                                System.currentTimeMillis() - stealCooldowns[e.author]!! >= 15000) {
                            if (e.message.mentionedUsers.size == 1) {
                                val random = Random()
                                if (random.nextInt(5) >= 2) {
                                    takeCandy(e.message.mentionedUsers[0], e.author,
                                            e.channel)
                                } else {
                                    val messageEmbed = createEmbed("Failed Theft!",
                                            e.author.asMention + " tried to steal 🍫 from " +
                                                    e.message.mentionedUsers[0].asMention + " but failed",
                                            Color.ORANGE)
                                    e.channel.sendMessage(messageEmbed).queue()
                                }
                                stealCooldowns[e.author] = System.currentTimeMillis()
                            } else {
                                val messageEmbed = createEmbed("Error!",
                                        "That command requires a mentioned user",
                                        Color.ORANGE)
                                e.channel.sendMessage(messageEmbed).queue()
                            }
                        } else {
                            val messageEmbed = createEmbed("Wait!",
                                    e.author.asMention + " can steal 🍫 in " + (15 -
                                            (System.currentTimeMillis() - stealCooldowns[e.author]!!) / 1000) + " seconds",
                                    Color.ORANGE)
                            e.channel.sendMessage(messageEmbed).queue()
                        }
                    } else if (msg.startsWith("!bag")) {
                        getBag(e.author, e.channel)
                    } else if (msg.startsWith("!leaderboard") || msg.startsWith("!lb")) {
                        showLeaderboard(e.channel)
                    }
                } else if (msg.startsWith("!trickortreat") || msg.startsWith("!tot") || msg.startsWith("!bag") ||
                        msg.startsWith("!leaderboard") || msg.startsWith("!lb") || msg.startsWith("!give") ||
                        msg.startsWith("!steal")) {
                    val messageEmbed = createEmbed("It Is Not Halloween!",
                            "This can only be done on Halloween",
                            Color.ORANGE)
                    e.channel.sendMessage(messageEmbed).queue()
                }
            }
        }
    }

    /**
     * load all data from file to the hash map
     */
    private fun loadData(jda: JDA) {
        try {
            val fileScanner = Scanner(File("halloween.txt"))
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine().trim { it <= ' ' }
                val parts = line.split(" ").toTypedArray()
                val user = jda.retrieveUserById(parts[0].trim { it <= ' ' }).complete()
                val count = parts[1].trim { it <= ' ' }.toInt()
                data[user] = count
            }
            fileScanner.close()
        } catch (e: FileNotFoundException) {
            File("halloween.txt")
        }
    }

    /**
     * save all data from hashmap to file
     */
    private fun saveData() {
        try {
            val printWriter = PrintWriter(File("halloween.txt"))
            for (user in data.keys) {
                printWriter.println(user.id + " " + data[user])
            }
            printWriter.close()
        } catch (e: IOException) {
            Logger.log(e)
        }
    }

    /**
     * create an embed with a title, body, and color
     *
     * @param title   title of embed
     * @param message message in embed
     * @param color   color of embed
     * @return built embed message
     */
    private fun createEmbed(title: String, message: String, color: Color): MessageEmbed {
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle(title)
        embedBuilder.setDescription(message)
        embedBuilder.setColor(color)
        return embedBuilder.build()
    }

    /**
     * give a user a candy and show an embed
     *
     * @param user    user to give candy to
     * @param channel channel to send embed in
     */
    private fun giveCandy(user: User, channel: MessageChannel) {
        if (data.containsKey(user)) {
            data[user] = data[user]!! + 1
        } else {
            data[user] = 1
        }
        val messageEmbed = createEmbed("Treat!",
                user.asMention + " received 1 🍫", Color.GREEN)
        channel.sendMessage(messageEmbed).queue()
    }

    /**
     * take candy from a user and show the embed
     *
     * @param user    user to take candy from
     * @param channel channel to send embed in
     */
    private fun takeCandy(user: User, channel: MessageChannel) {
        if (data.containsKey(user) && data[user]!! >= 1) {
            data[user] = data[user]!! - 1
            val messageEmbed = createEmbed("Trick!",
                    user.asMention + " lost 1 🍫", Color.ORANGE)
            channel.sendMessage(messageEmbed).queue()
        } else {
            data[user] = 0
            val messageEmbed = createEmbed("Trick!",
                    user.asMention + " had no 🍫 to lose", Color.ORANGE)
            channel.sendMessage(messageEmbed).queue()
        }
    }

    /**
     * get the user's candy bag to show how much candy they have
     *
     * @param user    user to get bag for
     * @param channel channel to send embed to
     */
    private fun getBag(user: User, channel: MessageChannel) {
        if (!data.containsKey(user)) {
            data[user] = 0
        }
        val messageEmbed = createEmbed("Candy Bag",
                user.asMention + " currently has " + data[user] + " 🍫", Color.MAGENTA)
        channel.sendMessage(messageEmbed).queue()
    }

    /**
     * show the game how to play message
     *
     * @param channel channel to send embed to
     */
    private fun showInfo(channel: MessageChannel) {
        val messageEmbed = createEmbed(
                "Halloween Trick or Treat Virtual Competition",
                """
                      How to play:
                      1. Type !trickortreat or !tot (10 second cooldown)
                      2. Receive a treat, or be tricked and lose candy
                      3. Type !bag to see how much candy you have
                      4. Type !leaderboard or !lb to see the top 10 users
                      5. Give people candy with !give @user (30 second cooldown)
                      6. Try to steal candy with !steal @user (15 second cooldown)
                      7. Get the most candy by the end of Halloween
                      8. Win 1 month of Discord Nitro Classic
                      
                      Trick or treating starts on Halloween. Good luck!
                      """.trimIndent(),
                Color.MAGENTA)
        channel.sendMessage(messageEmbed).queue()
    }

    /**
     * show the leaderboard showing the top 10 users
     *
     * @param channel channel to send embed to
     */
    private fun showLeaderboard(channel: MessageChannel) {
        var curCount = -1
        var curUser: User? = null
        val users = ArrayList<User?>()
        val counts = ArrayList<Int>()
        for (i in 0 until Math.min(data.size, 10)) {
            for (user in data.keys) {
                if (!users.contains(user) && data[user]!! > curCount) {
                    curUser = user
                    curCount = data[user]!!
                }
            }
            if (curCount > 0) {
                users.add(curUser)
                counts.add(curCount)
            }
            curUser = null
            curCount = -1
        }
        val leaderboard = StringBuilder()
        if (users.size > 0) {
            for (i in users.indices) {
                val user = users[i]
                val count = counts[i]
                leaderboard.append(i + 1).append(". ").append(user!!.name).append(" - ").append(count).append(" 🍫 \n")
            }
        } else {
            leaderboard.append("No users yet")
        }
        val messageEmbed = createEmbed("Leaderboard", leaderboard.toString(), Color.MAGENTA)
        channel.sendMessage(messageEmbed).queue()
    }

    /**
     * give a user a piece of candy
     *
     * @param from    user sending the candy
     * @param to      user receiving the candy
     * @param channel channel to send embeds to
     */
    private fun giveCandy(from: User, to: User, channel: MessageChannel) {
        if (data.containsKey(from) && data[from]!! >= 1) {
            data[from] = data[from]!! - 1
            if (data.containsKey(to)) {
                data[to] = data[to]!! + 1
            } else {
                data[to] = 1
            }
            val messageEmbed = createEmbed("Candy Given!",
                    to.asMention + " received 1 🍫 from " + from.asMention, Color.GREEN)
            channel.sendMessage(messageEmbed).queue()
        } else {
            val messageEmbed = createEmbed("No Candy!",
                    from.asMention + " does not have any 🍫", Color.ORANGE)
            channel.sendMessage(messageEmbed).queue()
        }
    }

    /**
     * take a piece of candy from another user
     *
     * @param from    user being taken from
     * @param to      user taking the candy
     * @param channel channel to send embeds to
     */
    private fun takeCandy(from: User, to: User, channel: MessageChannel) {
        if (data.containsKey(from) && data[from]!! >= 1) {
            data[from] = data[from]!! - 1
            if (data.containsKey(to)) {
                data[to] = data[to]!! + 1
            } else {
                data[to] = 1
            }
            val messageEmbed = createEmbed("Candy Stolen!",
                    to.asMention + " stole 1 🍫 from " + from.asMention, Color.GREEN)
            channel.sendMessage(messageEmbed).queue()
        } else {
            val messageEmbed = createEmbed("No Candy!",
                    from.asMention + " does not have any 🍫", Color.ORANGE)
            channel.sendMessage(messageEmbed).queue()
        }
    }

    /**
     * initialize the entire halloween event
     *
     * @param jda jda this is running in
     */
    init {
        data = HashMap()
        totCooldowns = HashMap()
        giveCooldowns = HashMap()
        stealCooldowns = HashMap()
        loadData(jda)
        Runtime.getRuntime().addShutdownHook(Thread { saveData() })
    }
}