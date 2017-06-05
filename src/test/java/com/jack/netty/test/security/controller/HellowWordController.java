/**
 * @Probject Name: netty-wfj-test
 * @Path: com.wfj.controllerHellowWordController.java
 * @Create By Jack
 * @Create In 2015年9月16日 上午11:41:31
 * TODO
 */
package com.jack.netty.test.security.controller;



import com.jack.netty.test.security.entity.Role;
import com.jack.netty.test.security.infc.HelloWorld;
import com.jack.netty.test.security.service.FindResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;


/**
 * @Class Name HellowWordController
 * @Author Jack
 * @Create In 2015年9月16日
 */
@WebService(endpointInterface = "com.jack.netty.test.security.infc.HelloWorld")
@Path("/hw")
public class HellowWordController implements HelloWorld {

	@Autowired
	private FindResource fr;

	/* (non-Javadoc)
	 * @see com.wfj.infc.HelloWorld#sayHi(java.lang.String)
	 */
	@GET
	@Path("/find/role/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Map<String, Object> sayHi(@PathParam("id") String id) {
		// TODO Auto-generated method stub
		System.out.println("sayHi called");
		Map<String, Object> res = new HashMap<String, Object>();

		Role role = fr.findRoleByID(Long.valueOf(id).longValue());

		res.put("success", true);
		res.put("data", role);

        return res;
	}

}
