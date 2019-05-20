package com.H.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.H.service.BgmService;
import com.H.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "bgm相关的接口", tags = "bgm相关的controller")
@RequestMapping("/bgm")
public class BgmController {
	
	@Autowired
	private BgmService bgmService;
	
	@ApiOperation(value="bgm列表",notes="返回bgm列表的接口")
	@PostMapping("/bgmList")
	public JSONResult bgmList() {
		return JSONResult.ok(bgmService.queryBgmList());
	}
	
}
