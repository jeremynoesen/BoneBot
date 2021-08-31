<img src="img/Logo.svg" alt="Logo" title = "Logo" align="right" width="200" height="200" />

# BoneBot

## About
BoneBot is a self-hosted, modular Discord bot that can respond and react to messages, add custom commands, send random files and quotes, and generate memes on demand to add more life to a Discord server.

## Purpose
BoneBot was originally written as a meme-generating Discord bot for the ISUCF'V'MB Trombone and Bass Trombone Discord server. The bot also has functionality to respond and react to messages and commands. This is utilized as a way to add helpful commands to the server to show links for online folders and band information.

## Usage

### Meme Generator
The meme generator will generate a meme with top and bottom text in Impact font.
##### Command
- `bbmeme`
- `bbmeme <image> <top text> // <bottom text>` (Can also press shift+enter instead of ` // `)
##### Image input
- Image (PNG, JPG)
- Image link (PNG, JPG)
- User ping (Grabs their avatar)
- Image from an embed (PNG, JPG)
- Any of the above from a reply  
  - Note: main message input will override reply input
- Random image (If properly configured and no image is provided)
##### Text input
- Text in your message
- Text in a reply (If no image is in the reply)
  - Note: main message input will override reply input
- Random text (If properly configured and no text is provided)

If you make a mistake in typing the command, or wish to generate another meme with similar inputs, you can edit the message to fix it without needing to send another message.

### Files
The file module will send a random file from `resources/files`. This can be an image, video, text file, audio file, etc. A specific file can also be sent from the `resources/files` folder
#### Command
- `bbfile`  
- `bbfile <path>`
##### File input
- Only works for files in `resources/files`
- must not start with a `/`
- If not specified, a random file will be sent.
  - Only files not in sub-directories will be sent.
  - Specifying a sub-directory for the path will grab a random file from within that directory.

### Quotes
The quote module will send a random quote from a file.
#### Command
`bbquote`

### Responder
The responder will respond to a message with another when a trigger phrase is said. It can appear like it is typing the message itself!

### Reactor
The reactor will react to a message with an emote when a trigger phrase is said.

### Commands
BoneBot allows for custom commands to be made to provide text and image responses and run shell commands. Their usage is pretty simple - a command called `test` would be used by typing `bbtest`. If you make a mistake in typing a command, you can edit the message to fix it without needing to send another message.

### Statuses
BoneBot can have various randomized statuses shown in Discord that change over time.

### Welcomer
The welcomer will send a private message to new users who join to welcome them to the server. This can be used to show server rules and an explanation of how to get started.

## Requirements
Any version of Windows, macOS, or Linux that can run Java 8 or later.

## Building
1. Clone or download this repository.
2. Run `./gradlew build` in the directory of the project.
3. A file called `BoneBot.jar` should have been created.  

You can also grab `BoneBot.jar` from the latest releases.

