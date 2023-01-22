package com.vladwick;

import com.vladwick.service.MergeFilesTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Merge sort of multiple files that can`t be fit into the RAM
 *
 * @author VladWick
 */
public class App {
    static String mode = "ascending";
    static String type = "";
    static List<String> namesOfFiles = new ArrayList<>();

    public static void main( String[] args ) throws IOException {
        System.out.println("=== Start ===\n");

        // Работа с аргументами из командной строки
        new CmdParser().parse(args);

        // Основная логика приложения
        new MergeFilesTask(mode, type, namesOfFiles);

        System.out.println("\n=== Finish ===");
    }
}
