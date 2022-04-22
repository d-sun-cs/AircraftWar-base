package edu.hitsz.application;

import edu.hitsz.dao.UserDao;
import edu.hitsz.dao.impl.UserDaoImpl;
import edu.hitsz.dao.pojo.User;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

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

        Game game = new Game();
        Menu m = new Menu();
        m.setGame(game);
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
            frame.remove(menu);
            frame.add(game);
            frame.setVisible(true);
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
            /*UserDao userDao = new UserDaoImpl();
            List<User> userList = userDao.selectUsersOrderByScoreDesc();
            int userNum = userList.size();
            String[] columnName = {"名次", "玩家名", "得分", "记录时间"};
            String[][] tableData = new String[userNum][4];
            for (int i = 0; i < userNum; i++) {
                User usr = userList.get(i);
                tableData[i][0] = (i + 1) + "";
                tableData[i][1] = usr.getName();
                tableData[i][2] = usr.getScore() + "";
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                String timeStr = sdf.format(new Date(usr.getCreateTime()));
                tableData[i][3] = timeStr;
            }*/
            Board board = new Board(game.getDifficulty());
            JPanel boardPanel = board.getMainPanel();
            frame.remove(game);
            frame.add(boardPanel);
            frame.setVisible(true);
            //展示确认窗口
            Input input = new Input(game.getScore(), game.getDifficulty());
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

class Status {
    public static boolean menuOver = false;
    public static boolean gameOver = false;
}
