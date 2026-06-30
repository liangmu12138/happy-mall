package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.StudyMaterial;

/**
 * 学习资料 Service
 */
public interface StudyMaterialService extends IService<StudyMaterial> {

    /**
     * 分页查询资料
     */
    Page<StudyMaterial> getMaterialPage(Integer pageNum, Integer pageSize, String category, String keyword);

    /**
     * 获取资料详情
     */
    StudyMaterial getMaterialDetail(Long id);

    /**
     * 上传资料
     */
    void uploadMaterial(StudyMaterial material);

    /**
     * 更新资料
     */
    void updateMaterial(StudyMaterial material);

    /**
     * 删除资料
     */
    void deleteMaterial(Long id, Long userId);

    /**
     * 点赞资料
     */
    void likeMaterial(Long id);

    /**
     * 下载资料（增加下载次数）
     */
    void downloadMaterial(Long id);
}
