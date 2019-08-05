package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField;
    private boolean isGameStopped;
    private int SIDE, countMinesOnField, countFlags, score, countClosedTiles;


    @Override
    public void initialize() {
        SIDE = 9;
        setScreenSize(SIDE, SIDE);
        gameField = new GameObject[SIDE][SIDE];
        countClosedTiles = SIDE * SIDE;
        score = 0;
        createGame();
    }

    private void createGame() {
        isGameStopped = false;
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                gameField[y][x] = new GameObject(x, y, false);
                setCellValueEx(x, y, Color.GRAY, "");
                setCellTextColor(x, y, Color.BLACK);
            }
        }
    }

    private void setMines(int x, int y) {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField.length; j++) {
                if (!(gameField[j][i].getX() == x && gameField[j][i].getY() == y)) {
                    if (getRandomNumber(10) == 1) {
                        gameField[j][i].setMine();
                        countMinesOnField++;
                        countFlags++;
                    }
                }
            }
        }
        countMineNeighbors();
    }

    private ArrayList<GameObject> getNeighbors(GameObject gameObject) {
        ArrayList<GameObject> neighbors = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                try {
                    if (!(gameField[gameObject.getY() + y][gameObject.getX() + x]).equals(gameObject)) {
                        neighbors.add(gameField[gameObject.getY() + y][gameObject.getX() + x]);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        return neighbors;
    }

    private void countMineNeighbors() {
        ArrayList<GameObject> mineNeighbors = new ArrayList<>();
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                if (!gameField[y][x].isMine()) {
                    mineNeighbors.addAll(getNeighbors(gameField[y][x]));
                    for (GameObject neighbor : mineNeighbors) {
                        if (neighbor.isMine()) {
                            int countMineNeighbor = gameField[y][x].getCountMineNeighbors();
                            countMineNeighbor++;
                            gameField[y][x].setCountMineNeighbors(countMineNeighbor);
                        }
                    }
                    mineNeighbors.clear();
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (countClosedTiles == SIDE * SIDE) {
            setMines(x, y);
        }
        if (!gameField[y][x].isOpen() && !gameField[y][x].isFlag() && !isGameStopped) {
            gameField[y][x].setOpen();
            if (!gameField[y][x].isMine()) {
                setScore(score += 5);
                countClosedTiles--;
                if (countClosedTiles == countMinesOnField) {
                    win();
                }
                switch (gameField[y][x].getCountMineNeighbors()) {
                    case 0 :
                        setCellValueEx(x, y, Color.DARKGRAY, "");
                        ArrayList<GameObject> neighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
                        for (GameObject neighbor : neighbors) {
                            if (!neighbor.isOpen()) openTile(neighbor.getX(), neighbor.getY());
                        }
                        break;
                    case 1 :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.BLUE);
                        break;
                    case 2 :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.GREEN);
                        break;
                    default :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.RED);
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
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        setScore(score = 0);
        createGame();
    }

    private void markTile(int x, int y) {
        if (!gameField[y][x].isOpen()) {
            if (!gameField[y][x].isFlag() && countFlags > 0) {
                countFlags--;
                gameField[y][x].setFlag(true);
                setCellValueEx(x, y, Color.YELLOW, FLAG);
            } else if (gameField[y][x].isFlag()) {
                countFlags++;
                gameField[y][x].setFlag(false);
                setCellValueEx(x, y, Color.GRAY, "");
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
