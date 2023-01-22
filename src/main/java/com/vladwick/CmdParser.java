package com.vladwick;

import org.apache.commons.cli.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdParser {
    public void parse( String[] args ) {
        Options options = new Options();

        Option ascending_mode = new Option("a", "asc", false, "Sorting mode: ascending");
        ascending_mode.setRequired(false);
        options.addOption(ascending_mode);

        Option descending_mode = new Option("d", "desc", false, "Sorting mode: descending");
        descending_mode.setRequired(false);
        options.addOption(descending_mode);

        Option string_type = new Option("s", "string", false, "Values type: string");
        string_type.setRequired(false);
        options.addOption(string_type);

        Option integer_type = new Option("i", "integer", false, "Values type: integer");
        integer_type.setRequired(false);
        options.addOption(integer_type);

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        // Проверка библиотекой 'commons-cli' вводимых аргументов
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Accepted options: ", options);
            System.exit(1);
            return;
        }

        // Дополнительная проверка корректности ввода аргументов
        if(cmd.hasOption("a")  && cmd.hasOption("d")) {
            System.out.println("You enter both arguments 'a' and 'd'. " +
                    "That`s inappropriate. " +
                    "(ascending was chosen by default)");
        } else if (!cmd.hasOption("a")  && !cmd.hasOption("d")) {
            System.out.println("'Ascending' option was chosen by default");
        } else if(cmd.hasOption("a")) {
            System.out.println("'Ascending' option was chosen specifically");
            App.mode = "ascending";
        } else if (cmd.hasOption("d")) {
            System.out.println("'Descending' option was chosen specifically");
            App.mode = "descending";
        }

        if(cmd.hasOption("s")  && cmd.hasOption("i")) {
            System.out.println("You enter both arguments 's' and 'i'. " +
                    "That`s inappropriate. ");
            throw new RuntimeException("You should enter only one argument 's' or 'i'.");
        } else if (!cmd.hasOption("s")  && !cmd.hasOption("i")) {
            throw new RuntimeException("You should enter one argument 's' or 'i'.");
        } else if(cmd.hasOption("s")) {
            System.out.println("'String' option was chosen specifically");
            App.type = "string";
        } else if (cmd.hasOption("i")) {
            System.out.println("'Integer' option was chosen specifically");
            App.type = "integer";
        }

        // Проверка введённых имён файлов
        App.namesOfFiles = cmd.getArgList();
        check();

        System.out.println("\nAll checks passed successfully!");
        System.out.println("Files to work with: " + App.namesOfFiles + "\n");
    }

    private void check() {
        List<String> array = App.namesOfFiles;
        // Проверка каждого введённого файла
        for(int i = 0; i < array.size(); ++i) {
            array.set(i, checkString(array.get(i)));
        }
        // Проверка массиве в целом
        array = checkArray(array);
        App.namesOfFiles = array;
    }

    private String checkString(String str) {
        if(str.contains("/") || str.contains("\\") || str.contains(",")) {
            throw new RuntimeException("You enter inappropriate name of the file: '" + str + "'");
        }
        if(str.contains(".") && str.length() == 1) {
            throw new RuntimeException("You enter inappropriate name of the file: '" + str + "'");
        }
        return str;
    }

    private List<String> checkArray(List<String> array) {
        if(array.size() < 2) {
            throw new RuntimeException("You should enter at least two files!");
        }

        // Проверка на повторы
        Set<String> set = new HashSet<>();
        for(String str: array) {
            set.add(str);
        }
        if(set.size() != array.size()) {
            throw new RuntimeException("You input has repeats!");
        }
        return array;
    }
}