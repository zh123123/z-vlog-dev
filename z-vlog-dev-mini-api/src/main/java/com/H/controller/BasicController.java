package com.H.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.H.utils.RedisOperator;

@RestController
public class BasicController {
	@Autowired
	public RedisOperator redis ;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";
	
	public static final String FILESPACE = "D:/z_vlog";
	
	public static final String FFMPEG_EXE = "D:\\Java\\ffmpeg\\bin\\ffmpeg.exe";
	
}
