package edu.hitsz.application;

import javax.swing.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");

        JFrame frame = new JFrame("Aircraft War");
        Game game = new Game();
        frame.add(game);
        frame.setSize(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        // 固定窗口尺寸
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.action();
    }
}
