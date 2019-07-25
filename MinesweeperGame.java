package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countMinesOnField;
    private int countFlags;
    private int score = 0;
    private int countClosedTiles = SIDE * SIDE;
    private boolean isGameStopped;
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
                setCellValue(x, y, "");
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
                } catch (IndexOutOfBoundsException ex) {
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
        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isOpen = true;
            if (!gameField[y][x].isMine) {
                score += 5;
                setScore(score);
                countClosedTiles--;
                if (countClosedTiles == countMinesOnField) {
                    win();
                }
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
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
        }
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Congratulations! You won!!!", Color.GREEN, 45);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 90);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }

    private void markTile(int x, int y) {
        if (!gameField[y][x].isOpen) {
            if (!gameField[y][x].isFlag && countFlags > 0) {
                countFlags--;
                gameField[y][x].isFlag = true;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);
            } else if (gameField[y][x].isFlag) {
                countFlags++;
                gameField[y][x].isFlag = false;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.GRAY);
            }
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
       if (!isGameStopped) {
           openTile(x, y);
       } else restart();
    }
}
