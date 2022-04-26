package edu.hitsz.application;

import edu.hitsz.application.game.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author SunDocker
 */
public class Menu {
    private JButton easy;
    private JPanel mainPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JComboBox<String> music;
    private JButton simple;
    private JButton difficult;
    private JTextField musicText;
    private JPanel topPanel;
    private JPanel bottomPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu");
        frame.setContentPane(new Menu().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Menu() {
        easy.addActionListener(e -> {
            System.out.println("选择了简单模式");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Game.setDifficulty(0);
            Status.menuOver = true;
            Game.setNeedMusic(music.getSelectedIndex() == 0);
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
        simple.addActionListener(e -> {
            System.out.println("选择了普通模式");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg2.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Game.setDifficulty(1);
            Status.menuOver = true;
            Game.setNeedMusic(music.getSelectedIndex() == 0);
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
        difficult.addActionListener(e -> {
            System.out.println("选择了困难模式");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg4.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Game.setDifficulty(2);
            Status.menuOver = true;
            Game.setNeedMusic(music.getSelectedIndex() == 0);
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
    }
}
