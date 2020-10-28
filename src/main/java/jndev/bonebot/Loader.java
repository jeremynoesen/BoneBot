package jndev.bonebot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * handles loading string files into string lists
 */
public class Loader {
    
    /**
     * load all data from file to array list
     *
     * @param filePath path to file holding the data
     * @param list     list to load data into
     */
    public static void loadData(String filePath, ArrayList<String> list) {
        try {
            Scanner fileScanner = new Scanner(new File(filePath));
            list.clear();
            while (fileScanner.hasNextLine()) list.add(fileScanner.nextLine());
            fileScanner.close();
            list.trimToSize();
        } catch (FileNotFoundException e) {
            Logger.log(e);
        }
    }
}
