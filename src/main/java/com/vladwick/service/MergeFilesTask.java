package com.vladwick.service;

import com.vladwick.service.comparator.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MergeFilesTask {

    static String mode = "ascending";
    static String type = "";
    static List<String> namesOfFiles = new ArrayList<String>();

    public MergeFilesTask(String mode, String type, List<String> files) throws IOException {
        this.mode = mode;
        this.type = type;
        this.namesOfFiles = files;
        formFilePath();
    }

    /**
     * Form correct path to all files that need to be merged
     * relatively to this project and current system
     *
     * @throws IOException
     */
    public void formFilePath() throws IOException {
        ArrayList<Path> pathArray = new ArrayList<>();
        for(int i = 1; i < namesOfFiles.size(); ++i) {
            File file = new File(Paths.get("src/main/resources/files/" + namesOfFiles.get(i)).toUri());
            pathArray.add(file.toPath());
        }
        ArrayList<String> filesArray = new ArrayList<>();
        for(int i = 0; i < pathArray.size(); ++i) {
            filesArray.add(pathArray.get(i).toAbsolutePath().toString());
        }
        solve(filesArray);
    }

    public void solve(ArrayList<String> filesArray) throws IOException {
        String tempDirectory = String.valueOf(Paths.get("target/classes/temp/"));
        String outputFile = String.valueOf(Paths.get(namesOfFiles.get(0)));

        // Формирование temp файлов
        List<File> files = Util.splitFiles(filesArray, tempDirectory);

        // Выбор компаратора
        MainComparator cmp = getComparator(type, mode);

        // 'Merge' файлов
        if(type == "integer" && mode == "descending") {
            Util.mergeSortedFilesIntegerDescending(
                    files,
                    outputFile,
                    cmp
            );
        } else if(type == "integer" && mode == "ascending") {
            Util.mergeSortedFilesIntegerAscending(
                    files,
                    outputFile,
                    cmp
            );
        } else if(type == "string" && mode == "descending") {
            Util.mergeSortedFilesStringDescending(
                    files,
                    outputFile,
                    cmp
            );
        } else if(type == "string" && mode == "ascending") {
            Util.mergeSortedFilesStringAscending(
                    files,
                    outputFile,
                    cmp
            );
        } else {
            throw new RuntimeException("Unsupported options 'type' or 'mode'.");
        }
        System.out.println("\nSuccessfully merge all files into one.");
    }

    private MainComparator getComparator(String type, String mode) {
        MainComparator cmp = new StringComparatorAsc();
        if(type == "integer") {
            if(mode == "descending") {
                cmp = new IntegerComparatorDesc();
            } else {
                cmp = new IntegerComparatorAsc();
            }
        } else {
            if(mode == "descending") {
                cmp = new StringComparatorDesc();
            }
        }
        return cmp;
    }
}
