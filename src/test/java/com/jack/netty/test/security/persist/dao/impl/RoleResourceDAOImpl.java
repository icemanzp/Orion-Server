package com.jack.netty.test.security.persist.dao.impl;

import com.jack.netty.test.security.entity.RoleResource;
import com.jack.netty.test.security.persist.dao.RoleResourceDAO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;



@Repository
public class RoleResourceDAOImpl implements RoleResourceDAO {

	@Autowired  
    private SqlSessionTemplate sqlSessionTemplate; 
	
    public RoleResourceDAOImpl() {
        super();
    }
    public int deleteByPrimaryKey(Long id) {
        RoleResource _key = new RoleResource();
        _key.setId(id);
        int rows = sqlSessionTemplate.delete("sec_role_resource.deleteByPrimaryKey", _key);
        return rows;
    }

    public void insert(RoleResource record) {
    	sqlSessionTemplate.insert("sec_role_resource.insert", record);
    }

    public void insertSelective(RoleResource record) {
    	sqlSessionTemplate.insert("sec_role_resource.insertSelective", record);
    }

    public RoleResource selectByPrimaryKey(Long id) {
        RoleResource _key = new RoleResource();
        _key.setId(id);
        RoleResource record = (RoleResource)sqlSessionTemplate.selectOne("sec_role_resource.selectByPrimaryKey", _key);
        return record;
    }

    public int updateByPrimaryKeySelective(RoleResource record) {
        int rows = sqlSessionTemplate.update("sec_role_resource.updateByPrimaryKeySelective", record);
        return rows;
    }

    public int updateByPrimaryKey(RoleResource record) {
        int rows = sqlSessionTemplate.update("sec_role_resource.updateByPrimaryKey", record);
        return rows;
    }

}