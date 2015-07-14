/*
 * Copyright (c) 2007-2013 SAIC. All Rights Reserved.
 * This software is published under the terms of the SAIC IT Dept.
 *
 * @Project: Telematics
 */
package com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper;

import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.BinaryAndHexUtil;
import com.saicmotor.telematics.tsgp.otaadapter.asn.utils.IntByteConvertor;
import org.apache.commons.lang.ArrayUtils;

/**
 * 从byte数组读取bit的工具
 * @author zhuxiaoyan
 */
public class BitReader {
    //原始byte数组
    private byte[] source;

    //读取指针，记入读到哪一位bit
    private int cursor = -1;

    public BitReader(byte[] source) {
        if (source.length == 0) {
            throw new IllegalArgumentException("source bytes must not be an empty array!");
        }
        this.source = ArrayUtils.clone(source);
    }

    public BitReader(byte sourceByte) {
        this.source = new byte[]{sourceByte};
    }

    /**
     * 顺序从bit流中读取数据，以01字符串的形式返回结果
     *
     * @param length
     * @return
     */
    public String read(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be larger than zero!");
        }

        //计算起止bit位，从0开始数
        int from = cursor + 1;
        int to = from + length - 1;

        if (to >= source.length * 8) {
            to = source.length * 8 - 1;
        }

        cursor = to;

        return read(from, to);
    }

    /**
     * 读取from到to（含）的bit数据
     *
     * @param from
     * @param to
     * @return
     */
    public String read(int from, int to) {
        if (from < 0) {
            throw new IllegalArgumentException("'from' must be larger than zero!");
        }

        if (to >= source.length * 8) {
            to = source.length * 8 - 1;
        }

        StringBuilder result = new StringBuilder();

        for (int i = from; i <= to; i++) {
            byte b = source[i / 8];

            int m = 7 - i % 8;

            int op = (int) Math.pow(2, m);

            int bit = (b & op) / op;

            result.append(bit);
        }

        return result.toString();
    }

    public String readAll() {
        return read(0, source.length * 8);
    }

    /**
     * 重置读写头
     */
    public void reset() {
        this.cursor = -1;
    }

    /**
     * 从OTA source string 中获取协议版本
     * @param otaSourceString
     * @return
     */
    public static String getClientProtocol(String otaSourceString) {
        //去掉4个长度字节和一个flag字节
        byte[] bs = BinaryAndHexUtil.hexStringToByte(otaSourceString.substring(5));
        //截取前1个字节里的协议
        BitReader reader = new BitReader(bs);
        String majorVersion = "" + Byte.parseByte("0000" + reader.read(4), 2);
        String minorVersion = "" + Byte.parseByte("0000" + reader.read(4), 2);
        return majorVersion + '.' + minorVersion;
    }


    /**
     * 转换协议字符串到Integer类型
     * @param version X.X形式的协议字符串
     * @return
     */
    public static Integer toIntegerProtocol(String version) {
        String[] ar = version.split("\\.");
        Byte majorVersion = Byte.parseByte(ar[0], 10);
        Byte minorVersion = Byte.parseByte(ar[1], 10);
        return (int)(majorVersion << 4 | minorVersion);
    }

    /**
     * 转换int形式的协议版本为X.X形式的字符串
     * @param version
     * @return
     */
    public static String toStringProtocol(int version) {
        BitReader reader = new BitReader((byte)version);
        String majorVersion = "" + Byte.parseByte("0000" + reader.read(4), 2);
        String minorVersion = "" + Byte.parseByte("0000" + reader.read(4), 2);
        return (majorVersion + '.' + minorVersion);
    }

    /**
     * 把int类型的app软件版本转换为x.x形式的字符串
     * @param appVersion
     * @return
     */
    public static String toStringAppVersion(int appVersion) {
        byte[] bytes = IntByteConvertor.intTo2Byte(appVersion);
        String majorVersion = "" + (int)bytes[0];
        String minorVersion = "" + (int)bytes[1];
        return (majorVersion + '.' + minorVersion);
    }

    /**
     * 把x.x形式的字符串转换成int类型的app软件版本
     * @param appVersion
     * @return
     */
    public static Integer toIntAppVersion(String appVersion) {
        String[] ar = appVersion.split("\\.");
        Byte majorVersion = Byte.parseByte(ar[0], 10);
        Byte minorVersion = Byte.parseByte(ar[1], 10);
        return (majorVersion << 8 | minorVersion);
    }

    public static void main(String[] aaa) {
        //10110101 =>> 10110100 ->> -1001011
        byte[] byteArray = new byte[]{Byte.parseByte("-1001011", 2), Byte.parseByte("01011010", 2), Byte.parseByte("01111010", 2)};

        BitReader br = new BitReader(byteArray);

        assert "10110101".equals(br.read(8));
        assert "01011010".equals(br.read(8));
        assert "01111010".equals(br.read(8));
        assert "101101010101101001111010".equals(br.read(0, 23));
        assert "101101010101101001111010".equals(br.readAll());
        br.reset();
        assert "101101010101101001111010".equals(br.read(24));

        assert 17 == toIntegerProtocol("1.1");

        System.out.println(toIntAppVersion("2.0"));
    }
}
