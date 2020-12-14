![Banner](https://i.imgur.com/pRvViK1.png)

## About
BoneBot is a simple discord bot that can respond to phrases, react to messages, and generate memes on demand to add more life to a discord server.

## Purpose
BoneBot was originally written as a meme-generating Discord bot for the ISUCF'V'MB Trombone and Bass Trombone Discord server. The bot also has functionality to respond and react to peoples' messages, which was utilized as a way to add helpful commands to the server to show links for online folders and band information.

## Usage

### Meme Generator
The command `bbmeme <optional text> <optional image or user ping>` generates a meme. If an image or text is provided, the generator will use these, otherwise, random images and text lines will be used. If a user is pinged, their avatar will be used as the image. These text lines will be in `texts.txt` and the images will be placed in a folder called `images` in the bot install directory. The images should ideally be JPEG or PNG format. The texts file will designate a new line as a separator for different texts.

### Responder
BoneBot can respond to messages based on phrases set in `responses.txt`. The format is `trigger // response` and for trigger phrases with multiple parts that can be separate or out of order with multiple responses that send in order, you can do `part1 / part2 / etc // response1 // response2`. When the bot sees a message with all the trigger phrases, the response will be sent as a message from the bot. You can use `SUSER$` in the response code to mention the user who invoked the response.

### Reactor
BoneBot can react with emotes to messages in a similar way to responding. These are set up in `reactions.txt` and have the same format as responses, except the response is replaced with an emote. You can either put a standard emoji as the response, or you can put the raw discord emoji code for custom emotes. You can get this in discord by typing \ and then :emote: and it will give you the emote as its code.

### Commands
BoneBot allows for custom commands to be made to provide one or multi message responses. The format is the same as responder, but without multiple trigger parts. You do not need to include the command prefix, that is automatically added on. The prefix can be customized in `config.txt`. Changing it from "bb" to something else also affect the restart and meme commands.

### Status
BoneBot can have various now playing statuses shown in Discord. You can put custom statuses in `statuses.txt`. Each new line designates a different status message.

### Other
The command `bbrestart` is used to restart the bot. If not set up with a launch daemon, the bot will just shut down. Only the server owner can run this command. You can type `bbhelp` to view all commands, including a list of custom added commands.

## Building
To build, clone or download this repository. With Gradle installed, run `gradle build` in the directory of the project. After that, a file called "BoneBot.jar" should have been created.

## Installation
Create a new folder where you would like the bot to be installed. Next, place `BoneBot.jar` in this directory. Next, you are going to want to head over to the Discord Developer Portal and create an application. After doing so, add a bot to the application in the bot tab. You can customize everything here as you please, as it is your own instance of the bot.

## Running
To start the bot, you can run in terminal, or create a script to run `java -jar /path/to/BoneBot.jar`. This will start the bot and generate needed files. If using a script to start the bot automatically along with a launch daemon, set the service to start back up on a successful exit. This will allow `bbrestart` to function properly. Restarting the bot is necessary after modifying any of the text files.

## Script
This is a script that can be used to install and run the bot. Replace `botdir` with the directory you placed the bot in. Use this instead of the previously mentioned steps for installing and running for an automatically updating bot and/or quick installation. You will need to restart the bot after updating text files. If you would like to make a simple start script, remove all but the first and last lines of this script.
```
cd botdir
git clone https://github.com/Jeremaster101/BoneBot.git
cd botdir/BoneBot
gradle build
cp botdir/BoneBot/build/libs/BoneBot.jar botdir
rm -rf botdir/BoneBot
cd botdir
java -jar botdir/BoneBot.jar
```

## Configuration
Configuration for meme images, texts, responder messages, reactor triggers, and status messages are all highlighted under **Usage**. There is a general config that handles cool downs for all of these, the bot token, and the command prefix. These values are in seconds, and are the default values. After changing any of these values, save the file, and restart the bot.
```
response-cooldown: 180
react-cooldown: 60
meme-cooldown: 5
status-cooldown: 60
command-cooldown: 5
command-prefix: bb
bot-token: TOKEN_HERE
```

## Contributing
To contribute to this repo, you can fork the release branch, make your edits and additions, and then create a pull request back to the release branch. You are also free to do whatever with your fork according to the license.