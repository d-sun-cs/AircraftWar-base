package edu.hitsz.dao.pojo;

import java.io.Serializable;

/**
 * @author SunDocker
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Integer score;
    /**
     * 存储 1970.1.1 0:0:0 毫秒
     */
    private Long createTime;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", createTime=" + createTime +
                ", difficulty=" + difficulty +
                '}';
    }

    private Integer difficulty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
}
