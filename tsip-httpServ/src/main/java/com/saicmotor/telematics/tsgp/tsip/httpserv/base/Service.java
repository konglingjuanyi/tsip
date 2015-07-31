/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base;

import java.lang.reflect.Method;

/**
 * 将需要运行的服务方法和实例封装到Service对象
 */
public class Service {
	
	/**
	 * 服务名称
	 */
	private String name;
	
	/**
	 * 服务实例
	 */
	private Object instance;
	
	/**
	 * 服务方法
	 */
	private Method method;
	
	/**
	 * @return 服务名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 设置服务名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 服务对象构造函数
	 * @param instance 服务实例
	 * @param method 服务方法
	 */
	public Service(Object instance, Method method) {
		super();
		this.instance = instance;
		this.method = method;
	}
	
	/**
	 * 得到服务实例
	 * @return 返回服务实例
	 */
	public Object getInstance() {
		return instance;
	}
	
	/**
	 * 设置服务实例
	 * @param instance 设置服务实例
	 */
	public void setInstance(Object instance) {
		this.instance = instance;
	}
	
	/**
	 * 得到服务方法
	 * @return 服务方法
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * 设置服务方法对象
	 * @param method 服务方法
	 */
	public void setMethod(Method method) {
		this.method = method;
	}
}
