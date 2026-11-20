package com.java1234.controller;

import com.java1234.common.Result;
import com.java1234.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 *
 * <p>为管理后台首页仪表盘提供图表数据。</p>
 *
 * @author Java1234
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    /** 统计服务 */
    private final StatsService statsService;

    /**
     * 构造方法，注入依赖
     *
     * @param statsService 统计服务
     */
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * 查询首页概览统计数据
     *
     * @return 统计数据集合
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(statsService.overview());
    }

    /**
     * 查询宠物分类数量分布
     *
     * @return 分布数据
     */
    @GetMapping("/category")
    public Result<List<Map<String, Object>>> category() {
        return Result.success(statsService.categoryCount());
    }

    /**
     * 查询近 N 天的领养申请趋势
     *
     * @param days 统计天数，默认 7 天
     * @return 趋势数据
     */
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(statsService.applyTrend(days));
    }

    /**
     * 查询领养申请状态分布
     *
     * @return 分布数据
     */
    @GetMapping("/apply-status")
    public Result<List<Map<String, Object>>> applyStatus() {
        return Result.success(statsService.applyStatusCount());
    }

    /**
     * 查询各城市宠物数量分布
     *
     * @return 分布数据
     */
    @GetMapping("/city")
    public Result<List<Map<String, Object>>> city() {
        return Result.success(statsService.cityCount());
    }

    /**
     * 查询首页所需的全部图表数据（一次性返回，减少请求次数）
     *
     * @return 图表数据集合
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("overview", statsService.overview());
        data.put("category", statsService.categoryCount());
        data.put("trend", statsService.applyTrend(7));
        data.put("applyStatus", statsService.applyStatusCount());
        data.put("city", statsService.cityCount());
        data.put("latestApply", statsService.latestApply(6));
        return Result.success(data);
    }
}
