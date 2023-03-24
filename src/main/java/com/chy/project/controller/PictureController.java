package com.chy.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.project.annotation.AuthCheck;
import com.chy.project.common.BaseResponse;
import com.chy.project.common.DeleteRequest;
import com.chy.project.common.ErrorCode;
import com.chy.project.common.ResultUtils;
import com.chy.project.constant.UserConstant;
import com.chy.project.exception.BusinessException;
import com.chy.project.exception.ThrowUtils;
import com.chy.project.model.dto.picture.PictureQueryRequest;
import com.chy.project.model.dto.post.PostAddRequest;
import com.chy.project.model.dto.post.PostEditRequest;
import com.chy.project.model.dto.post.PostQueryRequest;
import com.chy.project.model.dto.post.PostUpdateRequest;
import com.chy.project.model.entity.Picture;
import com.chy.project.model.entity.Post;
import com.chy.project.model.entity.User;
import com.chy.project.model.vo.PostVO;
import com.chy.project.service.PictureService;
import com.chy.project.service.PostService;
import com.chy.project.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 */
@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureService pictureService;


    /**
     * 分页获取图片列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                           HttpServletRequest request) {
        //获取第几页数据
        long current = pictureQueryRequest.getCurrent();
        //获取每页有多少条数据
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        String searchText = pictureQueryRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }

}
