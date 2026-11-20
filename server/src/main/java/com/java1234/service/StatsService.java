package com.java1234.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 *
 * <p>为管理后台首页仪表盘提供统计数据。</p>
 *
 * @author Java1234
 */
public interface StatsService {

    /**
     * 查询首页概览统计数据
     *
     * @return 统计数据集合
     */
    Map<String, Object> overview();

    /**
     * 查询宠物分类数量分布
     *
     * @return 分类名称与数量列表
     */
    List<Map<String, Object>> categoryCount();

    /**
     * 查询近 N 天的领养申请趋势
     *
     * @param days 统计天数
     * @return 日期与数量列表
     */
    List<Map<String, Object>> applyTrend(int days);

    /**
     * 查询领养申请状态分布
     *
     * @return 状态名称与数量列表
     */
    List<Map<String, Object>> applyStatusCount();

    /**
     * 查询各城市的宠物数量分布
     *
     * @return 城市名称与数量列表
     */
    List<Map<String, Object>> cityCount();

    /**
     * 查询最新提交的领养申请
     *
     * @param limit 数量限制
     * @return 申请列表
     */
    List<Map<String, Object>> latestApply(int limit);
}
