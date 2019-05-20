package com.H.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.H.pojo.Users;
import com.H.pojo.vo.UsersVO;
import com.H.service.UserService;
import com.H.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户相关业务的接口", tags = "用户相关业务的controller")
@RequestMapping("/user")
public class UserController extends BasicController {

	@Autowired
	private UserService userService;
	
	/**
	 * @description 用户上传头像
	 */
	@ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
	@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/uploadFace")
	public JSONResult uploadFace( String userId, @RequestParam(value = "file") MultipartFile[] files) {
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户不能为空！");
		}
		// 文件保存的空间
		String fileSpace = "D:/z_vlog";
		// 保存到数据库的相对路径前缀
		String uploadPathDB = "/" + userId + "/face";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (files != null && files.length > 0) {
				String fileName = files[0].getOriginalFilename();
				// 保存到数据的路径
				uploadPathDB += "/" + fileName;
				// 文件的保存路径
				String finalFacePath = fileSpace + uploadPathDB;

				File outFile = new File(finalFacePath);
				if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
					// 创建父文件夹
					outFile.getParentFile().mkdirs();
				}
				fileOutputStream = new FileOutputStream(outFile);
				inputStream = files[0].getInputStream();
				IOUtils.copy(inputStream, fileOutputStream);

			} else {
				return JSONResult.errorMsg("上传出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONResult.errorMsg("上传出错");
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//将相对路径存入数据库中
		System.out.println( uploadPathDB );
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userService.updateUserInfo(user);
		
		return JSONResult.ok(uploadPathDB);
	}
	/**
	 * @description 用户上传头像
	 */
	@ApiOperation(value = "用户信息查询", notes = "用户信息查询的接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/query")
	public JSONResult query(String userId) {
		
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空");
		}
		Users user = userService.queryUserInfo(userId);
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(user, usersVO);
		
		return JSONResult.ok(usersVO);
		
	}
	
}
