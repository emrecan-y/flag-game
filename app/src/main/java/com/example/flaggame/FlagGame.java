package com.example.flaggame;

import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class FlagGame {
    private static final Random RANDOM = new Random();
    private static Resources resources;
    private static AssetManager assets;
    private static Path pathToGameStateCsv;


    private static boolean gameIsActive;
    private static boolean showResultIsActive;
    private static List<Flag> flagsUnedited;
    private static List<Flag> flagsCurrentGame;
    private static AnswerSet answerSet;
    private static String difficulty;
    private static int roundsPlayed;
    private static int roundsWon;
    private static int roundsLost;
    private static int roundsPlayedGlobal;
    private static int roundsWonGlobal;
    private static int roundsLostGlobal;


    private FlagGame() {

    }

    public static void init(MainActivity mainActivity) {
        if (flagsUnedited == null) {
            pathToGameStateCsv = Paths.get(MainActivity.getDirectory() + "/gameState.csv");
            resources = mainActivity.getResources();
            assets = mainActivity.getAssets();
            readFlagCsv();
            readGameStateCsv();
        }
    }

    public static void start(String difficulty) {
        FlagGame.difficulty = difficulty;
        flagsCurrentGame = getFlagsUneditedFilteredByDifficulty();
        gameIsActive = true;
        if (answerSet == null) {
            pickRandomCurrentFlag();
        }
    }

    public static void reset() {
        resetLocalStats();
        flagsCurrentGame.clear();
        gameIsActive = false;
        answerSet = null;
        difficulty = null;
        roundsPlayed = 0;
        roundsWon = 0;
        roundsLost = 0;
        writeGameStateCsv();
    }


    public static void resetLocalStats() {
        roundsPlayed = 0;
        roundsWon = 0;
        roundsLost = 0;
        writeGameStateCsv();
    }

    public static void resetGlobalStats() {
        roundsPlayedGlobal = 0;
        roundsWonGlobal = 0;
        roundsLostGlobal = 0;
        writeGameStateCsv();
    }

    public static void correctAnswer() {
        //Bodge fix for emtpy flaglist
        if (flagsCurrentGame.size() > 1) {
            flagsCurrentGame.remove(answerSet.getRightAnswer());
        }
        showResultIsActive = true;
        roundsWon++;
        roundsWonGlobal++;
        roundsPlayed = roundsWon + roundsLost;
        roundsPlayedGlobal = roundsWonGlobal + roundsLostGlobal;
        writeGameStateCsv();
    }

    public static void wrongAnswer() {
        showResultIsActive = true;
        roundsLost++;
        roundsLostGlobal++;
        roundsPlayed = roundsWon + roundsLost;
        roundsPlayedGlobal = roundsWonGlobal + roundsLostGlobal;
        writeGameStateCsv();
    }

    public static void readFlagCsv() {
        flagsUnedited = new ArrayList<>();
        String currentLine;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open("flags.csv"), StandardCharsets.UTF_8))) {
            while ((currentLine = reader.readLine()) != null) {
                String[] cells = currentLine.split(";");
                Flag newFlag = new Flag(cells[0], cells[1], Integer.parseInt(cells[2]), resources);
                flagsUnedited.add(newFlag);
            }
            flagsCurrentGame = new ArrayList<>(flagsUnedited);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeGameStateCsv() {
        try (BufferedWriter writer = Files.newBufferedWriter(pathToGameStateCsv)) {
            StringBuilder builderStats = new StringBuilder();
            builderStats.append(gameIsActive);
            builderStats.append(";");
            builderStats.append(difficulty);
            builderStats.append(";");
            builderStats.append(roundsPlayed);
            builderStats.append(";");
            builderStats.append(roundsWon);
            builderStats.append(";");
            builderStats.append(roundsLost);
            builderStats.append(";");
            builderStats.append(roundsPlayedGlobal);
            builderStats.append(";");
            builderStats.append(roundsWonGlobal);
            builderStats.append(";");
            builderStats.append(roundsLostGlobal);
            builderStats.append(";");
            writer.write(builderStats.toString());
            writer.newLine();

            StringBuilder builderAnswerSet = new StringBuilder();
            if (answerSet != null) {
                builderAnswerSet.append(answerSet.getRightAnswer().getCountryName());
                builderAnswerSet.append(";");
                for (Flag flag : answerSet.getAnswersChoices()) {
                    builderAnswerSet.append(flag.getCountryName());
                    builderAnswerSet.append(";");
                }
            }
            writer.write(builderAnswerSet.toString());
            writer.newLine();

            StringBuilder builderFlagsCurrentGame = new StringBuilder();
            for (Flag flag : flagsCurrentGame) {
                builderFlagsCurrentGame.append(flag.getCountryName());
                builderFlagsCurrentGame.append(";");
            }
            writer.write(builderFlagsCurrentGame.toString());
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readGameStateCsv() {
        String currentLine;
        try (BufferedReader reader = Files.newBufferedReader(pathToGameStateCsv)) {
            currentLine = reader.readLine();
            String[] statsCells = currentLine.split(";");
            gameIsActive = Boolean.parseBoolean(statsCells[0]);
            difficulty = statsCells[1];
            roundsPlayed = Integer.parseInt(statsCells[2]);
            roundsWon = Integer.parseInt(statsCells[3]);
            roundsLost = Integer.parseInt(statsCells[4]);
            roundsPlayedGlobal = Integer.parseInt(statsCells[5]);
            roundsWonGlobal = Integer.parseInt(statsCells[6]);
            roundsLostGlobal = Integer.parseInt(statsCells[7]);

            currentLine = reader.readLine();
            if (currentLine != null) {
                String[] answerSetCells = currentLine.split(";");
                answerSet = createAnswerSetFromCsv(answerSetCells);
            }

            currentLine = reader.readLine();
            if (currentLine != null) {
                String[] flagsCurrentGameCells = currentLine.split(";");
                flagsCurrentGame.clear();
                for (String countryName : flagsCurrentGameCells) {
                    flagsCurrentGame.add(getFlagFromCountryName(countryName));
                }
            }

        } catch (IOException e) {
            System.out.println("No file available");
        }
    }

    public static AnswerSet createAnswerSetFromCsv(String[] answerSetCells) {
        Flag rightAnswer = getFlagFromCountryName(answerSetCells[0]);
        List<Flag> answerChoices = new ArrayList<>();
        answerChoices.add(getFlagFromCountryName(answerSetCells[1]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[2]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[3]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[4]));
        return new AnswerSet(rightAnswer, answerChoices);
    }

    public static Flag getFlagFromCountryName(String countryName) {
        Optional<Flag> flag = flagsUnedited.stream().filter(f -> f.getCountryName().equals(countryName)).findAny();
        return flag.orElse(null);
    }


    public static List<Flag> getFlagsUneditedFilteredByDifficulty() {
        switch (difficulty) {
            case "EASY":
                return flagsUnedited.stream().filter(flag -> flag.getLevelOfFamiliarity() < 4).collect(Collectors.toList());
            case "HARD":
                return flagsUnedited.stream().filter(flag -> flag.getLevelOfFamiliarity() > 3).collect(Collectors.toList());
            default:
                return flagsUnedited;
        }
    }

    public static List<Flag> getCorrectlyGuessedFlagsCurrentGame() {
        if (gameIsActive()) {
            List<Flag> flags = getFlagsUneditedFilteredByDifficulty();
            return flags.stream().filter(flag -> !flagsCurrentGame.contains(flag)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public static void pickRandomCurrentFlag() {
        Flag newFlag = flagsCurrentGame.get(RANDOM.nextInt(flagsCurrentGame.size()));
        updateAnswerSet(newFlag);
        writeGameStateCsv();
    }

    public static Flag getRandomFlag() {
        return flagsUnedited.get(RANDOM.nextInt(flagsUnedited.size()));
    }

    private static void updateAnswerSet(Flag newFlag) {
        List<Flag> answersChoices = new ArrayList<>();
        answersChoices.add(newFlag);
        while (answersChoices.size() < 4) {
            Flag randomFlag = FlagGame.getRandomFlag();
            if (!answersChoices.contains(randomFlag)) {
                answersChoices.add(randomFlag);
            }
        }
        Collections.shuffle(answersChoices);
        FlagGame.answerSet = new AnswerSet(newFlag, answersChoices);
    }

    public static Flag getCurrentFlag() {
        return answerSet.getRightAnswer();
    }

    public static AnswerSet getAnswerSet() {
        return FlagGame.answerSet;
    }

    public static int getRoundsPlayed() {
        return roundsPlayed;
    }

    public static int getRoundsWon() {
        return roundsWon;
    }

    public static int getRoundsLost() {
        return roundsLost;
    }

    public static int getRoundsPlayedGlobal() {
        return roundsPlayedGlobal;
    }

    public static int getRoundsWonGlobal() {
        return roundsWonGlobal;
    }

    public static int getRoundsLostGlobal() {
        return roundsLostGlobal;
    }

    public static boolean gameIsActive() {
        return gameIsActive;
    }

    public static boolean isShowResultIsActive() {
        return showResultIsActive;
    }

    public static void setShowResultIsActive(boolean showResultIsActive) {
        FlagGame.showResultIsActive = showResultIsActive;
    }
}


