package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.common.EsThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 15:32
 * @Version 1.0
 */
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    @Qualifier(value = "client")
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ApUserSearchService apUserSearchService;

    @Override
    public ResponseResult search(UserSearchDto userSearchDto) throws IOException {
        // 参数校验
        if (userSearchDto == null || StringUtils.isBlank(userSearchDto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = EsThreadLocalUtil.getUser();
        //异步调用 保存搜索记录
        if (user != null && userSearchDto.getFromIndex() == 0) {
            apUserSearchService.insert(userSearchDto.getSearchWords(), user.getId());
        }
        // 设置参数
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 关键字分词后查询
        QueryBuilders.queryStringQuery(userSearchDto.getSearchWords())
                .field("title")
                .field("content")
                .defaultOperator(Operator.OR);
        // 查询小于minBehotTime的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime")
                .lt(userSearchDto.getMinBehotTime().getTime());
        // 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(userSearchDto.getPageSize());

        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 封装数据
        ArrayList<Map> list = new ArrayList<>();
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            Map map = JSON.parseObject(source, Map.class);
            // 处理高亮
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0) {
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StringUtils.join(titles);
                //高亮标题
                map.put("h_title", title);
            } else {
                map.put("h_title", map.get("title"));
            }
            list.add(map);
        }
        return ResponseResult.okResult(list);
    }
}
