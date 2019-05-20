package com.H.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.H.enums.VideoStatus;
import com.H.pojo.Bgm;
import com.H.pojo.Videos;
import com.H.service.BgmService;
import com.H.service.VideoService;
import com.H.utils.FFMpeg;
import com.H.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "video相关的接口", tags = "video相关的controller")
@RequestMapping("/video")
public class VideoController extends BasicController {

	@Autowired
	private BgmService bgmService;
	@Autowired
	private VideoService videoService;

	@ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "bgmid", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds", value = "视频时长", required = true, dataType = "double", paramType = "form"),
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "int", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "int", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = true, dataType = "String", paramType = "form") })
	@PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
	public JSONResult uploadVideo(String userId, String bgmId, double videoSeconds, int videoWidth, int videoHeight,
			String desc, @ApiParam(value = "短视频", required = true) MultipartFile file) throws IOException {

		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户不能为空！");
		}
		// 文件保存的空间
		// String fileSpace = "D:/z_vlog";
		// 保存到数据库的相对路径前缀
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";
		// 文件的保存路径
		String finalVideoPath = "";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				
				// 保存到数据的路径
				uploadPathDB += "/" + fileName;
				// 文件的保存路径
				finalVideoPath = FILESPACE + uploadPathDB;

				File outFile = new File(finalVideoPath);
				if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
					// 创建父文件夹
					outFile.getParentFile().mkdirs();
				}
				fileOutputStream = new FileOutputStream(outFile);
				inputStream = file.getInputStream();
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
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 如果bgmId不为空，则查询音乐，并合并视频产生新的视频
		if (StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgm(bgmId);
			String bgmPath = FILESPACE + bgm.getPath();
			FFMpeg ffMpeg = new FFMpeg(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;
			String videoNamePre = UUID.randomUUID().toString() ;
			uploadPathDB = "/" + userId + "/video/" + videoNamePre + ".mp4";
			coverPathDB += "/" + videoNamePre + ".jpg";
			finalVideoPath = FILESPACE + uploadPathDB;
			ffMpeg.mergeVideoMusic(videoInputPath, bgmPath, videoSeconds, finalVideoPath);
		}

		System.out.println("uploadPathDB:" + uploadPathDB);
		System.out.println("finalVideoPath:" + finalVideoPath);
		
		//对视频进行截图
		FFMpeg ffMpeg = new FFMpeg(FFMPEG_EXE);
		ffMpeg.getCover(finalVideoPath, FILESPACE + coverPathDB);
		
		// 将视频信息存入数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatus.SUCCESS.value);
		video.setCreateTime(new Date());
		String videoId = videoService.saveVideo(video);

		return JSONResult.ok(videoId);
	}

	@ApiOperation(value = "上传视频封面", notes = "上传视频封面的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form") })
	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	public JSONResult uploadCover(String videoId, String userId,
			@ApiParam(value = "短视频封面", required = true) MultipartFile file) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			JSONResult.errorMsg("用户id和视频id不能为空");
		}
		// 保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		String finalCoverPath = "";

		
		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();
				uploadPathDB += "/" + fileName;
				finalCoverPath = FILESPACE + uploadPathDB;
				File outFile = new File(finalCoverPath);
				if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
					// 创建父文件夹
					outFile.getParentFile().mkdirs();
				}
				fileOutputStream = new FileOutputStream(outFile);
				inputStream = file.getInputStream();
				IOUtils.copy(inputStream, fileOutputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONResult.errorMsg("上传出错");
		}finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//更新视频封面
		videoService.updateVideoCover(videoId, uploadPathDB);
		return JSONResult.ok();
	}

}
