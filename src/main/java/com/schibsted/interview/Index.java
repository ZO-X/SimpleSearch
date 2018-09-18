package com.schibsted.interview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

public class Index {
    private final HashMap<String, HashMap<String, HashSet<Integer>>> index;

    private Index(InvertedIndexBuilder builder) {
        this.index = builder.invertedIndex;
    }

    public HashMap<String, HashMap<String, HashSet<Integer>>> getIndex() {
        return index;
    }


    public static class InvertedIndexBuilder {
        private String inexableDirectory;
        private HashMap<String, HashMap<String, HashSet<Integer>>> invertedIndex;

        public InvertedIndexBuilder(String inexableDirectory) {
            this.inexableDirectory = inexableDirectory;
        }

        public Index build() {
            createInvertedIndex(inexableDirectory);
            return new Index(this);
        }

        private void createInvertedIndex(String indexableDirectory) {
            this.invertedIndex = new HashMap<>();
            try (Stream<Path> filesInDirectory = Files.walk(Paths.get(indexableDirectory))) {
                filesInDirectory
                        .filter(InvertedIndexBuilder::isTxtFile)
                        .forEach(this::parseFile);
                printFileNumberInDirectory(indexableDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void parseFile(Path filePath) {
            AtomicInteger wordPositionCounter = new AtomicInteger(1);
            try (Stream<String> linesStream = lines(filePath)) {
                linesStream.map(String::toLowerCase)
                        .forEach(line -> {
                            String words[] = line.replaceAll("\\p{Punct}", " ").trim().split("\\s+");
                            final String fileName = filePath.getFileName().toString();
                            for (String word : words) {
                                if (invertedIndex.containsKey(word)) {
                                    // Adding new word position for current wordInFile->positions mapping
                                    if (invertedIndex.get(word).containsKey(fileName)) {
                                        invertedIndex.get(word).get(fileName).add(wordPositionCounter.get());
                                        // Index contain mapping for word but not for specific file
                                    } else {
                                        addNewWordMapping(word, fileName, wordPositionCounter.get());
                                    }
                                    // Index not contain mapping for word
                                } else {
                                    addNewWordMapping(word, fileName, wordPositionCounter.get());
                                }
                                wordPositionCounter.getAndIncrement();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void addNewWordMapping(String word, String fileName, Integer wordPositionCounter) {
            HashMap<String, HashSet<Integer>> fileToWordPositionsMap;
            if (invertedIndex.containsKey(word)) {
                fileToWordPositionsMap = invertedIndex.get(word);
            } else {
                fileToWordPositionsMap = new HashMap<>();
            }
            HashSet<Integer> newSet = new HashSet<>();
            newSet.add(wordPositionCounter);
            fileToWordPositionsMap.put(fileName, newSet);
            invertedIndex.put(word, fileToWordPositionsMap);
        }

        private static boolean isTxtFile(Path file) {
            if (Files.isRegularFile(file)) {
                String fileName = file.getFileName().toString();
                int extensionPosition = fileName.lastIndexOf('.');
                return fileName.substring(extensionPosition + 1).toLowerCase().equals("txt");
            }
            return false;
        }

        private static void printFileNumberInDirectory(String directory){
            System.out.println(Objects.requireNonNull(new File(directory).list()).length + " files read in directory " + directory);
        }
    }
}
