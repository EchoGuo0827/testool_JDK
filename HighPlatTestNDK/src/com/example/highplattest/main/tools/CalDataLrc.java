package com.example.highplattest.main.tools;

import android.content.Context;

import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;

public class CalDataLrc 
{
	private static final byte[] STX = new byte[] { 0x02 };

	private static final byte[] SIGNED_SYMBOL = new byte[] { 0x2F };

	private static final byte[] ETX = new byte[] { 0x03 };

	private static final int LEN_STX = STX.length;

	private static final int LEN_SERIAL = 1;

	private static final int LEN_SIGNED_SYMBOL = SIGNED_SYMBOL.length;

	private static final int LEN_CMD = 2;

	private static final int LEN_LENGTH = 2;

	private static final int LEN_LRC = 1;

//	private static final int LEN_RESPCODE = 2;

	private static final int LEN_ETX = ETX.length;

	// 最短长度 = 指令长度 + 指示位长度 + 序列号 + 响应码 + 响应数据域长度
//	private static final int MIN_RESP_LENGTH = LEN_CMD + LEN_SIGNED_SYMBOL
//			+ LEN_SERIAL + LEN_RESPCODE;

	public static byte[] setProCmd(byte[] cmd, byte[] mode,byte[] data) {
//		K21DeviceResponse k21DeviceResponse = null;
		byte[] lenbs = ISOUtils.intToBCD(data.length, LEN_LENGTH * 2, true);

		byte[] body = new byte[1 + LEN_LENGTH + data.length];
		int offsetS = 0;
		// ksn为10
		System.arraycopy(mode, 0, body, offsetS, 1);
		offsetS += 1;
		System.arraycopy(lenbs, 0, body, offsetS, LEN_LENGTH);
		offsetS += LEN_LENGTH;
		System.arraycopy(data, 0, body, offsetS, data.length);

		/** 计算报文体 **/
		byte[] payload = makeupPayload(new byte[] { (byte) (0x01 & 0xff) },
				cmd, body);

		byte[] lrc = caculateLRC(payload);

		/** 拼装请求 **/
		int offset;
		byte[] rslt = new byte[payload.length + LEN_STX + LEN_LRC];
		offset = 0;
		// logger.debug("start pack up request...");
		// logger.debug("pack up stx[" + Dump.getHexDump(STX) + "]");
		System.arraycopy(STX, 0, rslt, 0, STX.length);
		offset += STX.length;

		// logger.debug("pack up payload[" + Dump.getHexDump(payload) + "]");
		System.arraycopy(payload, 0, rslt, offset, payload.length);
		offset += payload.length;

		// logger.debug("pack up lrc[" + Dump.getHexDump(lrc) + "]");
		System.arraycopy(lrc, 0, rslt, offset, LEN_LRC);
		offset += LEN_LRC;

		// logger.debug("end pack up request...[" + Dump.getHexDump(rslt) +
		// "]");
		// 连接K21端
//		K21ControllerManager k21ControllerManager = K21ControllerManager
//				.getInstance(context);
//		try {
//			k21ControllerManager.connect();
//			k21DeviceResponse = k21ControllerManager.sendCmd(
//					new K21DeviceCommand(rslt), null);
//		} catch (ControllerException e) {
//			e.printStackTrace();
//		}
//		k21ControllerManager.close();
//		return k21DeviceResponse;
		return rslt;
	}
	
	public static byte[] mposPack(byte[] cmd, byte[] body) 
	{

		/** 计算报文体 **/
		byte serial = (byte) (Math.random()*255+1);
		byte[] payload = makeupPayload(new byte[] {serial},cmd, body);

		byte[] lrc = caculateLRC(payload);

		/** 拼装请求 **/
		int offset;
		byte[] rslt = new byte[payload.length + LEN_STX + LEN_LRC];
		offset = 0;
		// logger.debug("start pack up request...");
		// logger.debug("pack up stx[" + Dump.getHexDump(STX) + "]");
		System.arraycopy(STX, 0, rslt, 0, STX.length);
		offset += STX.length;

		// logger.debug("pack up payload[" + Dump.getHexDump(payload) + "]");
		System.arraycopy(payload, 0, rslt, offset, payload.length);
		offset += payload.length;

		// logger.debug("pack up lrc[" + Dump.getHexDump(lrc) + "]");
		System.arraycopy(lrc, 0, rslt, offset, LEN_LRC);
		offset += LEN_LRC;
		return rslt;
	}

	/**
	 * 计算lrc
	 * 
	 * @param payload
	 * @return
	 */
	private static byte[] caculateLRC(byte[] payload) {
		int offset = 0;
		byte lrc = payload[0];
		do {
			offset++;
			lrc ^= payload[offset];
		} while (offset < payload.length - 1);

		return new byte[] { lrc };
	}

	/**
	 * 生成数据包体
	 * <p>
	 * 包体将直接参与lrc计算。
	 * 
	 * @param serial
	 * @param cmdcode
	 * @param body
	 * @return
	 */
	private static byte[] makeupPayload(byte[] serial, byte[] cmdcode,
			byte[] body) {

		int offset = 0;
		byte[] payload = new byte[LEN_LENGTH + LEN_CMD + LEN_SIGNED_SYMBOL
				+ LEN_SERIAL + body.length + LEN_ETX];

		int len = LEN_CMD + LEN_SIGNED_SYMBOL + LEN_SERIAL + body.length;
		byte[] lenbs = ISOUtils.intToBCD(len, LEN_LENGTH * 2, true);
		System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
		offset += LEN_LENGTH;

		System.arraycopy(cmdcode, 0, payload, offset, LEN_CMD);
		offset += LEN_CMD;

		System.arraycopy(SIGNED_SYMBOL, 0, payload, offset, LEN_SIGNED_SYMBOL);
		offset += LEN_SIGNED_SYMBOL;

		System.arraycopy(serial, 0, payload, offset, LEN_SERIAL);
		offset += LEN_SERIAL;

		System.arraycopy(body, 0, payload, offset, body.length);
		offset += body.length;

		System.arraycopy(ETX, 0, payload, offset, LEN_ETX);
		return payload;
	}

}
