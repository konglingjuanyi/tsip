package com.saicmotor.telematics.tsgp.tsip.autonavi;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides the readLine method of a BufferedReader with no no automatic
 * buffering.  All methods are like those in InputStream except they return
 * -1 instead of throwing IOException.
 *
 * This also catches ArrayIndexOutOfBoundsExceptions while reading, as this
 * exception can be thrown from native socket code on windows occasionally.
 * The exception is treated exactly like an IOException.
 */
public class ByteUtil {

    private static final byte R = '\r';
    private static final byte N = '\n';

    private InputStream _istream;

    public ByteUtil(InputStream stream) {
        _istream = stream;
    }

    public void close() {
        try {
            _istream.close();
        } catch (IOException ignored) {
        }
    }

    public int read() {
        int c = -1;
        if (_istream == null)
            return c;
        try {
            c =  _istream.read();
        } catch(IOException ignored) {
            // return -1
        } catch(ArrayIndexOutOfBoundsException ignored) {
            // return -1
        }
        return c;
    }

    public int read(byte[] buf) {
        int c = -1;
        if (_istream == null) {
            return c;
        }
        try {
            c = _istream.read(buf);
        } catch(IOException ignored) {
            // return -1
        } catch(ArrayIndexOutOfBoundsException ignored) {
            // return -1
        }
        return c;
    }

    public int read(byte[] buf, int offset, int length) {
        int c = -1;
        if (_istream == null) {
            return c;
        }
        try {
            c = _istream.read(buf, offset, length);
        } catch(IOException ignored) {
            // return -1
        } catch(ArrayIndexOutOfBoundsException ignored) {
            // happens on windows machines occasionally.
            // return -1
        }
        return c;
    }

    /**
     * Reads a new line WITHOUT end of line characters.  A line is
     * defined as a minimal sequence of character ending with "\n", with
     * all "\r"'s thrown away.  Hence calling readLine on a stream
     * containing "abc\r\n" or "a\rbc\n" will return "abc".
     *
     * Throws IOException if there is an IO error.  Returns null if
     * there are no more lines to read, i.e., EOF has been reached.
     * Note that calling readLine on "ab<EOF>" returns null.
     */
    public String readLine() throws IOException {
        if (_istream == null)
            return "";
        StringBuilder sBuffer = new StringBuilder();
        int c = -1; //the character just read
        boolean keepReading = true;
        do {
            try {
                c = _istream.read();
            } catch(ArrayIndexOutOfBoundsException aiooe) {
                // this is apparently thrown under strange circumstances.
                // interpret as an IOException.
                throw new IOException("aiooe.");
            }
            switch(c) {
                // if this was a \n character, break out of the reading loop
                case  N: keepReading = false;
                    break;
                // if this was a \r character, ignore it.
                case  R: continue;
                    // if we reached an EOF ...
                case -1: return null;
                // if it was any other character, append it to the buffer.
                default: sBuffer.append((char)c);
            }
        } while(keepReading);
        // return the string we have read.
        return sBuffer.toString();
    }

    /**
     * 工具添加
     * @param bytes
     * @return
     */
    public static int bytesToInt16(byte[] bytes,int offset){
        int value;
        value = (int)(((bytes[offset + 0] << 8) & 0xff00)
                | ((bytes[offset + 1] & 0xff)));
        return value;
    }

    public static byte[] int16ToBytes(int i){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (i >> 8);
        bytes[1] = (byte)i;
        return bytes;
    }

    static long byteToLong(byte[] array,int offset ){
        return ((((long) array[offset + 0] & 0xff) << 56)
                | (((long) array[offset + 1] & 0xff) << 48)
                | (((long) array[offset + 2] & 0xff) << 40)
                | (((long) array[offset + 3] & 0xff) << 32)
                | (((long) array[offset + 4] & 0xff) << 24)
                | (((long) array[offset + 5] & 0xff) << 16)
                | (((long) array[offset + 6] & 0xff) << 8)
                | (((long) array[offset + 7] & 0xff) << 0));
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString
     *			16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStr2ByteArray(String hexString) {
        if (null == hexString || "".equals(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            //将hex 转换成byte   "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
}

