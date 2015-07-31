//package com.saicmotor.telematics.tsgp.tsip.autonavi;
//
///**
// * Created by Administrator on 14-10-28.
// */
//public class BitUtil {
//    //原始byte数组
//    private byte[] source;
//    //读取指针，记入读到哪一位bit
//    private int cursor = -1;
//    public BitUtil(byte[] source) {
//        if (source.length == 0) {
//            throw new IllegalArgumentException("source bytes must not be an empty array!");
//        }
//        this.source = source.clone();
//    }
//    public BitUtil(byte sourceByte) {
//        this.source = new byte[]{sourceByte};
//    }
//    /**
//     * 顺序从bit流中读取数据，以01字符串的形式返回结果
//     *
//     * @param length
//     * @return
//     */
//    public String read(int length) {
//        if (length <= 0) {
//            throw new IllegalArgumentException("length must be larger than zero!");
//        }
//        //计算起止bit位，从0开始数
//        int from = cursor + 1;
//        int to = from + length - 1;
//        if (to >= source.length * 8) {
//            to = source.length * 8 - 1;
//        }
//        cursor = to;
//        return read(from, to);
//    }
//    /**
//     * 读取from到to（含）的bit数据
//     *
//     * @param from
//     * @param to
//     * @return
//     */
//    public String read(int from, int to) {
//        if (from < 0) {
//            throw new IllegalArgumentException("'from' must be larger than zero!");
//        }
//        if (to >= source.length * 8) {
//            to = source.length * 8 - 1;
//        }
//        StringBuilder result = new StringBuilder();
//        for (int i = from; i <= to; i++) {
//            byte b = source[i / 8];
//            int m = 7 - i % 8;
//            int op = (int) Math.pow(2, m);
//            int bit = (b & op) / op;
//            result.append(bit);
//        }
//        return result.toString();
//    }
//    public String readAll() {
//        return read(0, source.length * 8);
//    }
//    /**
//     * 重置读写头
//     */
//    public void reset() {
//        this.cursor = -1;
//    }
//
//    public static String getServerProtocol(byte b) {
//        //截取前1个字节里的协议
//        BitUtil reader = new BitUtil(b);
//        String majorVersion = "" + Byte.parseByte("00000" + reader.read(3), 2);
//        String minorVersion = "" + Byte.parseByte("000" + reader.read(3,8), 2);
//        return majorVersion + '.' + minorVersion;
//    }
//
//}
//
