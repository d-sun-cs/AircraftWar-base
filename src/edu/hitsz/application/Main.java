package edu.hitsz.application;

import edu.hitsz.application.game.DifficultGame;
import edu.hitsz.application.game.EasyGame;
import edu.hitsz.application.game.Game;
import edu.hitsz.application.game.SimpleGame;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static Game game;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Menu m = new Menu();
        JPanel menu = m.getMainPanel();

        //new Thread(() -> {
                //展示界面...
                //在某个点击事件中进行下面两个操作
                /*Status.menuOver = true;
                Status.class.notifyAll();*/
            frame.add(menu);
            frame.setVisible(true);
        //}).start();
        new Thread(()->{
            synchronized (Status.class) {
                while(!Status.menuOver) {
                    try {
                        Status.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (Game.getDifficulty() == 0) {
                game = new EasyGame();
            } else if (Game.getDifficulty() == 1) {
                game = new SimpleGame();
            } else if (Game.getDifficulty() == 2) {
                game = new DifficultGame();
            } else {
                throw new RuntimeException("难度选择异常");
            }
            frame.remove(menu);
            frame.add(game);
            frame.setVisible(true);
            game.setFocusable(true);
            game.requestFocus();
            game.action();
        }).start();

        new Thread(()->{
            synchronized (Status.class) {
                while (!Status.gameOver) {
                    try {
                        Status.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //展示排行榜
            Board board = new Board(Game.getDifficulty());
            JPanel boardPanel = board.getMainPanel();
            frame.remove(game);
            frame.add(boardPanel);
            frame.setVisible(true);
            //展示确认窗口
            Input input = new Input(Game.getScore(), Game.getDifficulty());
            JFrame inputFrame = new JFrame("记录分数");
            input.setSelf(inputFrame);
            input.setBoard(board);
            inputFrame.setLocationRelativeTo(null);
            inputFrame.setContentPane(input.getMainPanel());
            inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            inputFrame.setUndecorated(true);
            inputFrame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
            inputFrame.pack();
            inputFrame.setResizable(false);
            inputFrame.setVisible(true);
        }).start();

    }
}


