package com.heima.model.wemedia.vos;

import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 9:48
 * @Version 1.0
 */
@Data
public class NewsAuthVo extends WmNews {
    private String authorName;
}
