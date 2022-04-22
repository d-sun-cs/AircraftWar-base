package edu.hitsz.application;

import edu.hitsz.dao.UserDao;
import edu.hitsz.dao.impl.UserDaoImpl;
import edu.hitsz.dao.pojo.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * @author SunDocker
 */
public class Input {
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private JPanel topPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JButton confirm;
    private JButton cancel;
    private JTextArea description;
    private JTextField input;

    private JFrame self;
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    private final UserDao userDao = new UserDaoImpl();

    public void setSelf(JFrame self) {
        this.self = self;
    }

    public Input(int score, int diffNum) {
        description.setText("游戏结束，你的得分为" + score + "，\n请输入名字记录得分：");
        confirm.addActionListener(e -> {
            //待转移
            User user = new User();
            user.setCreateTime(System.currentTimeMillis());
            user.setScore(score);
            user.setName(input.getText());
            user.setDifficulty(diffNum);
            userDao.saveUser(user);
            self.dispose();
            board.refresh();
        });
        cancel.addActionListener(e -> self.dispose());
    }


    /*public static void main(String[] args) {
        JFrame frame = new JFrame("确认");
        frame.setLocationRelativeTo(null);
        Input input = new Input(10, 0);
        input.setSelf(frame);
        frame.setContentPane(input.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }*/
}
