package com.happy.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.happy.mall.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生信息 Mapper
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
}
