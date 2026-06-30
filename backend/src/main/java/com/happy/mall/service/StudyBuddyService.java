package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.StudyBuddy;

import java.util.Map;

/**
 * 学习搭子 Service
 */
public interface StudyBuddyService extends IService<StudyBuddy> {

    /**
     * 分页查询学习搭子
     */
    Page<Map<String, Object>> getBuddyPage(Integer pageNum, Integer pageSize, String buddyType, String keyword);

    /**
     * 获取搭子详情
     */
    Map<String, Object> getBuddyDetail(Long id);

    /**
     * 发布搭子
     */
    void createBuddy(StudyBuddy buddy);

    /**
     * 更新搭子
     */
    void updateBuddy(StudyBuddy buddy);

    /**
     * 删除搭子
     */
    void deleteBuddy(Long id, Long userId);

    /**
     * 加入搭子
     */
    void joinBuddy(Long buddyId, Long userId);

    /**
     * 退出搭子
     */
    void leaveBuddy(Long buddyId, Long userId);
}
