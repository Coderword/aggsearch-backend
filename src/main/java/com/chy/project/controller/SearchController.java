package com.chy.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.project.common.BaseResponse;
import com.chy.project.common.ErrorCode;
import com.chy.project.common.ResultUtils;
import com.chy.project.exception.BusinessException;
import com.chy.project.exception.ThrowUtils;
import com.chy.project.manager.SearchFacade;
import com.chy.project.model.dto.post.PostQueryRequest;
import com.chy.project.model.dto.search.SearchQueryRequest;
import com.chy.project.model.dto.user.UserQueryRequest;
import com.chy.project.model.entity.Picture;
import com.chy.project.model.enums.SearchTypeEnum;
import com.chy.project.model.vo.PostVO;
import com.chy.project.model.vo.SearchVO;
import com.chy.project.model.vo.UserVO;
import com.chy.project.service.PictureService;
import com.chy.project.service.PostService;
import com.chy.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 聚合搜索接口（统一搜索）
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {
        /* 门面模式 */
        return ResultUtils.success(searchFacade.searchAll(searchQueryRequest, request));
    }
}
