package com.java1234.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.java1234.common.BusinessException;
import com.java1234.common.Result;
import com.java1234.entity.PetCategory;
import com.java1234.service.PetCategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物分类控制器
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/category")
public class PetCategoryController {

    /** 分类服务 */
    private final PetCategoryService petCategoryService;

    /**
     * 构造方法，注入依赖
     *
     * @param petCategoryService 分类服务
     */
    public PetCategoryController(PetCategoryService petCategoryService) {
        this.petCategoryService = petCategoryService;
    }

    /**
     * 查询全部分类
     *
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<PetCategory>> list() {
        return Result.success(petCategoryService.listAll());
    }

    /**
     * 新增分类
     *
     * @param category 分类信息
     * @return 分类ID
     */
    @PostMapping
    public Result<Long> add(@RequestBody PetCategory category) {
        if (petCategoryService.count(Wrappers.<PetCategory>lambdaQuery().eq(PetCategory::getName, category.getName())) > 0) {
            throw new BusinessException("该分类名称已存在");
        }
        category.setId(null);
        if (category.getSort() == null) {
            category.setSort(0);
        }
        category.setCreateTime(LocalDateTime.now());
        petCategoryService.save(category);
        return Result.success("新增成功", category.getId());
    }

    /**
     * 修改分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PetCategory category) {
        PetCategory exist = petCategoryService.getById(id);
        if (exist == null) {
            throw new BusinessException("分类不存在");
        }
        category.setId(id);
        petCategoryService.updateById(category);
        return Result.success("修改成功", null);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petCategoryService.removeById(id);
        return Result.success("删除成功", null);
    }
}
