![Banner](Banner.png)

## About
BoneBot is a simple, decentralized Discord bot that can respond and react to messages, add text-based commands, and generate memes on demand to add more life to a Discord server.

## Purpose
BoneBot was originally written as a meme-generating Discord bot for the ISUCF'V'MB Trombone and Bass Trombone Discord server. The bot also has functionality to respond and react to messages, which is utilized as a way to add helpful commands to the server to show links for online folders and band information.

## Usage

### Meme Generator
The command `bbmeme <text> <image or user>` generates a meme. If an image or text is provided, the generator will use these, otherwise, random images and text lines will be used if configured. If a user is pinged, their avatar will be used as the image. These text lines will be in `texts.txt` and the images will be placed in a folder called `images` in the resources directory. The images should ideally be JPEG or PNG format. The texts file will designate a new line as a separator for different texts.

### Responder
BoneBot can respond to messages based on phrases set in `responses.txt`. The format is `trigger // response`, and the trigger can use regex. When the bot sees a message with the trigger phrase, the response will be sent as a message from the bot. You can use `SUSER$` in the response code to mention the user who invoked the response. This is useful to make the bot feel like it is part of your conversations.

### Reactor
BoneBot can react with emotes to messages similarly to responding. These are set up in `reactions.txt` and have a format of `trigger // emote`. The trigger can use regex here as well. You can either put a standard emoji in unicode format, like `U+1F980`, or you can put the raw Discord emoji code for custom emotes. You can get this in Discord by typing \ and then `:emote:` and it will give you the emote as its code inside of `< >`. This is useful to make BoneBot interact with messages while not being an interruption.

### Commands
BoneBot allows for custom commands to be made to provide simple message responses. The format is `command // description // response`. You do not need to include the command prefix. The response can use the `SUSER$` placeholder as well. The prefix can be customized in `config.txt`. Changing it from "bb" to something else also affects the restart, help, and meme commands. Unlike the responder, commands require the command prefix, and also show up in the help command `bbhelp`. The description is used for the help command. This is great for quick access to links and information.

### Status
BoneBot can have various now playing statuses shown in Discord. You can put custom statuses in `statuses.txt`. Each new line designates a different status message. These will be selected at random based on the delay set in the main config.

## Building
To build, clone or download this repository. With Gradle installed, run `gradle build` in the directory of the project. After that, a file called `BoneBot.jar` should have been created. You can also grab `BoneBot.jar` from the latest release.

## Installation
Create a new folder where you would like the bot to be installed. Next, place `BoneBot.jar` in this directory. Next, you are going to want to head over to the Discord Developer Portal and create an application. After doing so, add a bot to the application in the bot tab. You can customize everything here as you please, as it is your own instance of the bot.

## Running
This is a script that can be used to run the bot. Replace `botdir` with the directory you placed the bot in. Running this will generate all necessary text files and directories. You will need to restart the bot after updating any text files, but do not need to when adding images.
```
cd botdir
java -jar botdir/BoneBot.jar
```

## Configuration
Configuration for meme images and texts, responder messages, reactor triggers, and status messages are all highlighted under **Usage**. There is a general config that handles all cool downs, the bot token, embed colors, and the command prefix. These values are in seconds, and are the default values. After changing any of these values, save the file and restart the bot.
```
response-cooldown: 180
react-cooldown: 60
meme-cooldown: 5
status-cooldown: 60
command-cooldown: 5
command-prefix: bb
embed-color: #0097FF
bot-token: TOKEN_HERE
```