package com.chy.project.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chy.project.common.BaseResponse;
import com.chy.project.common.ErrorCode;
import com.chy.project.datasource.DataSource;
import com.chy.project.datasource.PictureDataSource;
import com.chy.project.datasource.PostDataSource;
import com.chy.project.datasource.UserDataSource;
import com.chy.project.exception.BusinessException;
import com.chy.project.exception.ThrowUtils;
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
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @description: 搜索门面
 * @author: CHY
 * @date: 2023/3/27 14:25
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {

        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        //校验 type 是否为空
        /*if (StringUtils.isBlank(type)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "类型为空");
        }*/
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchQueryRequest.getSearchText();

        long current = searchQueryRequest.getCurrent(); //获取当前页码
        long pageSize = searchQueryRequest.getPageSize(); //条数

        //如果为空，搜索出所有数据
        if (searchTypeEnum == null) {

            //搜索用户
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userDataSource.doSearch(searchText, current, pageSize);
                return userVOPage;
            });

            //搜索文章
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postDataSource.doSearch(searchText, current, pageSize);
                return postVOPage;
            });

            //搜索图片
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
                return picturePage;
            });

            //把异步的对象组合起来
            CompletableFuture.allOf(userTask, postTask, pictureTask).join();

            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            //注册，每种数据源都放到Map里
            Map<String, DataSource<T>> typeDataSourcesMap = new HashMap() {{
                put(SearchTypeEnum.POST.getValue(), postDataSource);
                put(SearchTypeEnum.USER.getValue(), userDataSource);
                put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            }};

            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = typeDataSourcesMap.get(type);
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;

            /*
            DataSource dataSource = null;
            switch (searchTypeEnum) {
                case POST:
                    dataSource = postDateSource;
                    break;
                case USER:
                    dataSource = userDataSource;
                    break;
                case PICTURE:
                    dataSource = pictureDataSource;
                    break;
            }
            Page page = dataSource.doSearch(searchText, current, pageSize);
            */
        }
    }
}
