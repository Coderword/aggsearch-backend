package com.chy.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.project.model.entity.Picture;

/**
 * 图片服务
 */
public interface PictureService {

    /**
     * 搜索图片
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);

}
