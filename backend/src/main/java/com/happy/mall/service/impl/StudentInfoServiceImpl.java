package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.StudentInfo;
import com.happy.mall.mapper.StudentInfoMapper;
import com.happy.mall.service.StudentInfoService;
import org.springframework.stereotype.Service;

/**
 * 学生信息服务实现类
 */
@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    @Override
    public void saveStudentInfo(Long userId, String studentCardId) {
        // 检查学生卡ID是否已存在
        if (checkStudentCardIdExists(studentCardId)) {
            throw new BusinessException("该学生卡ID已被注册");
        }

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setUserId(userId);
        studentInfo.setStudentCardId(studentCardId);
        this.save(studentInfo);
    }

    @Override
    public StudentInfo getStudentInfoByUserId(Long userId) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getUserId, userId);
        return this.getOne(wrapper);
    }

    @Override
    public boolean checkStudentCardIdExists(String studentCardId) {
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentCardId, studentCardId);
        return this.count(wrapper) > 0;
    }
}
