package com.saicmotor.telematics.tsgp.tsip.otamsghandler.test.servicecatalog;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This program generates a AES key, retrieves its raw bytes, and then
 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
 * to initialize a AES cipher for encryption and decryption.
 */

public class ActiveCodeHandler {

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
	 * @param activeCode
	 *            激活码，16位字符
	 * @param vin
	 *            VIN码，17位字符
	 * @param iccid
	 *            ICCID，20位字符
	 * @param avnsn
	 *            AVN序列号，16位字符
	 * @param tboxsn
	 *            TBOX序列号，10位字符
	 * @param day
	 *            当前日期，，从2012-01-01T00:00:00 UTC开始的天数
	 * @return 0：激活码有效；1：激活码位数不对；2：VIN码位数不对；3：iccid位数不对；4：AVN序列号位数不对；
	 *         5：TBOX序列号不对
	 *         ；6：VIN码倒数第三位不是数字；7：激活码过有效期；8：VIN码最后一位非数字；9：激活码无效；10：MD5出错
	 */
	public static int checkActiveCode(String activeCode, String vin,
			String iccid, String avnsn, String tboxsn, int day) {
		if (activeCode == null || activeCode.length() != 16) {
			return 1;
		}
		if (vin == null || vin.length() != 17) {
			return 2;
		}

		if (iccid == null || iccid.length() != 20) {
			return 3;
		}

		if (avnsn == null || avnsn.length() != 16) {
			return 4;
		}

		if (tboxsn == null || tboxsn.length() != 10) {
			return 5;
		}
		int vinend3 = 0;
		String time = "";
		String A = "";
		try {
			vinend3 = Integer.valueOf(vin.substring(14, 15));
		} catch (NumberFormatException e) {
			return 6;
		}
		switch (vinend3) {
		case 0:
			time = activeCode.substring(2, 3) + activeCode.substring(5, 6)
					+ activeCode.substring(8, 9) + activeCode.substring(9, 10);
			A = activeCode.substring(0, 2) + activeCode.substring(3, 5)
					+ activeCode.substring(6, 8) + activeCode.substring(10, 14);
			break;
		case 1:
			time = activeCode.substring(4, 5) + activeCode.substring(2, 3)
					+ activeCode.substring(5, 6) + activeCode.substring(8, 9);
			A = activeCode.substring(0, 2) + activeCode.substring(3, 4)
					+ activeCode.substring(6, 8) + activeCode.substring(9, 14);
			break;
		case 2:
			time = activeCode.substring(0, 1) + activeCode.substring(4, 5)
					+ activeCode.substring(6, 7) + activeCode.substring(8, 9);
			A = activeCode.substring(1, 4) + activeCode.substring(5, 6)
					+ activeCode.substring(7, 8) + activeCode.substring(9, 14);
			break;
		case 3:
			time = activeCode.substring(5, 6) + activeCode.substring(7, 8)
					+ activeCode.substring(10, 11) + activeCode.substring(1, 2);
			A = activeCode.substring(0, 1) + activeCode.substring(2, 5)
					+ activeCode.substring(6, 7) + activeCode.substring(8, 10)
					+ activeCode.substring(11, 14);
			break;
		case 4:
			time = activeCode.substring(7, 8) + activeCode.substring(9, 10)
					+ activeCode.substring(3, 4) + activeCode.substring(11, 12);
			A = activeCode.substring(0, 3) + activeCode.substring(4, 7)
					+ activeCode.substring(8, 9) + activeCode.substring(10, 11)
					+ activeCode.substring(12, 14);
			break;
		case 5:
			time = activeCode.substring(3, 4) + activeCode.substring(7, 8)
					+ activeCode.substring(9, 10) + activeCode.substring(4, 5);
			A = activeCode.substring(0, 3) + activeCode.substring(5, 7)
					+ activeCode.substring(8, 9) + activeCode.substring(10, 14);
			break;
		case 6:
			time = activeCode.substring(3, 4) + activeCode.substring(6, 7)
					+ activeCode.substring(8, 9) + activeCode.substring(10, 11);
			A = activeCode.substring(0, 3) + activeCode.substring(4, 6)
					+ activeCode.substring(7, 8) + activeCode.substring(9, 10)
					+ activeCode.substring(11, 14);
			break;
		case 7:
			time = activeCode.substring(4, 5) + activeCode.substring(8, 9)
					+ activeCode.substring(2, 3) + activeCode.substring(11, 12);
			A = activeCode.substring(0, 2) + activeCode.substring(3, 4)
					+ activeCode.substring(5, 8) + activeCode.substring(9, 11)
					+ activeCode.substring(12, 14);
			break;
		case 8:
			time = activeCode.substring(8, 9) + activeCode.substring(2, 3)
					+ activeCode.substring(5, 6) + activeCode.substring(0, 1);
			A = activeCode.substring(1, 2) + activeCode.substring(3, 5)
					+ activeCode.substring(6, 8) + activeCode.substring(9, 14);
			break;
		case 9:
			time = activeCode.substring(11, 12) + activeCode.substring(4, 5)
					+ activeCode.substring(6, 7) + activeCode.substring(9, 10);
			A = activeCode.substring(0, 4) + activeCode.substring(5, 6)
					+ activeCode.substring(7, 9) + activeCode.substring(10, 11)
					+ activeCode.substring(12, 14);
			break;
		default:
			return 6;
		}

		int timei = parseHexTime2Int(time);
		if (timei == 99999) {
			return 7;
		}
		if (day - timei < 0 || day - timei > 1) {
			return 7;
		}
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
				return 8;
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
				return 8;
			}

			byte[] ciphertext = encrypt2(plaintext, keys);
			String ciphertextStr = parseByte2HexStr(ciphertext);
			String B = ciphertextStr.substring(vinend * 2, (vinend + 5) * 2);
			if (A.equalsIgnoreCase(B)) {
				return 0;
			}
			return 9;
		} catch (NoSuchAlgorithmException e) {
			return 10;
		}

	}

}
