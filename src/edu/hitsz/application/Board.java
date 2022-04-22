package edu.hitsz.application;

import edu.hitsz.dao.UserDao;
import edu.hitsz.dao.impl.UserDaoImpl;
import edu.hitsz.dao.pojo.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author SunDocker
 */
public class Board {
    private JPanel mainPanel;
    private JPanel midPanel;
    private JPanel bottomPanel;
    private JTable rankTable;
    private JButton deleteButton;
    private JScrollPane tableScrollPanel;
    private JPanel topPanel;
    private JTextField rankName;
    private JTextField difficulty;

    private final UserDao userDao = new UserDaoImpl();
    private List<User> userList;
    private Integer diffNum;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public Board(Integer diffNum) {
        /*String[] columnName = {"名次", "玩家名", "得分", "记录时间"};
        String[][] tableData = {
                {"test1.1", "test1.2", "test1.3", "test1.4"},
                {"test2.1", "test2.2", "test2.3", "test2.4"},
                {"test3.1", "test3.2", "test3.3", "test3.4"},
        };*/
        this.diffNum = diffNum;
        userList = userDao.selectUsersOrderByScoreDesc(diffNum);
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
        }

        DefaultTableModel model = new DefaultTableModel(tableData, columnName) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankTable.setModel(model);
        tableScrollPanel.setViewportView(rankTable);
        deleteButton.addActionListener(e -> {
            int row = rankTable.getSelectedRow();
            System.out.println(row);
            if (row != -1) {
                Long createTime = userList.get(row).getCreateTime();
                //弹窗
                Delete delete = new Delete(createTime);
                JFrame deleteFrame = new JFrame("确认");
                delete.setSelf(deleteFrame);
                delete.setBoard(this);
                deleteFrame.setLocationRelativeTo(null);
                deleteFrame.setContentPane(delete.getMainPanel());
                deleteFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                deleteFrame.setUndecorated(true);
                deleteFrame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                deleteFrame.pack();
                deleteFrame.setResizable(false);
                deleteFrame.setVisible(true);
            }
        });
        String diffStr = "难度：";
        if (diffNum == 0) {
            diffStr += "简单";
        } else if (diffNum == 1) {
            diffStr += "普通";
        } else if (diffNum == 2) {
            diffStr += "困难";
        }
        difficulty.setText(diffStr);
    }

    public void refresh() {
        userList = userDao.selectUsersOrderByScoreDesc(diffNum);
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
        }
        DefaultTableModel model = new DefaultTableModel(tableData, columnName) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rankTable.setModel(model);
        tableScrollPanel.setViewportView(rankTable);
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("Board");
        frame.setContentPane(new Board().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }*/
}
