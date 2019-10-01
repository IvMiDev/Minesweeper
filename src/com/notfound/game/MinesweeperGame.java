package com.notfound.game;

import javax.swing.*;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private GameObject[][] gameField;
    private ArrayList<GameObject> neighbors = new ArrayList<>(8);
    private boolean isGameStopped, isNewGame;
    private int side, score, x, y;
    private int countMinesOnField, countFlags, countClosedTiles;

    @Override
    public void initialize() {
        setNewGame(true);
        showFrame("New game!");
        synchronized (Thread.currentThread()) {
            try {
                if (isNewGame()) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException ignored) {
            }
        }
        gameField = new GameObject[getSide()][getSide()];
        setScreenSize(getSide(), getSide());
        createGame();
    }

    private void showFrame(String title) {
        ImageIcon icon = new ImageIcon("src/com/notfound/game/resources/bomb.png");
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        JButton b1 = new JButton();
        JButton b2 = new JButton();
        JButton b3 = new JButton();
        panel.add(label);
        if (isNewGame()) {
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
            if (isNewGame()) {
                setSide(10);
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
            if (isNewGame()) {
                setSide(15);
                frame.setVisible(false);
                synchronized (t) {
                    t.notify();
                }
            } else System.exit(0);
        });
        b3.addActionListener(event -> {
            setSide(20);
            frame.setVisible(false);
            synchronized (t) {
                t.notify();
            }
        });
    }

    private void createGame() {
        setNewGame(true);
        setGameStopped(false);
        setCountMinesOnField(0);
        setCountFlags(0);
        setCountClosedTiles(getSide() * getSide());
        setScore(0);
        setScoreValue(getScore());
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                gameField[y][x] = new GameObject(x, y);
                setCellValueEx(x, y, Color.GRAY, "", Color.BLACK);
            }
        }
    }

    private void setMines(int x, int y) {
        for (int i = 0; i < gameField.length; i++) {
            for (GameObject[] gameObject : gameField) {
                if (!(gameObject[i].getX() == x && gameObject[i].getY() == y)) {
                    switch (getSide()) {
                        case 10:
                            if (getRandomNumber(10) == 1) {
                                gameObject[i].setMine();
                                setCountMinesOnField(getCountMinesOnField() + 1);
                                setCountFlags(getCountFlags() + 1);
                            }
                            break;
                        case 15:
                            if (getRandomNumber(7) == 1) {
                                gameObject[i].setMine();
                                setCountMinesOnField(getCountMinesOnField() + 1);
                                setCountFlags(getCountFlags() + 1);
                            }
                            break;
                        case 20:
                            if (getRandomNumber(4) == 1) {
                                gameObject[i].setMine();
                                setCountMinesOnField(getCountMinesOnField() + 1);
                                setCountFlags(getCountFlags() + 1);
                            }
                    }
                }
            }
        }
        countMineNeighbors();
    }

    private void getNeighborsTest(GameObject gameObject) {
        neighbors.clear();
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
                    getNeighborsTest(gameObject[x]);
                    for (GameObject neighbor : neighbors) {
                        if (neighbor.isMine()) {
                            gameObject[x].setCountMineNeighbors(gameObject[x].getCountMineNeighbors() + 1);
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (isNewGame()) {
            setNewGame(false);
            setMines(x, y);
        }
        gameField[y][x].setOpen();
        if (!gameField[y][x].isMine()) {
            setScore(getScore() + 5);
            setScoreValue(getScore());
            setCountClosedTiles(getCountClosedTiles() - 1);
            if (getCountClosedTiles() == getCountMinesOnField()) {
                setGameStopped(true);
                showFrame("Congratulations! You won!!!");
            }
            switch (gameField[y][x].getCountMineNeighbors()) {
                case 0:
                    setCellValueEx(x, y, Color.DARKGRAY, "");
                    ArrayList<GameObject> neighbors = new ArrayList<>(getNeighbors(gameField[y][x]));
                    for (GameObject neighbor : neighbors) {
                        if (neighbor.isFlag()) {
                            neighbor.setFlag(false);
                            setCountFlags(getCountFlags() + 1);
                        }
                        if (!neighbor.isOpen()) openTile(neighbor.getX(), neighbor.getY());
                    }
                    break;
                case 1:
                    setCellValueEx(x, y, Color.DARKGRAY, gameField[y][x].getCountMineNeighbors(), Color.BLUE);
                    break;
                case 2:
                    setCellValueEx(x, y, Color.DARKGRAY, gameField[y][x].getCountMineNeighbors(), Color.GREEN);
                    break;
                case 3:
                    setCellValueEx(x, y, Color.DARKGRAY, gameField[y][x].getCountMineNeighbors(), Color.RED);
                    break;
                case 4:
                    setCellValueEx(x, y, Color.DARKGRAY, gameField[y][x].getCountMineNeighbors(), Color.DARKBLUE);
            }
        } else {
            setCellValueEx(x, y, Color.RED, MINE);
            setGameStopped(true);
            showFrame("Game Over!");
        }
    }

    private void markTile(int x, int y) {
        if (!gameField[y][x].isFlag() && getCountFlags() > 0) {
            setCountFlags(getCountFlags() - 1);
            gameField[y][x].setFlag(true);
            setCellValueEx(x, y, Color.YELLOW, FLAG);
        } else if (gameField[y][x].isFlag()) {
            setCountFlags(getCountFlags() + 1);
            gameField[y][x].setFlag(false);
            setCellValueEx(x, y, Color.GRAY, "");
        }
    }

    private boolean isInGame(int x, int y) {
        return (x >= 0 && x <= getSide() - 1) && (y >= 0 && y <= getSide() - 1);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        if (isInGame(x, y)) {
            if (!isGameStopped()) {
                if (!gameField[y][x].isOpen()) {
                    markTile(x, y);
                }
            }
        }
    }

    @Override
    public void onMousePressed(int x, int y) {
        if (isInGame(x, y)) {
            if (!isGameStopped()) {
                if (!gameField[y][x].isFlag()) {
                    setX(x);
                    setY(y);
                    if (!gameField[y][x].isOpen()) {
                        setCellColor(x, y, Color.DARKGRAY);
                    } else {
                        getNeighborsTest(gameField[y][x]);
                        repaint(neighbors, Color.DARKGRAY);
                    }
                }
            }
        }
    }

    @Override
    public void onMouseReleased(int x, int y) {
        if (!isInGame(x, y)) {
            repaintAll();
        } else {
            if (!isGameStopped()) {
                if (!gameField[y][x].isFlag()) {
                    if (gameField[y][x].isOpen()) {
                        getNeighborsTest(gameField[y][x]);
                        repaint(neighbors, Color.GRAY);
                    } else {
                        openTile(x, y);
                    }
                }
            }
        }
    }

    @Override
    public void onMouseDragged(int x, int y) {
        if (!isGameStopped() && isInGame(x, y)) {
            if (getX() != x || getY() != y) {
                if (!gameField[getY()][getX()].isFlag()) {
                    if (gameField[getY()][getX()].isOpen()) {
                        getNeighborsTest(gameField[getY()][getX()]);
                        repaint(neighbors, Color.GRAY);
                    } else setCellColor(getX(), getY(), Color.GRAY);
                }
                if (!gameField[y][x].isFlag()) {
                    if (gameField[y][x].isOpen()) {
                        getNeighborsTest(gameField[y][x]);
                        repaint(neighbors, Color.DARKGRAY);
                    } else setCellColor(x, y, Color.DARKGRAY);
                }
                setX(x);
                setY(y);
            }
        }
    }

    @Override
    public void repaint(ArrayList<GameObject> neighbors, Color color) {
        this.neighbors = neighbors;
        if (color.equals(Color.GRAY)) {
            for (GameObject neighbor : neighbors) {
                if (!neighbor.isOpen() && !neighbor.isFlag()) {
                    setCellColor(neighbor.getX(), neighbor.getY(), color);
                }
            }
        } else if (color.equals(Color.DARKGRAY)) {
            for (GameObject neighbor : neighbors) {
                if (!neighbor.isOpen() && !neighbor.isFlag()) {
                    setCellColor(neighbor.getX(), neighbor.getY(), color);
                }
            }
        }
    }

    private void repaintAll() {
        for (int x = 0; x < gameField.length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                if (!gameField[y][x].isFlag()) {
                    if (gameField[y][x].isOpen()) {
                        setCellColor(x, y, Color.DARKGRAY);
                    } else setCellColor(x, y, Color.GRAY);
                }
            }
        }
    }

    private boolean isGameStopped() {
        return isGameStopped;
    }

    private void setGameStopped(boolean gameStopped) {
        isGameStopped = gameStopped;
    }

    private boolean isNewGame() {
        return isNewGame;
    }

    private void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    private int getSide() {
        return this.side;
    }

    private void setSide(int side) {
        this.side = side;
    }

    private int getScore() {
        return score;
    }

    private void setScore(int score) {
        this.score = score;
    }

    private int getCountMinesOnField() {
        return countMinesOnField;
    }

    private void setCountMinesOnField(int countMinesOnField) {
        this.countMinesOnField = countMinesOnField;
    }

    private int getCountFlags() {
        return countFlags;
    }

    private void setCountFlags(int countFlags) {
        this.countFlags = countFlags;
    }

    private int getCountClosedTiles() {
        return countClosedTiles;
    }

    private void setCountClosedTiles(int countClosedTiles) {
        this.countClosedTiles = countClosedTiles;
    }

    private int getX() {
        return this.x;
    }

    private void setX(int x) {
        this.x = x;
    }

    private int getY() {
        return this.y;
    }

    private void setY(int y) {
        this.y = y;
    }
}
