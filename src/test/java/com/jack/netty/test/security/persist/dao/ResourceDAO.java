package com.jack.netty.test.security.persist.dao;

import com.jack.netty.test.security.entity.Resource;

public interface ResourceDAO {

    int deleteByPrimaryKey(Long id);

    Resource selectByPrimaryKey(Long id);

}