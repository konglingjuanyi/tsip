/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */

package com.saicmotor.telematics.tsgp.tsip.httpserv.base.provider;


import com.google.inject.Provider;
import com.saicmotor.telematics.framework.core.common.ModelMap;
import com.saicmotor.telematics.tsgp.tsip.httpserv.base.ContextManager;

/**
 * ModelMap的提供者类
 */
public class ModelMapProvider implements Provider<ModelMap> {
	public ModelMap get() {
		return ContextManager.getModelMap();
	}
}
