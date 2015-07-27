package com.saicmotor.telematics.tsgp.tsip.otamsghandler.test.servicecatalog;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */

public class ActiveCode {

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 */
	public static byte[] encrypt2(byte[] content, byte[] password) {

		try {

			SecretKeySpec key = new SecretKeySpec(password, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
			byte[] result = cipher.doFinal(content);
			return result; // 加密
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 将二进制转换成16进制 2. * @param buf 3. * @return 4. */
	public static String parseByte2HexStr(byte buf[]) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/** 将16进制转换为二进制 2. * @param hexStr 3. * @return 4. */
	public static byte[] parseHexStr2Byte(String hexStr) {

		if (hexStr.length() < 1)

			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static int parseHexTime2Int(String hexStr) {

		if (hexStr.length() != 4) {
			return 99999;
		}
		try {
			int a = Integer.parseInt(hexStr.substring(0, 1), 16);
			int b = Integer.parseInt(hexStr.substring(1, 2), 16);
			int c = Integer.parseInt(hexStr.substring(2, 3), 16);
			int d = Integer.parseInt(hexStr.substring(3, 4), 16);
			return (a * 16 * 16 * 16 + b * 16 * 16 + c * 16 + d);
		} catch (NumberFormatException e) {
			return 99999;
		}

	}

	/**
	 * @param vin
	 *            VIN码，17位字符
	 * @param iccid
	 *            ICCID，20位字符
	 * @param avnsn
	 *            AVN序列号，16位字符
	 * @param tboxsn
	 *            TBOX序列号，10位字符
	 *            当前日期，，从2012-01-01T00:00:00 UTC开始的天数
	 * @return String：激活码，16位字符；2：VIN码位数不对；3：iccid位数不对；4：AVN序列号位数不对；
	 *         5：TBOX序列号不对；6：VIN码倒数第三位不是数字；8：VIN码最后一位非数字；10：MD5出错
	 */
	public static String activeCodeCheck(String vin, String iccid,
			String avnsn, String tboxsn) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date beginDate = simpleDateFormat.parse("2012-01-01 00:00:00");
		Date endDate = new Date();
		long millisecond = endDate.getTime() - (beginDate.getTime() + Calendar.getInstance().getTimeZone().getRawOffset());
		int day = (int)(millisecond/24L/60L/60L/1000L);
		
		if (vin == null || vin.length() != 17) {
			return "2";
		}

		if (iccid == null || iccid.length() != 20) {
			return "3";
		}

		if (avnsn == null || avnsn.length() != 16) {
			return "4";
		}

		if (tboxsn == null || tboxsn.length() != 10) {
			return "5";
		}
		int vinend3 = 0;

		String A = "";
		String activeCode = "";
		String time = Integer.toHexString(day);
		if (time.length() == 1) {
			time = "000" + time;
		} else if (time.length() == 2) {
			time = "00" + time;
		} else if (time.length() == 3) {
			time = "0" + time;
		} else {
			time = time.substring(time.length() - 4, time.length());
		}
		time = time.toUpperCase();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");

			byte[] plaintext = md.digest((vin + iccid + avnsn + tboxsn)
					.toUpperCase().getBytes());
			int vinend = 0;

			byte[] keys;

			try {
				vinend = Integer.valueOf(vin.substring(16));
			} catch (NumberFormatException e) {
				return "8";
			}
			switch (vinend) {
			case 0:
				keys = md.digest(vin.toUpperCase().getBytes());
				break;
			case 1:
				keys = md.digest(vin.toUpperCase().getBytes());
				break;
			case 2:
				keys = md.digest(iccid.toUpperCase().getBytes());
				break;
			case 3:
				keys = md.digest(avnsn.toUpperCase().getBytes());
				break;
			case 4:
				keys = md.digest(tboxsn.toUpperCase().getBytes());
				break;
			case 5:
				keys = md.digest(vin.toUpperCase().getBytes());
				break;
			case 6:
				keys = md.digest(iccid.toUpperCase().getBytes());
				break;
			case 7:
				keys = md.digest(iccid.toUpperCase().getBytes());
				break;
			case 8:
				keys = md.digest(avnsn.toUpperCase().getBytes());
				break;
			case 9:
				keys = md.digest(tboxsn.toUpperCase().getBytes());
				break;
			default:
				return "8";
			}

			byte[] ciphertext = encrypt2(plaintext, keys);
			String ciphertextStr = parseByte2HexStr(ciphertext);
			A = ciphertextStr.substring(vinend * 2, (vinend + 5) * 2);

			try {
				vinend3 = Integer.valueOf(vin.substring(14, 15));
			} catch (NumberFormatException e) {
				return "6";
			}
			switch (vinend3) {
			case 0:
				activeCode = A.substring(0, 2) + time.substring(0, 1)
						+ A.substring(2, 4) + time.substring(1, 2)
						+ A.substring(4, 6) + time.substring(2, 3)
						+ time.substring(3, 4) + A.substring(6, 10);
				break;
			case 1:
				activeCode = A.substring(0, 2) + time.substring(1, 2)
						+ A.substring(2, 3) + time.substring(0, 1)
						+ time.substring(2, 3) + A.substring(3, 5)
						+ time.substring(3, 4) + A.substring(5, 10);
				break;
			case 2:
				activeCode = time.substring(0, 1) + A.substring(0, 3)
						+ time.substring(1, 2) + A.substring(3, 4)
						+ time.substring(2, 3) + A.substring(4, 5)
						+ time.substring(3, 4) + A.substring(5, 10);
				break;
			case 3:
				activeCode = A.substring(0, 1) + time.substring(3, 4)
						+ A.substring(1, 4) + time.substring(0, 1)
						+ A.substring(4, 5) + time.substring(1, 2)
						+ A.substring(5, 7) + time.substring(2, 3)
						+ A.substring(7, 10);
				break;
			case 4:
				activeCode = A.substring(0, 3) + time.substring(2, 3)
						+ A.substring(3, 6) + time.substring(0, 1)
						+ A.substring(6, 7) + time.substring(1, 2)
						+ A.substring(7, 8) + time.substring(3, 4)
						+ A.substring(8, 10);
				break;
			case 5:
				activeCode = A.substring(0, 3) + time.substring(0, 1)
						+ time.substring(3, 4) + A.substring(3, 5)
						+ time.substring(1, 2) + A.substring(5, 6)
						+ time.substring(2, 3) + A.substring(6, 10);
				break;
			case 6:
				activeCode = A.substring(0, 3) + time.substring(0, 1)
						+ A.substring(3, 5) + time.substring(1, 2)
						+ A.substring(5, 6) + time.substring(2, 3)
						+ A.substring(6, 7) + time.substring(3, 4)
						+ A.substring(7, 10);
				break;
			case 7:
				activeCode = A.substring(0, 2) + time.substring(2, 3)
						+ A.substring(2, 3) + time.substring(0, 1)
						+ A.substring(3, 6) + time.substring(1, 2)
						+ A.substring(6, 8) + time.substring(3, 4)
						+ A.substring(8, 10);
				break;
			case 8:
				activeCode = time.substring(3, 4) + A.substring(0, 1)
						+ time.substring(1, 2) + A.substring(1, 3)
						+ time.substring(2, 3) + A.substring(3, 5)
						+ time.substring(0, 1) + A.substring(5, 10);
				break;
			case 9:
				activeCode = A.substring(0, 4) + time.substring(1, 2)
						+ A.substring(4, 5) + time.substring(2, 3)
						+ A.substring(5, 7) + time.substring(3, 4)
						+ A.substring(7, 8) + time.substring(0, 1)
						+ A.substring(8, 10);
				break;
			default:
				return "6";
			}

			int r = new Random().nextInt(255);
			String radom = Integer.toHexString(r);
			if (radom.length() == 1) {
				radom = "0" + radom;
			} else {
				radom = radom.substring(radom.length() - 2, radom.length());
			}
			radom = radom.toUpperCase();
			activeCode = activeCode + radom;
			return activeCode;
		} catch (NoSuchAlgorithmException e) {
			return "10";
		}
		

	}
	
//	public static void main(String[] args){
//		
//		
//		String v = activeCodeCheck("LSJW26792CS990006", "98681011261310314413",
//				"AVNSN:AVN4170053", "00C4170053");
//		
//		System.out.println(v+"-----"+v.length());
//	}

}
