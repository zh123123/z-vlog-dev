package com.H.serviceImpl;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.H.mapper.VideosMapper;
import com.H.pojo.Videos;
import com.H.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService{

	@Autowired
	private VideosMapper videosMapper;
	@Autowired
	private Sid sid;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		String id = sid.nextShort();
		video.setId(id);
		videosMapper.insertSelective(video);
		return id;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateVideoCover(String videoId, String coverPath) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videosMapper.updateByPrimaryKeySelective(video);
	}
	
	

}
