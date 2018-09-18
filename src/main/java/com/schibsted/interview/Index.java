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
    /* Mapping: Term -> Map<FileName, SetOfTermPositionsInFile> */
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
                Long readFilesCount = filesInDirectory
                        .filter(InvertedIndexBuilder::isTxtFile)
                        .peek(this::parseFile).count();
                printFilesCountInDirectory(readFilesCount, indexableDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void parseFile(Path filePath) {
            AtomicInteger wordPositionCounter = new AtomicInteger(1);
            try (Stream<String> fileLines = lines(filePath)) {
                fileLines.map(lines -> lines.toLowerCase().replaceAll("\\p{Punct}", " ").trim().split("\\s+"))
                        .forEach(line -> processWords(line, filePath.getFileName().toString(), wordPositionCounter));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processWords(String[] words, String fileName, AtomicInteger wordPositionCounter) {
            for (String word : words) {
                if (invertedIndex.containsKey(word))
                    if (invertedIndex.get(word).containsKey(fileName))
                        /* Adding new word position for current wordInFile->positions mapping */
                        invertedIndex.get(word).get(fileName).add(wordPositionCounter.get());
                        /* Index not contain mapping for word */
                    else addNewWordMapping(word, fileName, wordPositionCounter.get());
                    /* Index contain mapping for word but not for specific file */
                else addNewWordMapping(word, fileName, wordPositionCounter.get());
                wordPositionCounter.getAndIncrement();
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

        private static void printFilesCountInDirectory(Long filesNumber, String directory) {
            System.out.println(filesNumber + " files read in directory " + directory);
        }
    }
}
