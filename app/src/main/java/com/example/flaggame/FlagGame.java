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
    private final Random random = new Random();
    private  Resources resources;
    private  AssetManager assets;
    private  Path pathToGameStateCsv;

    private  boolean gameIsActive;
    private  boolean showResultIsActive;
    private  List<Flag> flagsUnedited;
    private  List<Flag> flagsCurrentGame;
    private  AnswerSet answerSet;
    private  String difficulty;
    private  int roundsPlayed;
    private  int roundsWon;
    private  int roundsLost;
    private  int roundsPlayedGlobal;
    private  int roundsWonGlobal;
    private  int roundsLostGlobal;

    private static FlagGame instance;


    private FlagGame() {

    }

    public static FlagGame getInstance(){
        if(this.instance == null){
            synchronized(FlagGame.class){
                if(this.instance == null){
                    this.instance = new FlagGame();
                }
            }
        }
        return this.instance;
    }

    public  void init(MainActivity mainActivity) {
        if (flagsUnedited == null) {
            pathToGameStateCsv = Paths.get(MainActivity.getDirectory() + "/gameState.csv");
            resources = mainActivity.getResources();
            assets = mainActivity.getAssets();
            readFlagCsv();
            readGameStateCsv();
        }
    }

    public  void start(String difficulty) {
        this.difficulty = difficulty;
        flagsCurrentGame = getFlagsUneditedFilteredByDifficulty();
        gameIsActive = true;
        if (answerSet == null) {
            pickRandomCurrentFlag();
        }
    }

    public  void reset() {
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


    public  void resetLocalStats() {
        roundsPlayed = 0;
        roundsWon = 0;
        roundsLost = 0;
        writeGameStateCsv();
    }

    public  void resetGlobalStats() {
        roundsPlayedGlobal = 0;
        roundsWonGlobal = 0;
        roundsLostGlobal = 0;
        writeGameStateCsv();
    }

    public  void correctAnswer() {
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

    public  void wrongAnswer() {
        showResultIsActive = true;
        roundsLost++;
        roundsLostGlobal++;
        roundsPlayed = roundsWon + roundsLost;
        roundsPlayedGlobal = roundsWonGlobal + roundsLostGlobal;
        writeGameStateCsv();
    }

    public  void readFlagCsv() {
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

    public  void writeGameStateCsv() {
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

    public  void readGameStateCsv() {
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

    public  AnswerSet createAnswerSetFromCsv(String[] answerSetCells) {
        Flag rightAnswer = getFlagFromCountryName(answerSetCells[0]);
        List<Flag> answerChoices = new ArrayList<>();
        answerChoices.add(getFlagFromCountryName(answerSetCells[1]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[2]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[3]));
        answerChoices.add(getFlagFromCountryName(answerSetCells[4]));
        return new AnswerSet(rightAnswer, answerChoices);
    }

    public  Flag getFlagFromCountryName(String countryName) {
        Optional<Flag> flag = flagsUnedited.stream().filter(f -> f.getCountryName().equals(countryName)).findAny();
        return flag.orElse(null);
    }


    public  List<Flag> getFlagsUneditedFilteredByDifficulty() {
        switch (difficulty) {
            case "EASY":
                return flagsUnedited.stream().filter(flag -> flag.getLevelOfFamiliarity() < 4).collect(Collectors.toList());
            case "HARD":
                return flagsUnedited.stream().filter(flag -> flag.getLevelOfFamiliarity() > 3).collect(Collectors.toList());
            default:
                return flagsUnedited;
        }
    }

    public  List<Flag> getCorrectlyGuessedFlagsCurrentGame() {
        if (gameIsActive()) {
            List<Flag> flags = getFlagsUneditedFilteredByDifficulty();
            return flags.stream().filter(flag -> !flagsCurrentGame.contains(flag)).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public  void pickRandomCurrentFlag() {
        Flag newFlag = flagsCurrentGame.get( this.random.nextInt(flagsCurrentGame.size()));
        updateAnswerSet(newFlag);
        writeGameStateCsv();
    }

    public  Flag getRandomFlag() {
        return flagsUnedited.get(this.random.nextInt(flagsUnedited.size()));
    }

    private  void updateAnswerSet(Flag newFlag) {
        List<Flag> answersChoices = new ArrayList<>();
        answersChoices.add(newFlag);
        while (answersChoices.size() < 4) {
            Flag randomFlag = this.getRandomFlag();
            if (!answersChoices.contains(randomFlag)) {
                answersChoices.add(randomFlag);
            }
        }
        Collections.shuffle(answersChoices);
        this.answerSet = new AnswerSet(newFlag, answersChoices);
    }

    public  Flag getCurrentFlag() {
        return answerSet.getRightAnswer();
    }

    public  AnswerSet getAnswerSet() {
        return this.answerSet;
    }

    public  int getRoundsPlayed() {
        return roundsPlayed;
    }

    public  int getRoundsWon() {
        return roundsWon;
    }

    public  int getRoundsLost() {
        return roundsLost;
    }

    public  int getRoundsPlayedGlobal() {
        return roundsPlayedGlobal;
    }

    public  int getRoundsWonGlobal() {
        return roundsWonGlobal;
    }

    public  int getRoundsLostGlobal() {
        return roundsLostGlobal;
    }

    public  boolean gameIsActive() {
        return gameIsActive;
    }

    public  boolean isShowResultIsActive() {
        return showResultIsActive;
    }

    public  void setShowResultIsActive(boolean showResultIsActive) {
        this.showResultIsActive = showResultIsActive;
    }
}


