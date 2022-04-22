package edu.hitsz.application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JComboBox music;
    private JButton simple;
    private JButton difficult;
    private JTextField musicText;
    private JPanel topPanel;
    private JPanel bottomPanel;

    private Game game;

    public void setGame(Game game) {
        this.game = game;
    }

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
            game.setDifficulty(0);
            Status.menuOver = true;
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
        simple.addActionListener(e -> {
            System.out.println("选择了普通模式");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg3.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            game.setDifficulty(1);
            Status.menuOver = true;
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
        difficult.addActionListener(e -> {
            System.out.println("选择了困难模式");
            try {
                ImageManager.BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg5.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            game.setDifficulty(2);
            Status.menuOver = true;
            synchronized (Status.class) {
                Status.class.notifyAll();
            }
        });
    }
}
