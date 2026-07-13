package com.whut.emall.business.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whut.emall.business.entity.Category;
import com.whut.emall.business.mapper.CategoryMapper;
import com.whut.emall.common.entity.ApiException;

import jakarta.annotation.Resource;

@Service
public class CategoryService {
    @Resource CategoryMapper categoryMapper;

    public Category getCategoryByName(String name) {
        return categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getName, name));
    }
    
    public List<Category> getCategories() {
        return categoryMapper.selectList(null);
    }

    public Category createCategory(String name, Integer parentId, Integer sortOrder) {
        Category category = getCategoryByName(name);
        if (category != null)
                throw ApiException.err(400, "分类名已存在");
        int level = 1;
        if (parentId != null && parentId != 0) {
            Category parent = categoryMapper.selectById(parentId);
            if (parent == null)
                throw ApiException.err(404, "父分类不存在");
            level = parent.getLevel() + 1;
            if (level > 3)
                throw ApiException.err(400, "分类层级最多为3级");
        }
        category = new Category();
        category.setName(name);
        category.setParentId(parentId);
        category.setLevel(level);
        category.setSortOrder(sortOrder);
        categoryMapper.insert(category);
        return getCategoryByName(name);
    }

    public void updateCategory(Integer id, String name, Integer sortOrder) {
        Category category = categoryMapper.selectById(id);
        if (category == null)
            throw ApiException.err(404, "分类不存在");
        if (name != null && !name.equals(category.getName()) && getCategoryByName(name) != null)
            throw ApiException.err(400, "分类名已存在");
        category.setName(name);
        category.setSortOrder(sortOrder);
        categoryMapper.updateById(category);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null)
            throw ApiException.err(404, "分类不存在");
        List<Category> children = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (children != null && !children.isEmpty())
            throw ApiException.err(400, "该分类下存在子分类，无法删除");
        categoryMapper.deleteById(id);
    }
}
