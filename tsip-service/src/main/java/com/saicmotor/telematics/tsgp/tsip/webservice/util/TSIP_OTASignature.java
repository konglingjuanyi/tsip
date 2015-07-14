/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.webservice.util;


import com.saicmotor.telematics.tsgp.tsip.otamsghandler.exception.TSIPException;

import java.security.MessageDigest;

/**
 * MD5签名类,负责消息的签名和验证
 */
public class TSIP_OTASignature {
	/**
	 * 签名长度
	 */
	public static final int LENGTH=16;
	
	/**
	 * 默认使用MD5算法
	 */
	private String _algorithm = "MD5";

	/**
	 * 设置签名算法
	 * @param algorithm
	 */
	public void setAlgorithm(String algorithm) {
		if (algorithm == null)
			throw new NullPointerException();

		_algorithm = algorithm;
	}

	/**
	 * 得到签名算法
	 * @return 签名算法名称
	 */
	public String getAlgorithm() {
		return _algorithm;
	}

	/**
	 * 对byte数组格式的数据进行签名	
	 * @param in 输入数据
	 * @return 签名信息
	 */
	public byte[] digest(byte[] in){
		try{
			MessageDigest digest = MessageDigest  
	        	.getInstance(this._algorithm); 
			return digest.digest(in);
		}catch(Exception e){
			throw new TSIPException("对数据签名时出错",e);
		}
	}

	/**
	 * 使用签名信息对输入数据进行验证
	 * @param in 输入数据
	 * @param signature 签名信息
	 * @return 是否验证成功
	 */
	public boolean check(byte[] in,byte[] signature){
		try{
			MessageDigest digest = MessageDigest  
	    	.getInstance(this._algorithm); 
			byte[] out =digest.digest(in);
			if(out.equals(signature)){
				return true;
			}
			return false;
		}catch(Exception e){
			throw new TSIPException("对数据签名时出错",e);
		}
	}

}
