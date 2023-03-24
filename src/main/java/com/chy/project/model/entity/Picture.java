package com.chy.project.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 图片
 * @author: CHY
 * @date: 2023/3/24 16:56
 */
@Data
public class Picture implements Serializable {

    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片标题
     */
    private String title;

    private static final long serialVersionUID = 1L;
}
