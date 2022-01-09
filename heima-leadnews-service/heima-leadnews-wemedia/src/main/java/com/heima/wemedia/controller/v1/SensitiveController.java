package com.heima.wemedia.controller.v1;

import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 14:12
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/sensitive")
public class SensitiveController {
    @Autowired
    private WmSensitiveService wmSensitiveService;

    @PostMapping("/list")
    public ResponseResult sensitiveList(@RequestBody SensitiveDto dto) {
        return wmSensitiveService.sensitiveList(dto);
    }

    @PostMapping("/save")
    public ResponseResult sensitiveSave(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.sensitiveSave(wmSensitive);
    }

    @PostMapping("/update")
    public ResponseResult sensitiveUpdate(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.sensitiveUpdate(wmSensitive);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult sensitiveDelete(@PathVariable Long id) {
        return wmSensitiveService.sensitiveDelete(id);
    }
}
