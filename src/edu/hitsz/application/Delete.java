package edu.hitsz.application;

import edu.hitsz.dao.UserDao;
import edu.hitsz.dao.impl.UserDaoImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author SunDocker
 */
public class Delete {
    private JPanel mainPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private JPanel topPanel;
    private JPanel bottomPanel;
    private JButton confirm;
    private JButton cancel;
    private JTextField description;

    private JFrame self;
    private final UserDao userDao = new UserDaoImpl();
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setSelf(JFrame self) {
        this.self = self;
    }

    public Delete(Long createTime) {
        confirm.addActionListener(e -> {
            userDao.deleteUser(createTime);
            board.refresh();
            self.dispose();
        });
        cancel.addActionListener(e -> self.dispose());
        description.setText("是否确认删除该条选中记录？\n（注：选中多条时只会删除最上面的那条）");
    }
}
