package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.StudyMaterial;
import com.happy.mall.mapper.StudyMaterialMapper;
import com.happy.mall.service.StudyMaterialService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 学习资料 Service 实现类
 */
@Service
public class StudyMaterialServiceImpl extends ServiceImpl<StudyMaterialMapper, StudyMaterial> implements StudyMaterialService {

    @Override
    public Page<StudyMaterial> getMaterialPage(Integer pageNum, Integer pageSize, String category, String keyword) {
        LambdaQueryWrapper<StudyMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyMaterial::getStatus, 1);

        if (StringUtils.hasText(category)) {
            wrapper.eq(StudyMaterial::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(StudyMaterial::getTitle, keyword)
                    .or().like(StudyMaterial::getDescription, keyword)
                    .or().like(StudyMaterial::getSubject, keyword));
        }

        wrapper.orderByDesc(StudyMaterial::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public StudyMaterial getMaterialDetail(Long id) {
        StudyMaterial material = this.getById(id);
        if (material == null || material.getStatus() != 1) {
            throw new BusinessException("资料不存在或已下架");
        }
        return material;
    }

    @Override
    public void uploadMaterial(StudyMaterial material) {
        if (material.getDownloadCount() == null) material.setDownloadCount(0);
        if (material.getLikeCount() == null) material.setLikeCount(0);
        if (material.getRating() == null) material.setRating(new java.math.BigDecimal("0"));
        if (material.getStatus() == null) material.setStatus(1);
        this.save(material);
    }

    @Override
    public void updateMaterial(StudyMaterial material) {
        if (material.getId() == null) {
            throw new BusinessException("资料ID不能为空");
        }
        this.updateById(material);
    }

    @Override
    public void deleteMaterial(Long id, Long userId) {
        StudyMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("资料不存在");
        }
        if (!material.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的资料");
        }
        this.removeById(id);
    }

    @Override
    public void likeMaterial(Long id) {
        StudyMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("资料不存在");
        }
        material.setLikeCount(material.getLikeCount() + 1);
        this.updateById(material);
    }

    @Override
    public void downloadMaterial(Long id) {
        StudyMaterial material = this.getById(id);
        if (material == null) {
            throw new BusinessException("资料不存在");
        }
        material.setDownloadCount(material.getDownloadCount() + 1);
        this.updateById(material);
    }
}
