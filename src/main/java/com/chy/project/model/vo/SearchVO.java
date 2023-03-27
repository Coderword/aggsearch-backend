package com.chy.project.model.vo;

import com.chy.project.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    //通用数据源
    private List<?> dataList;

    private static final long serialVersionUID = 1L;
}
