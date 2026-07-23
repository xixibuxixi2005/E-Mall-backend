package com.whut.emall.business;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whut.emall.business.mapper.CategoryMapper;
import com.whut.emall.business.service.CategoryService;
import com.whut.emall.common.entity.Category;

import jakarta.annotation.Resource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = BusinessApplication.class)
public class CategoryServiceTest {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryService categoryService;

    private static Integer testCategoryId;
    private static final String TEST_CATEGORY_NAME = "测试分类_JUnit";

    @Test
    @Order(1)
    public void testGetCategories() {
        List<Category> list = categoryService.getCategories();
        Assertions.assertNotNull(list);
        System.out.println("分类总数：" + list.size());
    }

    @Test
    @Order(2)
    public void testSelectPage() {
        Page<Category> page = new Page<>(1, 10);
        Page<Category> result = categoryMapper.selectPage(page, null);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getRecords());
    }

    @Test
    @Order(3)
    public void testCreateCategory() {
        Category category = categoryService.createCategory(TEST_CATEGORY_NAME, 0, 999);
        Assertions.assertNotNull(category);
        Assertions.assertEquals(TEST_CATEGORY_NAME, category.getName());
        testCategoryId = category.getId();
        System.out.println("创建分类ID：" + testCategoryId);
    }

    @Test
    @Order(4)
    public void testGetCategoryByName() {
        Category category = categoryService.getCategoryByName(TEST_CATEGORY_NAME);
        Assertions.assertNotNull(category);
        Assertions.assertEquals(TEST_CATEGORY_NAME, category.getName());
    }

    @Test
    @Order(5)
    public void testCreateChildCategory() {
        if (testCategoryId == null) return;
        String childName = TEST_CATEGORY_NAME + "_子分类";
        Category child = categoryService.createCategory(childName, testCategoryId, 1);
        Assertions.assertNotNull(child);
        Assertions.assertEquals(2, child.getLevel());
        System.out.println("创建子分类ID：" + child.getId());
        categoryMapper.deleteById(child.getId());
    }

    @Test
    @Order(6)
    public void testUpdateCategory() {
        if (testCategoryId == null) return;
        categoryService.updateCategory(testCategoryId, TEST_CATEGORY_NAME + "_Updated", 888);
        Category updated = categoryMapper.selectById(testCategoryId);
        Assertions.assertEquals(TEST_CATEGORY_NAME + "_Updated", updated.getName());
        Assertions.assertEquals(888, updated.getSortOrder());
    }

    @Test
    @Order(7)
    public void testSelectById() {
        if (testCategoryId == null) return;
        Category category = categoryMapper.selectById(testCategoryId);
        Assertions.assertNotNull(category);
        Assertions.assertNotNull(category.getName());
    }

    @Test
    @Order(8)
    public void testDeleteCategory() {
        if (testCategoryId == null) return;
        categoryService.deleteCategory(testCategoryId);
        Category deleted = categoryMapper.selectById(testCategoryId);
        Assertions.assertNull(deleted);
        System.out.println("删除分类ID：" + testCategoryId);
    }
}
