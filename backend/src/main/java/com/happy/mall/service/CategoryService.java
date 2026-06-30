package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.Category;

import java.util.List;

/**
 * 分类 Service
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树形结构
     */
    List<Category> getCategoryTree();

    /**
     * 获取所有分类
     */
    List<Category> getAllCategories();

    /**
     * 新增分类
     */
    void addCategory(Category category);

    /**
     * 更新分类
     */
    void updateCategory(Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
}
