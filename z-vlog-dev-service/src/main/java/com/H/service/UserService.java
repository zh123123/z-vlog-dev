package com.H.service;

import com.H.pojo.Users;

public interface UserService {
	/**
	 * @description 判断用户名是否存在
	 */
	boolean queryUserNameIsExist(String username);
	/**
	 * 
	 * @description 保存用户（注册）
	 */
	void saveUser(Users user) ;
	/**
	 * @description 验证用户（用于登录）
	 */
	Users queryUserForLogin(String username,String password);
	
	/**
	 * @description 更新用户信息
	 */
	void updateUserInfo(Users user);
	
	/**
	 * @return 
	 * @description 查询用户信息
	 */
	Users queryUserInfo(String userId);
}
