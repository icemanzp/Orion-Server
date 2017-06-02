package com.jack.netty.test.security.service.impl;

import com.jack.netty.test.security.entity.Resource;
import com.jack.netty.test.security.entity.Role;
import com.jack.netty.test.security.persist.dao.ResourceDAO;
import com.jack.netty.test.security.persist.dao.RoleDAO;
import com.jack.netty.test.security.service.FindResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindResourceImpl implements FindResource {

	@Autowired
	private ResourceDAO rsDao;
	
	@Autowired
	private RoleDAO rDao;
	
	@Override
	public Resource findResourceByID(Long id) {
		// TODO Auto-generated method stub
		return rsDao.selectByPrimaryKey(id);
	}

	@Override
	public Role findRoleByID(Long id){
		// TODO Auto-generated method stub
		return rDao.selectByPrimaryKey(id);
	}
}
