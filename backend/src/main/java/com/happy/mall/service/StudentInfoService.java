package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.StudentInfo;

/**
 * 学生信息服务
 */
public interface StudentInfoService extends IService<StudentInfo> {

    /**
     * 保存学生信息
     */
    void saveStudentInfo(Long userId, String studentCardId);

    /**
     * 获取学生信息
     */
    StudentInfo getStudentInfoByUserId(Long userId);

    /**
     * 检查学生卡ID是否已存在
     */
    boolean checkStudentCardIdExists(String studentCardId);
}
