package com.jack.netty.test.security.persist.dao.impl;

import com.jack.netty.test.security.entity.Role;
import com.jack.netty.test.security.persist.dao.RoleDAO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class RoleDAOImpl implements RoleDAO {

	@Autowired  
    private SqlSessionTemplate sqlSessionTemplate; 
	
    public RoleDAOImpl() {
        super();
    }

    public int deleteByPrimaryKey(Long id) {
        Role _key = new Role();
        _key.setId(id);
        int rows = sqlSessionTemplate.delete("sec_role.deleteByPrimaryKey", _key);
        return rows;
    }

    public void insert(Role record) {
    		sqlSessionTemplate.insert("sec_role.insert", record);
    }

    public void insertSelective(Role record) {
    		sqlSessionTemplate.insert("sec_role.insertSelective", record);
    }

    public Role selectByPrimaryKey(Long id) {
        Role _key = new Role();
        _key.setId(id);
        Role record = (Role) sqlSessionTemplate.selectOne("sec_role.selectByPrimaryKey", id);
        return record;
    }

    public int updateByPrimaryKeySelective(Role record) {
        int rows = sqlSessionTemplate.update("sec_role.updateByPrimaryKeySelective", record);
        return rows;
    }

    public int updateByPrimaryKey(Role record) {
        int rows = sqlSessionTemplate.update("sec_role.updateByPrimaryKey", record);
        return rows;
    }
}