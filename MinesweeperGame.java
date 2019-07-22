package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private int countMinesOnField;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        boolean isMine;
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                if (getRandomNumber(10) == 1) {
                    isMine = true;
                    gameField[y][x] = new GameObject(x, y, isMine);
                    countMinesOnField++;
                } else {
                    isMine = false;
                    gameField[y][x] = new GameObject(x, y, isMine);
                }
                setCellColor(x, y, Color.GRAY);
            }
        }
        countMineNeighbors();
    }

    public void getNeighbors(GameObject gameObject) {

    }

    private void countMineNeighbors() {
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                getNeighbors(gameField[y][x]);
            }
        }
    }
}
