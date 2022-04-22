package edu.hitsz.dao;

import edu.hitsz.dao.pojo.User;

import java.util.List;

/**
 * @author SunDocker
 */
public interface UserDao {
    /**
     * 按得分从高到低查询用户（模拟order by关键字）
     * @return 按得分排好序的用户集合
     */
    List<User> selectUsersOrderByScoreDesc(int diffNum);

    /**
     * 保存用户
     * @param user
     * @return 暂时返回void，之后可能修改成boolean
     */
    void saveUser(User user);

    /**
     * 通过创建时间删除用户
     * @param createTime
     */
    void deleteUser(Long createTime);
}
