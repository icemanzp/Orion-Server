package com.jack.netty.test.security.persist.dao.impl;

import com.jack.netty.test.security.entity.Resource;
import com.jack.netty.test.security.persist.dao.ResourceDAO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

;

@Repository
public class ResourceDAOImpl implements ResourceDAO {
	
	@Autowired  
    private SqlSessionTemplate sqlSessionTemplate; 

    public ResourceDAOImpl() {
        super();
    }

    public int deleteByPrimaryKey(Long id) {
        Resource _key = new Resource();
        _key.setId(id);
        int rows = sqlSessionTemplate.delete("sec_resource.deleteByPrimaryKey", _key);
        return rows;
    }

    public Resource selectByPrimaryKey(Long id) {
        return sqlSessionTemplate.selectOne("sec_resource.selectByPrimaryKey", id);
    }

}