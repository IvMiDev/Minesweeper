package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;

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

    public ArrayList<GameObject> getNeighbors(GameObject gameObject) {
        ArrayList<GameObject> neighbors = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                try {
                    if (!(gameField[gameObject.y + y][gameObject.x + x]).equals(gameObject)) {
                        neighbors.add(gameField[gameObject.y + y][gameObject.x + x]);
                    }
                }catch (IndexOutOfBoundsException ex) {
                }
            }
        }
        return neighbors;
    }

    private void countMineNeighbors() {
        ArrayList<GameObject> mineNeighbors = new ArrayList<>();
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                if (!gameField[y][x].isMine) {
                    mineNeighbors.addAll(getNeighbors(gameField[y][x]));
                    for (GameObject neighbor : mineNeighbors) {
                        if (neighbor.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                    mineNeighbors.clear();
                }
            }
        }
    }
}
