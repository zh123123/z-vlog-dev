package com.H.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.H.pojo.Users;
import com.H.pojo.vo.UsersVO;
import com.H.service.UserService;
import com.H.utils.JSONResult;
import com.H.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="用户注册登录的接口",tags= {"注册和登录的controller"})
public class RegistLoginController extends BasicController{
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value="用户注册",notes="用户注册的接口")
	@PostMapping("/regist")
	public JSONResult regist(@RequestBody Users user) throws Exception {
		
		//Thread.sleep(3000);
		
		if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名或密码不能为空！");
		}
		
		boolean userNameIsExist = userService.queryUserNameIsExist(user.getUsername());
		if(!userNameIsExist) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);
			userService.saveUser(user);
		}else {
			return JSONResult.errorMsg("用户名已存在！");
		}
		user.setPassword("");
		
		UsersVO usersVO = setUserRedisSessionToken(user);
		return JSONResult.ok(usersVO);
	}
	
	@ApiOperation(value="用户登录",notes="用户登录的接口")
	@PostMapping("/login")
	public JSONResult login(@RequestBody Users user) throws Exception {
		
		if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名或密码不能为空！");
		}
		
		Users result = userService.queryUserForLogin(user.getUsername() , MD5Utils.getMD5Str(user.getPassword()));
		
		if( result == null ) {
			return JSONResult.errorMsg("用户名或密码错误");
		}
		result.setPassword("");
		UsersVO usersVO = setUserRedisSessionToken(result);
		return JSONResult.ok(usersVO);
	}
	
	@ApiOperation(value="用户注销", notes="用户注销的接口")
	@ApiImplicitParam(name="userId", value="用户id", required=true, 
						dataType="String", paramType="query")
	@PostMapping("/logout")
	public JSONResult logout(String userId) throws Exception {
		System.out.println(userId);
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return JSONResult.ok();
	}
	
	
	public UsersVO setUserRedisSessionToken(Users userModel) {
		String uniqueToken = UUID.randomUUID().toString();
		redis.set( USER_REDIS_SESSION + ":" + userModel.getId() , uniqueToken , 60 * 30);  //有效期为30min
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(userModel, usersVO);
		usersVO.setUserToken(uniqueToken);
		return usersVO;
	}
	
}
