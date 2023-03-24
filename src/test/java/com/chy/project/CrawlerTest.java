package com.chy.project;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chy.project.model.entity.Picture;
import com.chy.project.model.entity.Post;
import com.chy.project.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: CHY
 * @date: 2023/3/23 22:20
 */
@SpringBootTest
public class CrawlerTest {

    @Resource
    private PostService postService;

    /**
     * 获取图片测试
     */
    @Test
    void testFetchPicture() throws IOException {
        //拿到整个 html 页面
        int current = 1;
        String url = "https://www.bing.com/images/search?q=坤坤&form=HDRSC3&first=" + current;
        //String url = String.format("https://www.bing.com/images/search?q=鲲鲲&form=HDRSC3&first=%s", current);
        Document doc = Jsoup.connect(url).get(); // jsoup得到的对象
        //提取 html 页面中的内容
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : elements) {
            //取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            //解析获取到的JSON数据
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            // System.out.println(murl);
            //图图片标题（inflnk）
            String title = element.select(".inflnk").get(0).attr("aria-label");
            // System.out.println(title);

            Picture picture = new Picture();
            picture.setUrl(murl);
            picture.setTitle(title);
            pictureList.add(picture);

            //System.out.println(m);
            //System.out.println(title);
            //System.out.println(element);
        }
        System.out.println(pictureList);
    }


    /*
     * 获取文章测试
     */
    @Test
    void testFetchPassage() {
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
        Assertions.assertTrue(b);
    }
}
