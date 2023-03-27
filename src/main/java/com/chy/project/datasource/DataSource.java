package com.chy.project.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.ss.formula.functions.T;

/**
 * @description: 数据源接口（新接入的数据源必须实现）
 * @author: CHY
 * @date: 2023/3/27 14:47
 */
public interface DataSource<T> {

    /**
     * 搜索
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
