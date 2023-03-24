package com.chy.project.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chy.project.model.dto.post.PostEsDTO;
import com.chy.project.model.entity.Post;
import com.chy.project.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.Assertions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 获取初始化帖子列表
 */
// todo 取消注释开启任务
// 取消注释后，每次启动 SpringBoot 项目时会执行一次 run 方法；单次获取帖子完成后，开启注释@Component，下次就不会执行了
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        // 1. 获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json) //发给后台哪些内容
                .execute() //发送请求
                .body(); //获取响应的内容
        //System.out.println(result);

        // 2. json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title")); //设置标题
            post.setContent(tempRecord.getStr("content")); //设置内容
            //设置标签
            JSONArray tags = (JSONArray) tempRecord.get("tags"); //转数组
            List<String> tagList = tags.toList(String.class); //转List
            post.setTags(JSONUtil.toJsonStr(tagList)); //转字符串
            post.setUserId(1L);
            postList.add(post);
        }
        //System.out.println(postList);

        // 3. 数据入库
        boolean b = postService.saveBatch(postList);
        if (b) {
            log.info("获取初始化帖子列表成功，条数 = {}", postList.size());
        } else {
            log.error("获取初始化帖子列表失败！");
        }
    }
}
