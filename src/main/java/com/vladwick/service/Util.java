package com.vladwick.service;

import com.vladwick.service.comparator.MainComparator;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Util {

    /**
     * Split large files into the several temp files
     *
     * @param filesArray
     * @param tempDirectory
     * @return List<File>
     * @throws IOException
     */
    public static List<File> splitFiles(final ArrayList<String> filesArray,
                                        final String tempDirectory) throws IOException {

        File dir = new File(tempDirectory);
        if (dir.exists()) {
            dir.delete();
        }
        dir.mkdir();

        List<File> files = new ArrayList<>();
        int tempFileCounter = 1;
        for(int k = 0; k < filesArray.size(); ++k) {
            RandomAccessFile raf = new RandomAccessFile(filesArray.get(k), "r");
            long sourceSize = raf.length();
            // calculate 'numberOfSplits'
            int numberOfSplits = getNumberOfSplits(sourceSize);
            System.out.println("Number of splits of the file '" + filesArray.get(k)  + "' = " + numberOfSplits);
            long bytesPerSplit = sourceSize / numberOfSplits;
            long remainingBytes = sourceSize % numberOfSplits;
            int maxReadBufferSize = 8 * 1024; // 8KB
            for (int i = 1; i <= numberOfSplits; i++) {
                File file = new File(tempDirectory + "/temp-file-" + tempFileCounter + ".txt");
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                if (bytesPerSplit > maxReadBufferSize) {
                    long numReads = bytesPerSplit / maxReadBufferSize;
                    long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                    for (int j = 0; j < numReads; j++) {
                        readWrite(raf, bos, maxReadBufferSize);
                    }
                    if (numRemainingRead > 0) {
                        readWrite(raf, bos, numRemainingRead);
                    }
                } else {
                    readWrite(raf, bos, bytesPerSplit);
                }
                files.add(file);
                tempFileCounter++;
                bos.close();
            }
            if (remainingBytes > 0) {
                File file = new File(tempDirectory + "/temp-file-" + tempFileCounter + ".txt");
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                readWrite(raf, bos, remainingBytes);
                files.add(file);
                bos.close();
            }
        }
        File outputDirectory = new File("target/classes/output/");
        outputDirectory.mkdir();
        return files;
    }

    private static int getNumberOfSplits(long sourceSize) {
        // Файлы до 50KB не разделяются на temp файлы
        if(sourceSize < 50000) {
            return 1;
        } else if(sourceSize < 100000) {
            return 2;
        } else if(sourceSize < 1000000) {
            return 10;
        } else if(sourceSize < 10000000) {
            return 100;
        } else {
            return 1000;
        }
    }

    private static void readWrite(RandomAccessFile raf, BufferedOutputStream bos, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bos.write(buf);
            bos.flush();
        }
    }

    /**
     * Merge all sorted files into the single big file
     *
     * Four functions. Which one is going to be used - is a matter of options 'mode' and 'type'.
     *
     * @param files
     * @param outputFile
     * @param cmp
     * @throws IOException
     */
    public static void mergeSortedFilesStringAscending(final List<File> files,
                                              final String outputFile,
                                              final MainComparator cmp
                                              )
            throws IOException {

        File f = new File(Paths.get(("target/classes/output/" + outputFile)).toAbsolutePath().toUri());
        System.out.println("\nPath of the output file: " + f);
        if (f.exists()) {
            f.delete();
        }

        TreeMap<String, BufferedReader> map = new TreeMap<>(cmp);
        List<BufferedReader> brReaders = new ArrayList<>();
        System.out.println("\nComparator: " + cmp.getClass());

        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        try {
            // В первом цикле в map добавляются первые элементы каждого temp файла
            for (File file : files) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                brReaders.add(br);
                String line = br.readLine();
                map.put(line, br);
            }
            // Во втором цикле происходит основной алгоритм
            while (!map.isEmpty()) {
                // Из map берётся самый лексикографическо маленький элемент
                Map.Entry<String, BufferedReader> nextToGo = map.pollFirstEntry();
                // Пишется в output.txt
                bw.write(nextToGo.getKey());
                // Добавляется переход на новую строчку
                bw.write("\r\n");
                // Далее берёт Reader для текущего файла
                // и считывает следующую строку в нашем файле пока в этой файле ещё ессть строки
                String line = nextToGo.getValue().readLine();

                if (line != null) {
                    // Добавляет в map в правильное место
                    map.put(line, nextToGo.getValue());
                }
            }
        }
        // Закрываем все reader`ы
        finally {
            // Закрываем все reader`ы
            if (brReaders != null) {
                for (BufferedReader br : brReaders) {
                    br.close();
                }
                // Удаляем все temp файлы
                File dir = files.get(0).getParentFile();
                for (File file : files) {
                    file.delete();
                }
                // Удаляем папку с temp файлами
                if (dir.exists()) {
                    dir.delete();
                }
            }
            // Закрываем writer
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static void mergeSortedFilesStringDescending(final List<File> files,
                                              final String outputFile,
                                              final MainComparator cmp
    )
            throws IOException {

        File f = new File(Paths.get(("target/classes/output/" + outputFile)).toAbsolutePath().toUri());
        System.out.println("\nPath of the output file: " + f);
        if (f.exists()) {
            f.delete();
        }

        TreeMap<String, ReversedLinesFileReader> map = new TreeMap<>(cmp);
        List<ReversedLinesFileReader> brReaders = new ArrayList<>();
        System.out.println("\nComparator: " + cmp.getClass());

        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        try {
            for (File file : files) {
                ReversedLinesFileReader br = new ReversedLinesFileReader(file);
                brReaders.add(br);
                String line = br.readLine();
                map.put(line, br);
            }
            while (!map.isEmpty()) {
                Map.Entry<String, ReversedLinesFileReader> nextToGo = map.pollFirstEntry();
                bw.write(nextToGo.getKey());
                bw.write("\r\n");
                String line = nextToGo.getValue().readLine();
                if (line != null) {
                    map.put(line, nextToGo.getValue());
                }
            }
        }
        finally {
            if (brReaders != null) {
                for (ReversedLinesFileReader br : brReaders) {
                    br.close();
                }
                File dir = files.get(0).getParentFile();
                for (File file : files) {
                    file.delete();
                }
                if (dir.exists()) {
                    dir.delete();
                }
            }
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static void mergeSortedFilesIntegerAscending(final List<File> files,
                                        final String outputFile,
                                        final MainComparator cmp
    )
            throws IOException {

        File f = new File(Paths.get(("target/classes/output/" + outputFile)).toAbsolutePath().toUri());
        System.out.println("\nPath of the output file: " + f);
        if (f.exists()) {
            f.delete();
        }

        TreeMap<Long, BufferedReader> map = new TreeMap<>(cmp);
        List<BufferedReader> brReaders = new ArrayList<>();
        System.out.println("\nComparator: " + cmp.getClass());

        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        try {
            for (File file : files) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                brReaders.add(br);
                String line = br.readLine();

                // Если файл начинается с пустой строчки - пропускаем эту строчку, чтобы не было NumberFormatException
                if(line != "" && line != null && line != " ") {
                    map.put(Long.valueOf(line), br);
                } else {
                    System.out.println("\nOne of your file started with a space. " +
                            "This file is not going to be included into the merge.");
                }
            }
            while (!map.isEmpty()) {
                Map.Entry<Long, BufferedReader> nextToGo = map.pollFirstEntry();
                bw.write(nextToGo.getKey().toString());
                bw.write("\r\n");
                String line = nextToGo.getValue().readLine();
                if (line != null) {
                    map.put(Long.valueOf(line), nextToGo.getValue());
                }
            }
        }
        finally {
            if (brReaders != null) {
                for (BufferedReader br : brReaders) {
                    br.close();
                }
                File dir = files.get(0).getParentFile();
                for (File file : files) {
                    //file.delete();
                }
                if (dir.exists()) {
                    //dir.delete();
                }
            }
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static void mergeSortedFilesIntegerDescending(final List<File> files,
                                               final String outputFile,
                                               final MainComparator cmp
    )
            throws IOException {

        File f = new File(Paths.get(("target/classes/output/" + outputFile)).toAbsolutePath().toUri());
        System.out.println("\nPath of the output file: " + f);
        if (f.exists()) {
            f.delete();
        }

        TreeMap<Long, ReversedLinesFileReader> map = new TreeMap<>(cmp);
        List<ReversedLinesFileReader> brReaders = new ArrayList<>();

        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        try {
            for (File file : files) {
                ReversedLinesFileReader br = new ReversedLinesFileReader(file);
                brReaders.add(br);

                String line = br.readLine();
//                try{
//                    int number = Integer.parseInt(line);
//                    System.out.println("\nThis line: '" + line + "'");
//                } catch(NumberFormatException ex){ // handle your exception
//                    throw new NumberFormatException("Current line in the file is integer. It is either string or space!");
//                }
                map.put(Long.valueOf(line), br);
            }
            while (!map.isEmpty()) {
                Map.Entry<Long, ReversedLinesFileReader> nextToGo = map.pollFirstEntry();
                bw.write(nextToGo.getKey().toString());
                bw.write("\r\n");
                String line = nextToGo.getValue().readLine();
                if (line != null) {
                    map.put(Long.valueOf(line), nextToGo.getValue());
                }
            }
        }
        finally {
            if (brReaders != null) {
                for (ReversedLinesFileReader br : brReaders) {
                    br.close();
                }
                File dir = files.get(0).getParentFile();
                for (File file : files) {
                    file.delete();
                }
                if (dir.exists()) {
                    dir.delete();
                }
            }
            if (bw != null) {
                bw.close();
            }
        }
    }

}