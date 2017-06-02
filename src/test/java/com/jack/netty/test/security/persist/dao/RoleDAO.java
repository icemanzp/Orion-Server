package com.jack.netty.test.security.persist.dao;

import com.jack.netty.test.security.entity.Role;

public interface RoleDAO {

    int deleteByPrimaryKey(Long id);

    void insert(Role record);

    void insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
}