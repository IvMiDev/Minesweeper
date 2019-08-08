package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import javax.swing.*;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField;
    private boolean isGameStopped, isNewGame;
    private int side, countMinesOnField, countFlags, score, countClosedTiles;

    @Override
    public void initialize() {
        side = 9;
        gameField = new GameObject[side][side];
        countClosedTiles = side * side;
        score = 0;
        setScreenSize(side, side);
        createGame();
    }

    private void createGame() {
        isGameStopped = false;
        //isNewGame = false;
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
                    case 3 :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.RED);
                        break;
                    case 4 :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.MIDNIGHTBLUE);
                        break;
                    default :
                        setCellValueEx(x, y, Color.DARKGRAY, String.valueOf(gameField[y][x].getCountMineNeighbors()));
                        setCellTextColor(x, y, Color.DARKRED);
                }
            } else {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
        }
    }

    private void win() {
        isGameStopped = true;
        //showMessageDialog(Color.BLACK, "Congratulations! You won!!!", Color.GREEN, 45);
        newGameOrRestart("Congratulations! You won!!!"); //test!!!
    }

    private void gameOver() {
        isGameStopped = true;
        //showMessageDialog(Color.BLACK, "Game Over!", Color.RED, 90);
        newGameOrRestart("Game Over!"); //test!!!
    }

    private void restart() {
        countClosedTiles = side * side;
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

    private void choiceDif() {
        JFrame frame = new JFrame("New game!");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Choice your difficulty");
        JButton easy = new JButton("Easy");
        JButton normal = new JButton("Normal");
        JButton hard = new JButton("Hard");
        panel.add(label);
        panel.add(easy);
        panel.add(normal);
        panel.add(hard);
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        easy.addActionListener(e -> {
            side = 9;
            if (isNewGame) {
                createGame();
            }
            else restart();
            frame.setVisible(false);
        });
        normal.addActionListener(e -> {
            side = 18;
            if (isNewGame) {
                createGame();
            }
            else restart();
            frame.setVisible(false);
        });
        hard.addActionListener(e -> {
            side = 36;
            if (isNewGame) createGame();
            else restart();
            frame.setVisible(false);
        });
    }

    private void newGameOrRestart(String title) {
        JFrame frame = new JFrame(title);
        JPanel panel = new JPanel();
        JLabel label = new JLabel(title + "Would you like to restart?");
        JButton yes = new JButton("Yes");
        JButton no = new JButton("No");
        panel.add(label);
        panel.add(yes);
        panel.add(no);
        frame.add(panel);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        yes.addActionListener(e -> {
            choiceDif();
            frame.setVisible(false);
        });
        no.addActionListener(e -> {
            frame.setVisible(false);
            System.exit(0);
        });
    }
}
