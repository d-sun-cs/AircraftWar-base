package edu.hitsz.dao;

import edu.hitsz.dao.pojo.User;

import java.util.List;

/**
 * @author SunDocker
 */
public interface UserDao {
    /**
     * 按得分从高到低查询用户
     * @return
     */
    List<User> selectUsersOrderByScoreDesc();

    /**
     * 保存用户
     * @param user
     * @return
     */
    void saveUser(User user);
}
