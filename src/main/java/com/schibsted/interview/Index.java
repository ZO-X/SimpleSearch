package com.schibsted.interview;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

public class Index {

    private final HashMap<String, HashMap<String, HashSet<Integer>>> index;

    public static Index createInvertedIndex(String indexableDirectory) {
        HashMap<String, HashMap<String, HashSet<Integer>>> invertedIndex = new HashMap<>();
        try (Stream<Path> filesInDirectory = Files.walk(Paths.get(indexableDirectory))) {
            filesInDirectory
                    .filter(Index::isTxtFile)
                    .forEach(txtFile -> {
                        AtomicInteger wordPositionCounter = new AtomicInteger(1);
                        parseFile(txtFile, wordPositionCounter, invertedIndex);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Index(invertedIndex);
    }

    private Index(HashMap<String, HashMap<String, HashSet<Integer>>> index) {
        this.index = index;
    }

    public HashMap<String, HashMap<String, HashSet<Integer>>> getIndex() {
        return index;
    }

    private static void parseFile(Path filePath, AtomicInteger wordPositionCounter,
                                  HashMap<String, HashMap<String, HashSet<Integer>>> index) {
        try (Stream<String> linesStream = lines(filePath)) {
            linesStream.forEach(line -> {
                String words[] = line.split(" ");
                final String fileName = filePath.getFileName().toString();
                for (String word : words) {
                    if (index.containsKey(word)) {
                        // Adding new word position for current wordInFile->positions mapping
                        if (index.get(word).containsKey(fileName)) {
                            index.get(word).get(fileName).add(wordPositionCounter.get());
                            // Index contain mapping for word but not for specific file
                        } else {
                            addNewWordMapping(word, fileName, wordPositionCounter, index);
                        }
                        // Index not contain mapping for word
                    } else {
                        addNewWordMapping(word, fileName, wordPositionCounter, index);
                    }
                    wordPositionCounter.getAndIncrement();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addNewWordMapping(String word, String fileName, AtomicInteger wordPositionCounter,
                                          HashMap<String, HashMap<String, HashSet<Integer>>> index) {
        HashMap<String, HashSet<Integer>> fileToWordPositionsMap;
        if (index.containsKey(word)) {
            fileToWordPositionsMap = index.get(word);
        } else {
            fileToWordPositionsMap = new HashMap<>();
        }
        HashSet<Integer> newSet = new HashSet<>();
        newSet.add(wordPositionCounter.get());
        fileToWordPositionsMap.put(fileName, newSet);
        index.put(word, fileToWordPositionsMap);
    }

    private static boolean isTxtFile(Path file) {
        if (Files.isRegularFile(file)) {
            String fileName = file.getFileName().toString();
            int extensionPosition = fileName.lastIndexOf('.');
            if (fileName.substring(extensionPosition + 1).toLowerCase().equals("txt"))
                return true;
            else
                return false;
        }
        return false;
    }
}
