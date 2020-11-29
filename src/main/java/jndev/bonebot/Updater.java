package jndev.bonebot;

import net.dv8tion.jda.api.entities.Message;

import java.io.*;

/**
 * class with updating from github implementation
 */
public class Updater {
    
    /**
     * pull newest version from github and recompile and restart the bot
     *
     * @param executor message initiating the update
     * @param buildDir directory for git pull to occur to
     * @throws IOException
     */
    public static void updateFromGitHub(Message executor, String buildDir) throws IOException {
        
        executor.getChannel().sendMessage("Starting update from GitHub...").queue();
    
        File tempScript = File.createTempFile("update", null);
    
        executor.getChannel().sendMessage("Creating script...").queue();
    
        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);
        printWriter.println("#!/bin/bash");
        printWriter.println("cd " + buildDir);
        printWriter.println("git pull https://github.com/Jeremaster101/BoneBot.git");
        printWriter.println("javac *.java");
        
        printWriter.close();
    
        executor.getChannel().sendMessage("Running script...").queue();
    
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException e) {
            Logger.log(e);
        } finally {
            tempScript.delete();
        }
    
        executor.getChannel().sendMessage("Restarting...").queue();
    
        System.exit(0);
    }
}
