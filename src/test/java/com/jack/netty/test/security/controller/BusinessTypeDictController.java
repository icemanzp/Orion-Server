package com.jack.netty.test.security.controller;


import com.jack.netty.test.security.entity.Resource;
import com.jack.netty.test.security.entity.Role;
import com.jack.netty.test.security.service.FindResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ??????????????????controller
 * 
 * @Class Name BusinessTypeDictController
 * @Author zhangxy
 * @Create In 2015???7???29???
 */
@Controller
@RequestMapping("/test")
public class BusinessTypeDictController{
	
	@Autowired
	private FindResource fr;
	/**
	 * ????????????????????????
	 * 
	 * @Methods Name saveBusinessTypeDict
	 * @Create In 2015???7???29??? By zhangxy
	 * @param para
	 * @param request
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/save", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Map<String, Object> saveBusinessTypeDict(HttpServletRequest request)
			throws Exception {
		Map<String, Object> res = new HashMap<String, Object>();

		Resource resource = fr.findResourceByID(Long.valueOf("2").longValue());
		
		res.put("success", true);
		res.put("data", resource);
		return res;

	}
	
	@RequestMapping(value = "/findRole", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Map<String, Object> findRoleId(HttpServletRequest request){
		Map<String, Object> res = new HashMap<String, Object>();  
		
		Role role = fr.findRoleByID(Long.valueOf("1").longValue());
		
		res.put("success", true);
		res.put("data", role);
		
		return res;
	}
}
