package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countMinesOnField;
    private int countFlags;
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
        countFlags = countMinesOnField;
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

    private void openTile(int x, int y) {
        gameField[y][x].isOpen = true;
        if (!gameField[y][x].isMine) {
            if (gameField[y][x].countMineNeighbors == 0) {
                setCellValue(x, y, "");
                setCellColor(x, y, Color.DARKGRAY);
                ArrayList<GameObject> neighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
                for (GameObject neighbor : neighbors) {
                    if (!neighbor.isOpen) {
                        openTile(neighbor.x, neighbor.y);
                    }
                }
            } else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                setCellColor(x, y, Color.DARKGRAY);
            }
        } else {
            setCellValue(x, y, MINE);
            setCellColor(x, y, Color.RED);
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        openTile(x, y);
    }
}
