package com.H.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.H.mapper.BgmMapper;
import com.H.pojo.Bgm;
import com.H.service.BgmService;

@Service
public class BgmServiceImpl implements BgmService {

	@Autowired
	private BgmMapper bgmMapper;
//	@Autowired
//	private Sid sid;
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		return bgmMapper.selectAll();
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Bgm queryBgm(String id) {
		return bgmMapper.selectByPrimaryKey(id);
	}
	
}
