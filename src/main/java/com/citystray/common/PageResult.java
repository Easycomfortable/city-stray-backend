package com.citystray.common;

import lombok.Data;

import java.util.List;

/**
 * 分页查询结果封装类
 * <p>
 * 用于封装分页查询的返回结果，包含总记录数和当前页数据列表。
 * </p>
 *
 * @param <T> 列表元素的泛型类型
 * @author CityStray Team
 * @since 1.0.0
 */
@Data
public class PageResult<T> {

    /** 总记录数 */
    private Long total;

    /** 当前页数据列表 */
    private List<T> records;

    /**
     * 构造方法
     *
     * @param total   总记录数
     * @param records 当前页数据列表
     */
    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
}
