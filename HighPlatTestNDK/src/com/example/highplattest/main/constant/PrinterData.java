package com.example.highplattest.main.constant;

public interface PrinterData {
	//TTF切刀
	public final  String CUT_TEST="*cut\n";
	//TTF走纸
	public final  String FEEDLINE="*feedline p:48\n";
	//TTF单次打印票据
	public final  String DATACOMM_SIGN=
					"*feedline p:48\n"+
					"!NLFONT 3 3 3\n"+
					"!yspace 9\n"+
					"*text l kissbaby面包屋（名城店）\n"+
					"!NLFONT 1 12 3\n"+
					"*line\n"+
					"*text l 商户实收：                   ￥5\n"+
					"*line\n"+
					"*text l 商户实付：                   ￥5\n"+
					"*line\n"+
					"*text l 支付方式：                支付宝\n"+
					"*line\n"+
					"!yspace 6\n"+
					"*text l 交易单号：2017020821001004920238\n" +
					"*text r 392810\n"+
					"*text l 商户名称：kissbaby面包屋（名城店\n"+
					"*text r ）\n"+
					"*text l 设备号：                83902148\n"+
					"*text l 交易时间：   2017/02/08 18:19:32\n"+
					"*text l 交易状态：              付款成功\n"+
					"!yspace 9\n"+
					"*line\n"+
					"!yspace 6\n"+
					"*text l 打印时间：      2017-02-08 18:19\n"+
					"*text l 备注：\n"+
					"*line\n"+
					"!yspace 0\n"+// 将行间距修改为打印初始化值
					"*feedline p:200\n" ;
	//TTF打印票据
	public final String DATACOMM =
					"!NLFONT 3 3 3\n"+
					"!yspace 9\n"+
					"*text l kissbaby面包屋（名城店）\n"+
					"!NLFONT 1 12 3\n"+
					"*line\n"+
					"*text l 商户实收：                   ￥5\n"+
					"*line\n"+
					"*text l 商户实付：                   ￥5\n"+
					"*line\n"+
					"*text l 支付方式：                支付宝\n"+
					"*line\n"+
					"!yspace 6\n"+
					"*text l 交易单号：2017020821001004920238\n" +
					"*text r 392810\n"+
					"*text l 商户名称：kissbaby面包屋（名城店\n"+
					"*text r ）\n"+
					"*text l 设备号：                83902148\n"+
					"*text l 交易时间：   2017/02/08 18:19:32\n"+
					"*text l 交易状态：              付款成功\n"+
					"!yspace 9\n"+
					"*line\n"+
					"!yspace 6\n"+
					"*text l 打印时间：      2017-02-08 18:19\n"+
					"*text l 备注：\n"+
					"*line\n"+
					"!yspace 0\n"+// 将行间距修改为打印初始化值
					"*feedline p:200\n" +
					"*cut\n";
	//TTF单次打印图片
	public final  String DATAPIC_SIGN=
			"*feedline p:48\n" +
			"!NLFONT 9 12\n*text c 以下打印PNG图片\n*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n*line\n" +
			"*feedline p:200\n";
			
	//TTF图片打印
	public final  String DATAPIC=
			"!NLFONT 9 12\n*text c 以下打印PNG图片\n*image l 576*1126 path:/mnt/sdcard/picture/carrefour2.png\n*line\n" +
			"*feedline p:200\n"+
			"*cut\n";
	
	/*
	 * TTF打印使用
	 */
	//汉字
	public final String DATA1=
			"!NLFONT 9 12\n*text c 以下打印汉字样式小字体西方样式大字体居左带下划线\n!hz s\n!asc l\n!gray 5\n!yspace 6\n*underline l aBc34国国国国aBc34\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印汉字样式标准字体,每行16个,居中\n!hz n\n!gray 5\n!yspace 6\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印汉字样式标准字体宽度大字体高度,居右\n!hz nl\n!gray 5\n!yspace 6\n*text r 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n";
	//英文
	public final String DATA2=
			"!NLFONT 9 12\n*text c 以下打印西方样式标准字体,每行32个,居中\n!asc n\n!gray 5\n!yspace 6\n*text c sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印西方样式大字体,每行24个,居右\n!asc l\n!gray 5\n!yspace 6\n*text r sssssssssssssssssssssssssssssssssssssssssssssssssssssss\n*line\n*feedline 1\n";
	// 分隔符
	public final String DATA3 =
			"!hz n\n!asc n\n!gray 7\n!yspace 6\n*text c 以下打印两条分隔符\n*line\n*line\n*feedline 1\n";
	// 条形码
	public final String DATA4=
			"!NLFONT 9 12\n*text c 以下打印条形码(宽度1高度64居中,纯数字50个),不要求能够扫描出来\n!barcode 1 64\n*barcode c 01234567890123456789012345678901234567890123456789\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的EAN-8条形码内容是否为:12345670，并且条码下方【无】显示条码信息\n!BARCODE 6 120 0 4\n*BARCODE c 1234567\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印条形码(宽度6高度120居中)请扫描打印出的UPC-E条形码内容是否为:11234562，并且条码下方【有】显示条码信息\n!BARCODE 6 120 1 7\n*BARCODE c 1123456\n*line\n*feedline 1\n";
	// 二维码
	public final String DATA5 =
			"!NLFONT 9 12\n*text c 以下打印二维码(高度384居右)请扫描打印出的二维码内容是否为:ABC123456789DEFGH\n!qrcode 384 3\n*qrcode r ABC123456789DEFGH\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印二维码(高度200居中)请扫描打印出的二维码内容是否为:QRCODE测试支持中文好\n!QRCODE 200 2 3\n*QRCODE c QRCODE测试支持中文好\n*line\n*feedline 1\n";
	// 灰度7
	 public final String DATA6 = "!hz n\n!gray 7\n*text c 本行字灰度为7\n*line\n*feedline 1\n";
	 //间距
	 public final String DATA7="!NLFONT 9 12\n*text c 以下打印汉字行间距为40\n!hz n\n!yspace 40\n*text c 国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n*line\n*feedline 1\n";
	 //特有
	 public final String DATA8="!NLFONT 9 12 3\n*UNDERLINE l 本行文字带下划线\n*underline r 并且不换行Test\n";
	 //图片
	 public final String DATA9=
			"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居左),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image l 384*300 path:/mnt/sdcard/picture/color1.png\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居中),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image c 384*300 path:/mnt/sdcard/picture/color2.png\n*line\n*feedline 1\n"+
			"!NLFONT 9 12\n*text c 以下打印PNG图片,阈值使用大津算法(居右),效果请对照实际图片,彩色图片浅颜色将打印成白色,深颜色将打印成黑色\n*image r 384*300 path:/mnt/sdcard/picture/color3.png\n*line\n*feedline 1\n";
	 //家乐福图片
	 public final String DATA10=
			 "!NLFONT 9 12\n*text c 以下打印PNG图片\n*image l 576*961 path:/mnt/sdcard/picture/carrefour1.png\n*line\n*feedline 1\n"+
			 "!NLFONT 9 12\n*text c 以下打印PNG图片\n*image l 576*1126 path:/mnt/sdcard/picture/carrefour2.png\n*line\n*feedline 1\n";

}
