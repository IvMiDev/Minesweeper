package com.notfound.game;

import javafx.scene.paint.Color;

public interface GameScreen {

    void setScreenSize(int x, int y);

    void setCellColor(int x, int y, Color color);

    Color getCellColor(int x, int y);

    void setCellTextSize(int x, int y, int var3);

    int getCellTextSize(int var1, int var2);

    void setCellValue(int var1, int var2, String var3);

    String getCellValue(int var1, int var2);

    void setCellNumber(int var1, int var2, int var3);

    int getCellNumber(int var1, int var2);

    void setCellTextColor(int var1, int var2, Color var3);

    Color getCellTextColor(int var1, int var2);

    void setCellValueEx(int var1, int var2, Color var3, String var4);

    void setCellValueEx(int var1, int var2, Color var3, String var4, Color var5);

    void setCellValueEx(int var1, int var2, Color var3, String var4, Color var5, int var6);

    void setScore(int var);

    int getRandomNumber(int num);

    int getRandomNumber(int num1, int num2);

    void initialize();

    void onMouseRightClick(int x, int y);

    void onMousePressed(int x, int y);

    void onMouseReleased(int x, int y);
}

