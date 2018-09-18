package com.schibsted.interview;

import java.io.File;
import java.util.List;
import java.util.Scanner;


public class SearchConsole {

    public static void main(String[] args) {
        if (args.length == 0) {
            returnError("No directory given to index.");
        }
        final String indexableDirectory = args[0];
        File directory = new File(indexableDirectory);
        if (!directory.isDirectory()){
            returnError("\""+indexableDirectory+"\" is not a valid path.");
        }

        Index index = new Index.InvertedIndexBuilder(indexableDirectory).build();
        SimpleSearch simpleSearch = new SimpleSearch(index.getIndex());

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.print("search> ");
                final String line = input.nextLine().toLowerCase();
                if (line.equals(":exit") || line.equals(":quit")){
                    System.exit(0);
                }
                printResult(simpleSearch.search(line));
            }
        }
    }

    private static void printResult(List<SearchResult> results) {
        String format = "%-32s%s%n";
        if (results.isEmpty()){
            System.out.println("No results found :[");
        }
        int rank = 1;
        for (SearchResult result : results) {
            System.out.printf(format, rank + ") " + "File: " + result.getFileName(), "score: " + result.getScore() + "%");
            rank++;
        }
    }

    private static void returnError(String errorMessage) {
        System.err.println(errorMessage);
        System.out.println("Usage: java simplesearch <path to indexable directory>");
        System.exit(1);
    }
}



