package com.jack.netty.test.security.service;


import com.jack.netty.test.security.entity.Resource;
import com.jack.netty.test.security.entity.Role;

public interface FindResource {
	
	public Resource findResourceByID(Long id);
	
	public Role findRoleByID(Long id);
}