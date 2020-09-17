package jndev.bonebot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * logger to write errors to a log file
 *
 * @author JNDev (Jeremaster101)
 */
public class Logger {
    
    /**
     * write exception to a log file with time stamp
     *
     * @param exception exception to log
     */
    public static void log(Exception exception) {
        File log = new File("log.txt");
        try {
            PrintWriter pw = new PrintWriter(log);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));
            pw.println(exception.getMessage());
            for (StackTraceElement ste : exception.getStackTrace()) {
                pw.println(ste.toString());
            }
            pw.println();
            pw.close();
            log = null;
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }
    
}
