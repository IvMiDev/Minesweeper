package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import javax.swing.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game implements Runnable {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField;
    private boolean isGameStopped, isNewGame, isFirstLaunch;
    private int side, countMinesOnField, countFlags, score, countClosedTiles;

    @Override
    public void run() {
        showFrame("New game!");
    }

    @Override
    public void initialize() {
        isFirstLaunch = true;
        isNewGame = true;
        run();
        synchronized (Thread.currentThread()) {
            try {
                if (side == 0) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void createGame() {
        isGameStopped = isNewGame = false;
        countMinesOnField = 0;
        countFlags = 0;
        gameField = new GameObject[side][side];
        countClosedTiles = side * side;
        if (isFirstLaunch) setScreenSize(side, side);
        setScore(score = 0);
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                gameField[y][x] = new GameObject(x, y);
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
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                if (!gameField[y][x].isMine()) {
                    ArrayList<GameObject> mineNeighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
                    for (GameObject neighbor : mineNeighbors) {
                        if (neighbor.isMine()) {
                            int countMineNeighbor = gameField[y][x].getCountMineNeighbors();
                            gameField[y][x].setCountMineNeighbors(++countMineNeighbor);
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (countClosedTiles == side * side) {
            setMines(x, y);
        }
        if (!gameField[y][x].isOpen() && !gameField[y][x].isFlag()) {
            gameField[y][x].setOpen();
            if (!gameField[y][x].isMine()) {
                setScore(score += 5);
                countClosedTiles--;
                if (countClosedTiles == countMinesOnField) {
                    isFirstLaunch = false;
                    isGameStopped = true;
                    showFrame("Congratulations! You won!!!");
                }
                switch (gameField[y][x].getCountMineNeighbors()) {
                    case 0:
                        setCellValueEx(x, y, Color.DARKGRAY, "");
                        ArrayList<GameObject> neighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
                        for (GameObject neighbor : neighbors) {
                            if (!neighbor.isOpen()) openTile(neighbor.getX(), neighbor.getY());
                        }
                        break;
                    case 1:
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.BLUE);
                        break;
                    case 2:
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.GREEN);
                        break;
                    case 3:
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.RED);
                        break;
                    case 4:
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.MIDNIGHTBLUE);
                        break;
                    default:
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.DARKRED);
                }
            } else {
                setCellValueEx(x, y, Color.RED, MINE);
                isFirstLaunch = false;
                isGameStopped = true;
                showFrame("Game Over!");
            }
        }
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
        if (!isGameStopped) {
            markTile(x, y);
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (!isGameStopped) {
            openTile(x, y);
        }
    }

    /*
    All code below for test!!!
    */

    private void showFrame(String title) {
        String difficulty = "Choose difficulty level: ";
        String restart = "Would you like to replay? ";
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton b1 = new JButton();
        JButton b2 = new JButton();
        JButton b3 = new JButton();
        panel.add(label);
        if (isNewGame) {
            frame.setTitle(title);
            label.setText(difficulty);
            b1.setText("Easy");
            b2.setText("Normal");
            b3.setText("Hard");
            panel.add(b1);
            panel.add(b2);
            panel.add(b3);
        } else {
            frame.setTitle(title);
            label.setText(restart);
            b1.setText("Yes");
            b2.setText("No");
            panel.add(b1);
            panel.add(b2);
        }
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        Thread t = Thread.currentThread();
        b1.addActionListener(e -> {
            if (isNewGame) {
                side = 10;
                frame.setVisible(false);
                createGame();
                if (isFirstLaunch) {
                    synchronized (t) {
                        t.notify();
                    }
                }
            } else {
                isNewGame = true;
                frame.setVisible(false);
                showFrame("New game!");
            }
        });
        b2.addActionListener(e -> {
            if (isNewGame) {
                side = 15;
                frame.setVisible(false);
                createGame();
                if (isFirstLaunch) {
                    synchronized (t) {
                        t.notify();
                    }
                }
            } else System.exit(0);
        });
        b3.addActionListener(e -> {
            side = 20;
            frame.setVisible(false);
            createGame();
            if (isFirstLaunch) {
                synchronized (t) {
                    t.notify();
                }
            }
        });
    }
}
