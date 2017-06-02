/**
 * @Probject Name: netty-wfj-test
 * @Path: com.wfj.controllerHellowWordController.java
 * @Create By Jack
 * @Create In 2015年9月16日 上午11:41:31
 * TODO
 */
package com.jack.netty.test.security.controller;



import com.jack.netty.test.security.infc.HelloWorld;

import javax.jws.WebService;


/**
 * @Class Name HellowWordController
 * @Author Jack
 * @Create In 2015年9月16日
 */
@WebService(endpointInterface = "com.jack.netty.test.security.infc.HelloWorld")
public class HellowWordController implements HelloWorld {

	/* (non-Javadoc)
	 * @see com.wfj.infc.HelloWorld#sayHi(java.lang.String)
	 */
	public String sayHi(String text) {
		// TODO Auto-generated method stub
		System.out.println("sayHi called");
        return "Hello " + text;
	}

}
