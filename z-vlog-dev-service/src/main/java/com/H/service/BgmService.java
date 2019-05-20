package com.H.service;

import java.util.List;

import com.H.pojo.Bgm;

public interface BgmService {
	/**
	 * @description 查询bgm列表
	 */
	List<Bgm> queryBgmList();
	
	/**
	 * @description 根据id查询bgm
	 */
	Bgm queryBgm(String id);
}
