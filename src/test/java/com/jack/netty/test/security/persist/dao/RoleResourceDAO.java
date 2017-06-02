package com.jack.netty.test.security.persist.dao;

import com.jack.netty.test.security.entity.RoleResource;

public interface RoleResourceDAO {

    int deleteByPrimaryKey(Long id);

    void insert(RoleResource record);

    void insertSelective(RoleResource record);

    RoleResource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleResource record);

    int updateByPrimaryKey(RoleResource record);
}