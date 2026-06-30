package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.Category;
import com.happy.mall.mapper.CategoryMapper;
import com.happy.mall.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类 Service 实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryTree() {
        List<Category> allCategories = this.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));

        // 构建树形结构
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        return rootCategories;
    }

    @Override
    public List<Category> getAllCategories() {
        return this.list(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort));
    }

    @Override
    public void addCategory(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        this.save(category);
    }

    @Override
    public void updateCategory(Category category) {
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        this.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 检查是否有子分类
        Long childCount = this.count(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, categoryId));
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，无法删除");
        }

        this.removeById(categoryId);
    }
}
