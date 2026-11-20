package com.java1234.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 *
 * @author Java1234
 */
@Data
public class PageResult<T> implements Serializable {

    /** 总记录数 */
    private Long total;

    /** 当前页数据列表 */
    private List<T> records;

    /**
     * 构造分页结果
     *
     * @param total   总记录数
     * @param records 当前页数据
     */
    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
