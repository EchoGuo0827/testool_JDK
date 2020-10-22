package com.example.highplattest.main.constant;

public interface Mifare_1 {
	final int LEN_UID = 10;
	final int MAXLEN_DATA = 0xff;
	final int SEQNR = 0;
	final int LEN_BLKDATA = 16;
	
	final byte AUTHKEY_TYPE_A = 0x60;
	final byte AUTHKEY_TYPE_B = 0x61;
	final byte[] AUTHKEY = {(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff};
	final byte[] BLK02DATA_ORI={0x67,0x45,0x23,0x01,(byte) 0x98,(byte) 0xba,(byte) 0xdc,
			(byte) 0xfe,0x67,0x45,0x23,0x01,0x02,(byte) 0xfd,0x02,(byte) 0xfd};

}
