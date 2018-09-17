package com.schibsted.interview;

import java.io.File;
import java.util.List;
import java.util.Scanner;


public class SearchSearch {

    public static void main(String[] args) {
        if (args.length == 0) {
            returnError("No directory given to index.");
        }
        final String indexableDirectory = new String(args[0]);
        File directory = new File(indexableDirectory);
        if (!directory.isDirectory()){
            returnError("\""+indexableDirectory+"\" is not a valid path.");
        }

        Index index = Index.createInvertedIndex(indexableDirectory);
        SimpleSearchEngine simpleSearchEngine = new SimpleSearchEngine(index.getIndex());

        Scanner keyboard = new Scanner(System.in);
        while (true) {
            System.out.printf("search> ");
            final String line = keyboard.nextLine();
            printResult(simpleSearchEngine.search(line));
        }
    }

    private static void printResult(List<SearchResult> results) {
        String format = "%-34s%s%n";
        if (results.isEmpty()){
            System.out.println("No results found :(");
        }
        for (SearchResult result : results) {
            System.out.printf(format, "File: " + result.getFileName(), "score: " + result.getScore() + "%");
        }
    }

    private static void returnError(String errorMessage) {
        System.err.println(errorMessage);
        System.out.println("Usage: java simplesearch <path to indexable directory>");
        System.exit(1);
    }
}