## Installation
1. Create a new folder where you would like the bot to be installed. 
2. Place `BoneBot.jar` in this directory.
3. Head over to the [Discord Developer Portal](https://discord.com/developers/applications) and create an application.
4. Add a bot to the application in the bot tab.
5. Copy the bot token from this page. You will need it later.

## Running
Use the commands below, replacing `botdir` with the directory you placed the bot in. Running for the first time will generate all necessary configuration files and folders, but will fail to start the bot. You will need to set `bot-token` in `resources/config.txt` to the token you copied earlier for the bot to start.
```
cd botdir
java -jar BoneBot.jar
```

## Configuration
**After changing any of text configurations, save the file and restart the bot.**

### Meme Generator
- Images for the randomizer are to be placed in the `resources/memeimages` folder. They must be PNG or JPG format.
- Texts for the randomizer are to be placed in `resources/memetexts.txt`.
  - Each separate line denotes a separate text.
  - The format is `<top text> // <bottom text>`.
- To set the size of generates memes, set `meme-size` in the main configuration to any number. This sets the image width to this number, and varies the height based on this width. Set it to `0` to use the image's original dimensions with no scaling.
- To set a cool down for the generator, set `meme-cooldown` in the main configuration to any whole number in seconds.
- To enable or disable the generator, set `memes-enabled` in the main configuration to `true` or `false`.

### Quotes
- Quotes will be put into `resources/quotes.txt`.
- Each line designates a new entry.
- To set a cool down for quotes, set `quote-cooldown` in the main configuration to any while number in seconds.
- To enable or disable quotes, set `quotes-enabled` in the main configuration to `true` or `false`.

### Files
- Files will be put into `resources/files`.
- To set a cool down for files, set `file-cooldown` in the main configuration to any whole number in seconds.
- To enable or disable files, set `files-enabled` in the main configuration to `true` or `false`.

### Responder
- Responses will be put into `resources/responses.txt`.
- Each line designates a new entry.
- The format is `trigger // response`.
- Case is ignored in the trigger.
- The trigger can include Regex. Case is not ignored if you use Regex.
- The response can include `\n` as a line separator.
- You can also include `$USER$` in the response to ping the user who invoked a response, or `$REPLY$` to reply to them.
- You can send a single file by adding `$FILE$ path/to/file $FILE$`.
- The response sends with a delay based on message length multiplied by `typing-speed` in the main configuration in milliseconds. This number must be a whole number.
- To set a cool down for the responder, set `responder-cooldown` in the main configuration to whole any number in seconds.
- To enable or disable the responder, set `responder-enabled` in the main configuration to `true` or `false`.

### Reactor
- Reactions will be put into `resources/reactions.txt`.
- Each line designates a new entry.
- The format is `trigger // emote`.
- Case is ignored in the trigger.
- The trigger can include Regex. Case is not ignored if you use Regex.
- For the emote, you can put either a unicode emoji, `U+1F980`, or a raw discord emote, `:bonebot:819645061200347177`.
- To set a delay between when the message is sent and when the bot reacts, set `reactor-delay` in the main configuration to any whole number in milliseconds.
- To set a cool down for the reactor, set `reactor-cooldown` in the main configuration to any whole number in seconds.
- To enable or disable the reactor, set `reactor-enabled` in the main configuration to `true` or `false`.

### Commands
- Reactions will be put into `resources/commands.txt`.
- Each line designates a new entry.
- The format is `command // description // response`.
- Do not put the prefix in the command.
- The response can include `\n` as a line separator.
- You can include `$USER$` in the response to ping the user who used the command, or `$REPLY$` to reply to them. The user replacement happens before the shell commands below.
- You can also include `$ID$` in the response to place the user's ID into it. This also happens before the shell commands.
- You can run a shell command by adding `$CMD$ command here $CMD$`.
  - Need to run multiple commands? Make a shell script and run the script with a command, or separate commands with a semi-colon!
  - Add `$CMDOUT$` to your response to also include the output of this command in the response.
  - If the above would return a file path, you can surround it with `$FILE$` to send that file, outlined below
- You can send a single file by adding `$FILE$ path/to/file $FILE$`.
- You can add a reaction to the command trigger message by adding `$REACT$ emote $REACT$`. Format for emotes is similar to the reactor.
- To change the command prefix, set `command-prefix` in the main configuration to a custom prefix. Case is ignored.
- To set a cool down for commands, set `commands-cooldown` in the main configuration to any whole number in seconds.
- To enable or disable commands, set `commands-enabled` in the main configuration to `true` or `false`.

### Statuses
- Statuses will be put into `resources/statuses.txt`.
- Each line designates a new entry.
- Each line must start with `playing`, `watching`, or `listening to`.
- The main config has option `status-delay` to set how long each status shows in seconds as a whole number.
- To enable or disable statuses, set `statuses-enabled` in the main configuration to `true` or `false`.

### Welcomer
- The welcome message will be put into `resources/welcome.txt`.
- The entire file will make up the welcome message.
- New lines work without using `\n`.
- To mention the user in the message, add `$USER$`.
- To mention the server name, add `$GUILD$`.
- You can send a single file in the embed by adding `$FILE$ path/to/file $FILE$`.
- To enable or disable the welcomer, set `welcomer-enabled` in the main configuration to `true` or `false`.

### Messages
- All messages built in to the bot are editable, located in `resources/messages.txt`.
- The only placeholders allowed are specified per line, except for all messages from `error` to the end of the file. These can include the `$USER$` and `$REPLY$` placeholders.
- Standard Discord Markdown formatting is supported.
- This file can modify all built-in responses, command descriptions, and commands.
- All messages can include new line characters `\n`, except for the commands

### Miscellaneous
- You can change the colors of embeds for the meme generator and help message by setting `embed-color` in the main configuration to a hex code.
- You can allow BoneBot to listen to input from other bots by setting `listen-to-bots` to `true`. It defaults to `false`.
- All files in `temp` will be deleted every time the bot program is closed.
- You can make other files and directories in the bot folder as needed for your own organization of files the bot may use, but not for the files the bot requires

### Defaults
##### Main Configuration
Located at `resources/config.txt`
```
responder-enabled: true
responder-cooldown: 180
typing-speed: 100
reactor-enabled: true
reactor-cooldown: 60
reactor-delay: 1000
memes-enabled: true
meme-cooldown: 5
meme-size: 1200
statuses-enabled: true
status-delay: 60
commands-enabled: true
command-cooldown: 5
command-prefix: bb
quotes-enabled: true
quote-cooldown: 5
files-enabled: true
file-cooldown: 5
welcomer-enabled: true
listen-to-bots: false
embed-color: #fd0605
bot-token: TOKEN
```

##### Messages
```
help-title: $BOT$ Help
help-about: $BOT$ aims to add more life to a server by responding and reacting to messages. It also adds commands, which are listed below.
help-format: • **`$CMD$`**: $DESC$
help-description: Show this help message.
meme-description: Generate a random or custom meme.
file-description: Send a random or specific file.
quote-description: Send a random quote.
help-command: help
meme-command: meme
file-command: file
quote-command: quote
meme-title: $USER$ generated a meme:
welcome-title: $USER$ joined $GUILD$

error: **An error occurred!** Please check the log file!
unknown-command: **Unknown command!**
no-files: There are **no files** to send!
unknown-file: **Unknown file!**
meme-input-missing: Please provide the missing **text** and/or **image**!
no-quotes: There are no quotes to show!
meme-cooldown: Another meme can be generated in **$TIME$** seconds.
file-cooldown: Another file can be sent in **$TIME$** seconds.
quote-cooldown: Another quote can be sent in **$TIME$** seconds.
command-cooldown: Commands can be used again in **$TIME$** seconds.

```

##### Welcomer
```
Welcome $USER$ to **$GUILD$**!
```

### Permissions
The following Discord permissions are required for BoneBot to work:
- View Channels
- Send Messages
- Embed Links
- Attach Files
- Add Reactions
- Use External Emoji
- Read Message History
- Server Members Intent

## Demonstration
Below are a few images showing what BoneBot can do. Simple actions, such as reactions, text responses, and status messages, are not shown due to those being basic Discord functions.

### Meme Generator
The following is an example command sent to the bot to generate a meme with top and bottom text, as well as a user's avatar as the image input. The next image is the output of this command.

<div align="center"><img src="img/memeinput.png" alt="Meme example command" title="Meme example command" /></div>
<div align="center"><img src="img/memeoutput.png" alt="Meme example output" title="Meme example output" width="512" /></div>

### Help Command
The following image is an example of the help command with one custom command added.

<div align="center"><img src="img/help.png" alt="Help example" title="Help example" /></div>