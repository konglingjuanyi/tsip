///*
// * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
// * This software is published under the terms of the SAIC IT Dept.
// *
// * @Project: Telematics
// */
//
//package com.saicmotor.telematics.tsgp.tsip.httpserv.base;
//
//import com.google.inject.Binder;
//import com.google.inject.Module;
//import com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader.RequestContentReader;
//import com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader.RequestContentReaderRegister;
//import com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer.IResponseWriter;
//import com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer.ResponseWriterRegister;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Modifier;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 处理Json请求的Guice模块
// */
//public class BaseModule extends AbstractBindingModule {
//	private static final Logger LOGGER = LoggerFactory.getLogger(BaseModule.class);
//
//	public BaseModule() {
//		this.addScanPackages("com.saicmotor.telematics.tsgp.tsip.httpserv.base.reader");
//		this.addScanPackages("com.saicmotor.telematics.tsgp.tsip.httpserv.base.writer");
//	}
//
//	public List<Module> getModules() {
//		List<Module> modules = new ArrayList<Module>(0);
//
//		modules.add(new Module() {
//
//			public void configure(Binder binder) {
//
//				// 初始化ResponseWriter
//				ResponseWriterRegister writerRegister = new ResponseWriterRegister();
//				RequestContentReaderRegister readerRegister = new RequestContentReaderRegister();
//
//				// 注册所有服务对象和回调类
//				String mimiType;
//				String[] mimiTypes;
//
//				for (Class clazz : classes) {
//					if (RequestContentReader.class.isAssignableFrom(clazz)
//							&& !clazz.isInterface()
//							&& !Modifier.isAbstract(clazz.getModifiers())) {
//						// 注册RequestContentReader
//						try {
//							RequestContentReader reader = (RequestContentReader) clazz
//									.newInstance();
//
//							readerRegister.registRequestReader(reader.getContentType(),clazz);
//
//						} catch (Exception e) {
//							LOGGER.error("接入模块注册失败:"+clazz.getName(),e);
//						}
//					}else if (IResponseWriter.class.isAssignableFrom(clazz)
//							&& !clazz.isInterface()
//							&& !Modifier.isAbstract(clazz.getModifiers())) {
//						// 注册ResponseWriter
//						try {
//							IResponseWriter writer = (IResponseWriter) clazz
//									.newInstance();
//							mimiType = writer.getMimeType();
//							mimiTypes = mimiType.split(",");
//							for (String mt : mimiTypes) {
//								if (!"".equals(mt.trim())){
//									writerRegister.registResponseWriter(mt
//											.trim(), clazz);
//								}
//							}
//						} catch (Exception e) {
//							LOGGER.error("接入模块注册失败:"+clazz.getName(),e);
//						}
//					}
//				}
//			}
//		});
//
//		return modules;
//	}
//}
