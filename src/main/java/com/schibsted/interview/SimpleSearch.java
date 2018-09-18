package com.schibsted.interview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleSearch {

    private final Index index;

    public SimpleSearch(Index index) {
        this.index = index;
    }

    public List<SearchResult> search(String phrase) {
        HashMap<String, Integer> results = new HashMap<>();
        String[] phraseWords = phrase.replaceAll("\\p{Punct}", " ").trim().split("\\s+");
        IntStream.range(0, phraseWords.length)
                .filter(wordIndex -> index.getIndex().containsKey(phraseWords[wordIndex]))
                .forEach(wordIndex -> index.getIndex().get(phraseWords[wordIndex]).keySet()
                        .forEach((fileName) -> updateFileScore(
                                results,
                                fileName,
                                getMaxScoreFromFile(fileName, phraseWords, wordIndex))));

        return results.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new SearchResult(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Integer getMaxScoreFromFile(String fileName, String[] phrase, Integer wordIndex) {
        Integer maxScore = -1;
        for (Integer wordPosition : index.getIndex().get(phrase[wordIndex]).get(fileName)) {
            Integer currentScore = checkScore(phrase, wordIndex, wordPosition, fileName) * 100 / phrase.length;
            if (currentScore >= maxScore) {
                maxScore = currentScore;
            }
        }
        return maxScore;
    }

    private void updateFileScore(HashMap<String, Integer> results, String fileName, Integer score) {
        if (!results.containsKey(fileName) || results.get(fileName) < score) {
            results.put(fileName, score);
        }
    }

    private Integer checkScore(String[] phrase, Integer wordIndex, Integer wordPosition, String fileName) {
        if (wordIndex >= phrase.length) {
            return 0;
        }
        if (isWordPresentAtPosition(phrase[wordIndex], fileName, wordPosition)) { //file
            return 1 + checkScore(phrase, wordIndex + 1, wordPosition + 1, fileName);
        }
        return 0;
    }

    private boolean isWordPresentAtPosition(String word, String fileName, Integer position) {
        return index.getIndex().containsKey(word) && index.getIndex().get(word).containsKey(fileName)
                && index.getIndex().get(word).get(fileName).contains(position);
    }
}
