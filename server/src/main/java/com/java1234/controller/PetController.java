package com.java1234.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.LoginUser;
import com.java1234.common.PageResult;
import com.java1234.common.Result;
import com.java1234.dto.PetDTO;
import com.java1234.entity.Pet;
import com.java1234.entity.PetImage;
import com.java1234.entity.SysUser;
import com.java1234.service.PetService;
import com.java1234.service.SysUserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 宠物信息控制器
 *
 * <p>提供宠物列表、详情、发布、修改、上下架等接口，小程序端与后台共用。</p>
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/pet")
public class PetController {

    /** 宠物服务 */
    private final PetService petService;

    /** 用户服务 */
    private final SysUserService sysUserService;

    /**
     * 构造方法，注入依赖
     *
     * @param petService    宠物服务
     * @param sysUserService 用户服务
     */
    public PetController(PetService petService, SysUserService sysUserService) {
        this.petService = petService;
        this.sysUserService = sysUserService;
    }

    /**
     * 分页查询宠物列表
     *
     * @param page       页码
     * @param pageSize   每页条数
     * @param keyword    关键字
     * @param categoryId 分类ID
     * @param city       城市
     * @param gender     性别
     * @param status     状态
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<Pet>> list(@RequestParam(defaultValue = "1") Long page,
                                        @RequestParam(defaultValue = "10") Long pageSize,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long categoryId,
                                        @RequestParam(required = false) String city,
                                        @RequestParam(required = false) String gender,
                                        @RequestParam(required = false) Integer status) {
        Page<Pet> pageInfo = new Page<>(page, pageSize);
        Page<Pet> result = petService.pagePet(pageInfo, keyword, categoryId, city, gender, status, null);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 查询宠物详情
     *
     * @param id 宠物ID
     * @return 宠物详情（含相册）
     */
    @GetMapping("/detail/{id}")
    public Result<Pet> detail(@PathVariable Long id) {
        // 获取当前登录用户ID，用于标记收藏状态
        Long userId = currentUserId();
        Pet pet = petService.getDetail(id, userId);
        List<PetImage> images = petService.listImages(id);
        pet.setImages(images);
        return Result.success(pet);
    }

    /**
     * 发布宠物领养信息
     *
     * @param petDTO 宠物信息
     * @return 新增的宠物ID
     */
    @PostMapping
    public Result<Long> add(@RequestBody PetDTO petDTO) {
        SysUser user = currentUser();
        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setCategoryId(petDTO.getCategoryId());
        pet.setAge(petDTO.getAge());
        pet.setGender(petDTO.getGender());
        pet.setCity(petDTO.getCity());
        pet.setPrice(petDTO.getPrice());
        pet.setHealth(petDTO.getHealth());
        pet.setDescription(petDTO.getDescription());
        pet.setCoverImg(petDTO.getCoverImg());
        pet.setVaccine(petDTO.getVaccine() == null ? 0 : petDTO.getVaccine());
        pet.setSterilized(petDTO.getSterilized() == null ? 0 : petDTO.getSterilized());
        pet.setDeworm(petDTO.getDeworm() == null ? 0 : petDTO.getDeworm());
        pet.setStatus(petDTO.getStatus() == null ? 1 : petDTO.getStatus());
        pet.setUserId(user.getId());
        Long id = petService.savePet(pet, petDTO.getImages());
        return Result.success("发布成功", id);
    }

    /**
     * 修改宠物信息
     *
     * @param id     宠物ID
     * @param petDTO 宠物信息
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(petDTO.getName());
        pet.setCategoryId(petDTO.getCategoryId());
        pet.setAge(petDTO.getAge());
        pet.setGender(petDTO.getGender());
        pet.setCity(petDTO.getCity());
        pet.setPrice(petDTO.getPrice());
        pet.setHealth(petDTO.getHealth());
        pet.setDescription(petDTO.getDescription());
        pet.setCoverImg(petDTO.getCoverImg());
        pet.setVaccine(petDTO.getVaccine());
        pet.setSterilized(petDTO.getSterilized());
        pet.setDeworm(petDTO.getDeworm());
        pet.setStatus(petDTO.getStatus());
        petService.updatePet(pet, petDTO.getImages());
        return Result.success("修改成功", null);
    }

    /**
     * 修改宠物上下架状态
     *
     * @param id 宠物ID
     * @return 是否成功
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        if (pet == null) {
            throw new com.java1234.common.BusinessException("宠物信息不存在");
        }
        // 1 待领养 2 已领养 0 下架，循环切换
        int target = pet.getStatus() != null && pet.getStatus() == 0 ? 1 : 0;
        petService.updateStatus(id, target);
        return Result.success("操作成功", null);
    }

    /**
     * 删除宠物信息
     *
     * @param id 宠物ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petService.removeById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 查询我发布的宠物列表
     *
     * @return 宠物列表
     */
    @GetMapping("/my")
    public Result<List<Pet>> myPets() {
        SysUser user = currentUser();
        Page<Pet> pageInfo = new Page<>(1, 100);
        Page<Pet> result = petService.pagePet(pageInfo, null, null, null, null, null, user.getId());
        List<Pet> records = new ArrayList<>(result.getRecords());
        records.forEach(p -> p.setImages(petService.listImages(p.getId())));
        return Result.success(records);
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前用户
     */
    private SysUser currentUser() {
        SysUser user = sysUserService.getByUsername(LoginUser.getUsername());
        if (user == null) {
            throw new com.java1234.common.BusinessException("登录状态已失效，请重新登录");
        }
        return user;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，未登录返回 null
     */
    private Long currentUserId() {
        SysUser user = sysUserService.getByUsername(LoginUser.getUsername());
        return user == null ? null : user.getId();
    }
}
