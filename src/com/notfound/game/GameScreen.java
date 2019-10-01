package com.notfound.game;

import javafx.scene.paint.Color;
import java.util.ArrayList;

public interface GameScreen {

    void setScreenSize(int x, int y);

    void setCellColor(int x, int y, Color color);

    Color getCellColor(int x, int y);

    void setCellTextSize(int x, int y, int size);

    int getCellTextSize(int x, int y);

    void setCellValue(int x, int y, String value);

    String getCellValue(int x, int y);

    void setCellNumber(int x, int y, int num);

    int getCellNumber(int x, int y);

    void setCellTextColor(int x, int y, Color color);

    Color getCellTextColor(int x, int y);

    void setCellValueEx(int x, int y, Color color, int value);

    void setCellValueEx(int x, int y, Color color, int value, Color col);

    void setCellValueEx(int x, int y, Color color, String value);

    void setCellValueEx(int x, int y, Color color, String value, Color col);

    void setCellValueEx(int x, int y, Color color, String value, Color col, int val);

    void setScoreValue(int value);

    int getRandomNumber(int num);

    int getRandomNumber(int num1, int num2);

    void initialize();

    void onMouseRightClick(int x, int y);

    void onMousePressed(int x, int y);

    void onMouseReleased(int x, int y);

    void onMouseDragged(int x, int y);

    void repaintNeighbors(ArrayList<GameObject> list, Color color);
}

