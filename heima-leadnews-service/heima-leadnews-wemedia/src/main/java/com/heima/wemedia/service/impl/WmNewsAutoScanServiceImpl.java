package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;

import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/23 0023 15:30
 * @Version 1.0
 */
@Service
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    public static final String SUGGESTION = "suggestion";
    public static final String BLOCK = "block";
    public static final String REVIEW = "review";

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private GreenImageScan greenImageScan;
    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private IArticleClient iArticleClient;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;
    @Autowired
    private Tess4jClient tess4jClient;

    @Transactional
    @Override
    @Async
    public void autoScanWmNews(Integer id) {
        // 查询文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            ////从内容中提取纯文本内容和图片
            //Map<String, Object> textAndImages = handleTextAndImages(wmNews);
            //// 自定义敏感词审核
            //boolean isSensitive = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
            //if(!isSensitive) return;
            //// 审核文本内容  阿里云接口
            //boolean isTextScan = handleTextScan((String) textAndImages.get("content"), wmNews);
            //if (!isTextScan) {
            //    return;
            //}
            //// 审核图片  阿里云接口
            //boolean isImageScan = handleImageScan((List<String>) textAndImages.get("images"), wmNews);
            //if (!isImageScan) {
            //    return;
            //}
            // 保存文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews, (short) 9, "审核成功");
        }
    }

    /**
     * @return boolean
     * @Author milian
     * @Description 自定义敏感词审核
     * @Date 9:51
     * @Param [content, wmNews]
     **/
    private boolean handleSensitiveScan(String content, WmNews wmNews) {
        boolean flag = true;
        // 查询敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        // 初始化字典库
        SensitiveWordUtil.initMap(sensitiveList);
        // 审核
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if (map.size() > 0) {
            updateWmNews(wmNews, (short) 2, "当前文章中存在违规内容" + map);
            flag = false;
        }
        return flag;
    }

    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);
        // 布局
        articleDto.setLayout(wmNews.getType());
        // 频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }

        // 作者
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }
        // 文章id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());
        ResponseResult responseResult = iArticleClient.saveArticle(articleDto);
        return responseResult;
    }

    /**
     * @return boolean
     * @Author milian
     * @Description //TODO 图片审核
     * @Date 16:06
     * @Param [images, wmNews]
     **/
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        if (images == null || images.size() == 0) {
            return flag;
        }
        List<byte[]> imageList = new ArrayList<>();
        // 文章去重
        images = images.stream().distinct().collect(Collectors.toList());
        // 下载图片
        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);
            imageList.add(bytes);
        }
        // 图片文字审核
        try {
            for (String image : images) {
                byte[] bytes = fileStorageService.downLoadFile(image);
                //从byte[]转换为butteredImage
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BufferedImage imageFile = ImageIO.read(in);
                //识别图片的文字
                String result = tess4jClient.doOCR(imageFile);
                //审核是否包含自管理的敏感词
                boolean isSensitive = handleSensitiveScan(result, wmNews);
                if (!isSensitive) {
                    return isSensitive;
                }
                imageList.add(bytes);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 提交图片审核
        try {
            Map map = greenImageScan.imageScan(imageList);
            if (map != null) {
                if (map.get(SUGGESTION).equals(BLOCK)) {
                    flag = false;
                    updateWmNews(wmNews, (short) 2, "当前文章中存在违规内容");
                }
                if (map.get(SUGGESTION).equals(REVIEW)) {
                    flag = false;
                    updateWmNews(wmNews, (short) 3, "当前文章中存在不确定内容");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * @return boolean
     * @Author milian
     * @Description //TODO 文章内容审核
     * @Date 15:57
     * @Param [content, wmNews]
     **/
    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;
        if ((content + '-' + wmNews.getTitle()).length() == 0) {
            return flag;
        }
        try {
            Map map = greenTextScan.greeTextScan(content + '-' + wmNews.getTitle());
            if (map != null) {
                if (map.get(SUGGESTION).equals(BLOCK)) {
                    flag = false;
                    updateWmNews(wmNews, (short) 2, "当前文章中存在违规内容");
                }
                if (map.get(SUGGESTION).equals(REVIEW)) {
                    flag = false;
                    updateWmNews(wmNews, (short) 3, "当前文章中存在不确定内容");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }

        return flag;
    }

    /**
     * @return void
     * @Author milian
     * @Description //TODO 修改文章内容
     * @Date 15:55
     * @Param [wmNews, status, reason]
     **/
    private void updateWmNews(WmNews wmNews, short status, String reason) {
        wmNews.setReason(reason);
        wmNews.setStatus(status);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author milian
     * @Description 提取文章纯文本内容和图片
     * @Date 15:36
     * @Param [wmNews]
     **/
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        //存储纯文本内容
        StringBuilder stringBuilder = new StringBuilder();
        List<String> images = new ArrayList<>();
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSON.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }
        // 提取封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;
    }
}
