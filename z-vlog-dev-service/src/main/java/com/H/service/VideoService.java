package com.H.service;

import com.H.pojo.Videos;

public interface VideoService {

	/**
	 * 保存视频
	 */
	String saveVideo(Videos video);
	/**
	 * 更新视频信息封面
	 */
	void updateVideoCover(String videoId , String videoCover);
}
