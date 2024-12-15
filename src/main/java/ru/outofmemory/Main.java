package ru.outofmemory;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final String DEFAULT_INTEGERS_FILENAME = "integers.txt";
    private static final String DEFAULT_FLOATS_FILENAME = "floats.txt";
    private static final String DEFAULT_STRINGS_FILENAME = "strings.txt";
    private static final String DEFAULT_OUTPUT_PATH = ".";
    private static final String DEFAULT_PREFIX = "";

    // Will be parsed from command line options
    // (see 'parseCommandLineOptions' function)
    private static boolean appendMode;
    private static boolean showShortStats;
    private static boolean showFullStats;
    private static String outputPath;
    private static String prefix;
    private static List<String> files;

    static List<String> integers = new ArrayList<>();
    static List<String> floats = new ArrayList<>();
    static List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        // If parsing command line options was not successful - exits the program
        parseCommandLineOptions(args);

        // Sorting lines by their types in each file by Lists (integers, floats, strings)
        for (String filename : files) {
             sortLinesFromFile(filename);
        }

        // Generate full file paths
        String integersFullFilePath = getFullFilePath(outputPath, prefix, DEFAULT_INTEGERS_FILENAME);
        String floatsFullFilePath = getFullFilePath(outputPath, prefix, DEFAULT_FLOATS_FILENAME);
        String stringsFullFilePath = getFullFilePath(outputPath, prefix, DEFAULT_STRINGS_FILENAME);

        // Write lists to files
        writeListToFile(integers, integersFullFilePath);
        writeListToFile(floats, floatsFullFilePath);
        writeListToFile(strings, stringsFullFilePath);

        // Print statistics if requested
        if (showShortStats) {
            printShortStats(integersFullFilePath);
            printShortStats(floatsFullFilePath);
            printShortStats(stringsFullFilePath);

        } else if (showFullStats) {
            printFullIntegerStats(integersFullFilePath);
            printFullFloatStats(floatsFullFilePath);
            printFullStringStats(stringsFullFilePath);
        }
    }

    public static void parseCommandLineOptions(String[] args) {
        if (args == null) return;

        Options options = new Options();

        options.addOption("h", "help", false, "Show help message");
        options.addOption("s", "short", false, "Show short statistics");
        options.addOption("f", "full", false, "Show full statistics");
        options.addOption("a", "append", false, "Append to file instead of rewrite");
        options.addOption("o", "output", true, "Path for result files");
        options.addOption("p", "prefix", true, "Output filename prefix");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Main", options);
                System.exit(0);
            }

            appendMode = cmd.hasOption("a");
            showShortStats = cmd.hasOption("s");
            showFullStats = cmd.hasOption("f");
            outputPath = cmd.getOptionValue("o", DEFAULT_OUTPUT_PATH);
            prefix = cmd.getOptionValue("p", DEFAULT_PREFIX);
            files = List.of(cmd.getArgs());

            if (files.isEmpty()) {
                System.out.println("No files to sorting");
                System.exit(0);
            }

            if (showShortStats && showFullStats) {
                System.out.println("WARNING: Flags -s and -f cannot be used together. Flag -s will be ignored");
                showShortStats = false;
            }

        } catch (ParseException e) {
            System.out.printf("Command line options parsing failed: %s\n", e.getMessage());
            System.exit(1);
        }
    }

    public static void sortLinesFromFile(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            reader.lines()
                    .map(String::trim)
                    .forEach(Main::sortLineToList);

        } catch (IOException e) {
            System.out.printf("Error read file '%s': %s\n", filepath, e.getMessage());
        }
    }

    public static String getFullFilePath(String path, String prefix, String filename) {
        return Paths.get(path, prefix + filename).toString();
    }

    public static void writeListToFile(List<String> list, String filepath) {
        if (list == null || list.isEmpty()) return;

        try {
            Files.createDirectories(Path.of(outputPath));
        } catch (IOException e) {
            System.out.printf("Error creating output path: %s\n", e.getMessage());
            System.exit(1);
        }

        boolean append = appendMode && Files.exists(Path.of(filepath));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, append))) {
            for (String line : list) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.printf("Error writing list to file: %s\n", e.getMessage());
        }
    }

    public static void printShortStats(String filepath) {
        if (Files.notExists(Path.of(filepath))) {
            System.out.printf("\nFile '%s' was not created (0 items)\n", filepath);
            return;
        }

        List<String> list = fileToListOfLines(filepath);
        System.out.printf("\nIn '%s': %d items\n", filepath, list.size());
    }

    public static void printFullIntegerStats(String filepath) {
        printShortStats(filepath);

        if (Files.notExists(Path.of(filepath))) return;

        List<String> list = fileToListOfLines(filepath);

        if(list.isEmpty()) return;

        List<Long> longs = list.stream()
                .map(Long::parseLong)
                .toList();

        Optional<Long> maxOptional = longs.stream().max(Long::compare);
        Optional<Long> minOptional = longs.stream().min(Long::compare);
        OptionalDouble avgOptional = longs.stream().mapToLong(Long::longValue).average();
        long sum = longs.stream().mapToLong(Long::longValue).sum();

        maxOptional.ifPresent(max -> {
            minOptional.ifPresent(min -> {
                avgOptional.ifPresent(avg -> {
                    System.out.printf("MAX=%d, MIN=%d, AVG=%f, SUM=%d\n", max, min, avg, sum);
                });
            });
        });
    }

    public static void printFullFloatStats(String filepath) {
        printShortStats(filepath);

        if (Files.notExists(Path.of(filepath))) return;

        List<String> list = fileToListOfLines(filepath);

        if(list.isEmpty()) return;

        List<Double> longs = list.stream()
                .map(Double::parseDouble)
                .toList();

        Optional<Double> maxOptional = longs.stream().max(Double::compare);
        Optional<Double> minOptional = longs.stream().min(Double::compare);
        OptionalDouble avgOptional = longs.stream().mapToDouble(Double::doubleValue).average();
        double sum = longs.stream().mapToDouble(Double::doubleValue).sum();

        maxOptional.ifPresent(max -> {
            minOptional.ifPresent(min -> {
                avgOptional.ifPresent(avg -> {
                    System.out.printf("MAX=%f, MIN=%f, AVG=%f, SUM=%f\n", max, min, avg, sum);
                });
            });
        });
    }

    public static void printFullStringStats(String filepath) {
        printShortStats(filepath);

        if (Files.notExists(Path.of(filepath))) return;

        List<String> list = fileToListOfLines(filepath);

        if(list.isEmpty()) return;

        OptionalInt maxOptional = list.stream().mapToInt(String::length).max();
        OptionalInt minOptional = list.stream().mapToInt(String::length).min();

        maxOptional.ifPresent(max -> {
            minOptional.ifPresent(min -> {
                System.out.printf("MAX=%d, MIN=%d\n", max, min);
            });
        });
    }

    public static List<String> fileToListOfLines(String filepath) {
        try {
            return Files.readAllLines(Path.of(filepath));

        } catch (IOException e) {
            System.out.printf("Error read file: %s\n", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void sortLineToList(String line) {
        if(isInteger(line)) {
            integers.add(line);

        } else if (isFloat(line)) {
            floats.add(line);

        } else {
            strings.add(line);
        }
    }

    public static boolean isInteger(String line) {
        try {
            Long.parseLong(line);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isFloat(String line) {
        try {
            Double.parseDouble(line);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
