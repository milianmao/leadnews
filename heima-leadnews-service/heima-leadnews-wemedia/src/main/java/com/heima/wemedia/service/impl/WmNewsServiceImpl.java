package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IArticleClient;
import com.heima.common.constants.NewsConstants;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.*;
import com.heima.model.wemedia.vos.NewsAuthVo;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.*;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:45
 * @Version 1.0
 */
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmNewsTaskService wmNewsTaskService;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private IArticleClient iArticleClient;

    @Override
    public ResponseResult findNewList(WmNewsPageReqDto dto) {
        // ????????????
        dto.checkParam();
        // ??????????????????
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lqw = new LambdaQueryWrapper<>();
        // ??????????????????
        if (dto.getStatus() != null) {
            lqw.eq(WmNews::getStatus, dto.getStatus());
        }
        // ???????????????
        if (StringUtils.isNotBlank(dto.getKeyword())) {
            lqw.like(WmNews::getTitle, dto.getKeyword());
        }
        // ????????????
        if (dto.getChannelId() != null) {
            lqw.eq(WmNews::getChannelId, dto.getChannelId());
        }
        // ????????????????????????
        if (dto.getBeginPubDate() != null) {
            lqw.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }
        lqw.orderByDesc(WmNews::getCreatedTime);
        page = page(page, lqw);
        ResponseResult responseResult = new PageResponseResult((int) page.getPages(), (int) page.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult submit(WmNewsDto dto) {
        if (dto == null || dto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // ?????????????????????
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(dto, wmNews);
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        saveOrUpdateWmNews(wmNews);
        // ?????????????????????
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        // ??????????????????????????????
        List<String> materials = ectractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials, wmNews.getId());
        // ???????????????????????????????????????????????????
        saveRelativeInfoForCover(dto, wmNews, materials);
        // ????????????
        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        // ?????????????????????
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description ????????????
     * @Date 10:43
     * @Param [dto]
     **/
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // ????????????
        if (dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // ??????????????????
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "???????????????");
        }
        // ???????????????????????????
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "????????????????????????????????????????????????");
        }
        // ????????????enable
        if (dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2) {
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable, dto.getEnable())
                    .eq(WmNews::getId, wmNews.getId()));
        }
        if (wmNews.getArticleId() != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("articleId", wmNews.getArticleId());
            map.put("enable", dto.getEnable());
            kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO ??????????????????
     * @Date 9:44
     * @Param [newsAuthDto]
     **/
    @Override
    public ResponseResult listVo(NewsAuthDto newsAuthDto) {
        if (newsAuthDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        IPage<NewsAuthVo> page = new Page<>(newsAuthDto.getPage(), newsAuthDto.getSize());
        page = wmNewsMapper.getListVo(page, newsAuthDto);
        ResponseResult responseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO ??????????????????
     * @Date 14:28
     * @Param [id]
     **/
    @Override
    public ResponseResult newsAuthOne(Long id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        NewsAuthVo newsAuthOne = wmNewsMapper.getNewsAuthOne(id);
        return ResponseResult.okResult(newsAuthOne);
    }

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO ??????????????????
     * @Date 14:32
     * @Param [newsAuthDto]
     **/
    @Override
    public ResponseResult authFail(NewsAuthDto newsAuthDto) {
        if (newsAuthDto == null || newsAuthDto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (newsAuthDto.getStatus().equals(NewsConstants.NEWS_STATUS_MANUAL)) {
            WmNews news = getById(newsAuthDto.getId());
            news.setStatus(NewsConstants.NEWS_STATUS_FAIL);
            news.setReason(newsAuthDto.getMsg());
            save(news);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.NEWS_NOT_AUDIT);
    }

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO ??????????????????
     * @Date 15:21
     * @Param [newsAuthDto]
     **/
    @Override
    public ResponseResult authPass(NewsAuthDto newsAuthDto) {
        if (newsAuthDto == null || newsAuthDto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (newsAuthDto.getStatus().equals(NewsConstants.NEWS_STATUS_MANUAL)) {
            WmNews wmNews = getById(newsAuthDto.getId());


            // ??????????????????
            ArticleDto articleDto = new ArticleDto();
            BeanUtils.copyProperties(wmNews, articleDto);
            // ??????
            articleDto.setLayout(wmNews.getType());
            // ??????
            WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
            if (wmChannel != null) {
                articleDto.setChannelName(wmChannel.getName());
            }
            // ??????
            articleDto.setAuthorId(wmNews.getUserId().longValue());
            WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
            if (wmUser != null) {
                articleDto.setAuthorName(wmUser.getName());
            }
            // ??????id
            if (wmNews.getArticleId() != null) {
                articleDto.setId(wmNews.getArticleId());
            }
            articleDto.setCreatedTime(new Date());
            ResponseResult responseResult = iArticleClient.saveArticle(articleDto);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-?????????????????????app???????????????????????????");
            }
            //??????article_id
            wmNews.setArticleId((Long) responseResult.getData());
            wmNews.setStatus(NewsConstants.NEWS_STATUS_PASS);
            save(wmNews);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.NEWS_NOT_AUDIT);
    }

    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {
        List<String> images = dto.getImages();

        //??????????????????????????????????????????????????????????????????
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            //??????
            if (materials.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() >= 1 && materials.size() < 3) {
                //??????
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                //??????
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //????????????
            if (images != null && images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }
        if (images != null && images.size() > 0) {
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * @return void
     * @Author milian
     * @Description ????????????????????????????????????
     * @Date 14:21
     * @Param [materials, id]
     **/
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * @return void
     * @Author milian
     * @Description ??????????????????
     * @Date 14:22
     * @Param [materials, newsId, wmContentReference]
     **/
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()) {
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));
            if (dbMaterials == null || dbMaterials.size() == 0) {
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }
            if (materials.size() != dbMaterials.size()) {
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }
            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
            //????????????
            wmNewsMaterialMapper.saveRelations(idList, newsId, type);

        }
    }

    private List<String> ectractUrlInfo(String content) {
        ArrayList<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    /**
     * @description: ?????????????????????
     * @author milian
     * @date 2021/11/22 0022
     * @version 1.0
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        // ????????????
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);
        if (wmNews.getId() == null) {
            // ??????
            save(wmNews);
        } else {
            // ??????
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }
}
