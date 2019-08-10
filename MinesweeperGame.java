package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import javax.swing.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private ImageIcon icon = new ImageIcon("C:\\Java\\bomb3.png");
    private GameObject[][] gameField;
    private boolean isGameStopped, isNewGame;
    private int side, countMinesOnField, countFlags, score, countClosedTiles;

    @Override
    public void initialize() {
        isNewGame = true;
        showFrame("New game!");
        synchronized (Thread.currentThread()) {
            try {
                if (side == 0) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException ignored) {
            }
        }
        gameField = new GameObject[side][side];
        setScreenSize(side, side);
        createGame();
    }

    private void createGame() {
        isGameStopped = isNewGame = false;
        countMinesOnField = countFlags = 0;
        countClosedTiles = side * side;
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
            for (GameObject[] gameObject : gameField) {
                if (!(gameObject[i].getX() == x && gameObject[i].getY() == y)) {
                    switch (side) {
                        case 10:
                            if (getRandomNumber(10) == 1) {
                                gameObject[i].setMine();
                                countMinesOnField++;
                                countFlags++;
                            }
                            break;
                        case 15:
                            if (getRandomNumber(7) == 1) {
                                gameObject[i].setMine();
                                countMinesOnField++;
                                countFlags++;
                            }
                            break;
                        case 20:
                            if (getRandomNumber(4) == 1) {
                                gameObject[i].setMine();
                                countMinesOnField++;
                                countFlags++;
                            }
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
            for (GameObject[] gameObject : gameField) {
                if (!gameObject[x].isMine()) {
                    ArrayList<GameObject> mineNeighbors = new ArrayList<>(getNeighbors(gameObject[x]));
                    for (GameObject neighbor : mineNeighbors) {
                        if (neighbor.isMine()) {
                            int countMineNeighbor = gameObject[x].getCountMineNeighbors();
                            gameObject[x].setCountMineNeighbors(++countMineNeighbor);
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
                if (--countClosedTiles == countMinesOnField) {
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

    private void showFrame(String title) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton b1 = new JButton();
        JButton b2 = new JButton();
        JButton b3 = new JButton();
        panel.add(label);
        if (isNewGame) {
            frame.setTitle(title);
            label.setText("Choose difficulty level: ");
            b1.setText("Easy");
            b2.setText("Normal");
            b3.setText("Hard");
            panel.add(b1);
            panel.add(b2);
            panel.add(b3);
        } else {
            frame.setTitle(title);
            label.setText("Would you like to replay? ");
            b1.setText("Yes");
            b2.setText("No");
            panel.add(b1);
            panel.add(b2);
        }
        frame.add(panel);
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 80);
        frame.setLocationRelativeTo(null);
        b1.setFocusable(false);
        b2.setFocusable(false);
        b3.setFocusable(false);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        Thread t = Thread.currentThread();
        b1.addActionListener(event -> {
            if (isNewGame) {
                side = 10;
                frame.setVisible(false);
                synchronized (t) {
                    t.notify();
                }
            } else {
                frame.setVisible(false);
                createGame();
            }
        });
        b2.addActionListener(event -> {
            if (isNewGame) {
                side = 15;
                frame.setVisible(false);
                synchronized (t) {
                    t.notify();
                }
            } else System.exit(0);
        });
        b3.addActionListener(event -> {
            side = 20;
            frame.setVisible(false);
            synchronized (t) {
                t.notify();
            }
        });
    }
}
