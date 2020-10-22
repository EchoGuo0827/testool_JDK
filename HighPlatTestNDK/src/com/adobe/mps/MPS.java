package com.adobe.mps;

import java.nio.ByteBuffer;
import com.example.highplattest.main.bean.PaperInfo;
import android.content.Context;
import android.util.Log;

/**
 * ＊ 广州创自信息科技有限公司
 * Created by kavinwang on 16/9/20.
 */
public class MPS {

	private final String TAG = this.getClass().getName();
    private static final MPS instance = new MPS();

    static {
        try {
            System.loadLibrary("APC");
            System.loadLibrary("MPS");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static MPS getInstance() {
        return MPS.instance;
    }

    private MPS() {
    }

    public native int ImagetoPDF(Object[] arg1, String toFilePath);

    public native int MPSInit();

    public native int MPSTerm() ;

    public native int PDFDocInit(String filePath) ; //return totalPages

    public native int PDFDocInit(String filePath, String password);

    public native int[] PDFGetPageAttributes(int pageNo);

    public native int PDFPageRender(int pageNo, int width, int height, int type, ByteBuffer rawBuffer, int rawBufferOffset,int[] colorSpace) ;

    public native int PDFPagetoImage(int pageNo, String bmpPath, int width, int height, int type) ;

    public native int PDFtoImages(String arg1, String arg2, int arg3, int arg4, int arg5, boolean arg6) ;


    /**
     * 将PDF文件转换为JPG图片
     * @param context
     * @param pdfFilePath
     * @param pageNo
     * @param dir
     * @return
     * @throws Exception
     */
    public void PDFtoJPG(Context context, String pdfFilePath, int pageNo, String dir) throws Exception{
        String jpgPath = null;
         if(context != null) {
             //此处必须每次转换前都要初始化，否则直接崩溃
             if (this.MPSInit() != 0) throw new Exception("转换PDF初始化错误！");

             PaperInfo paperInfo = new PaperInfo(2580, 3643, 2496, 3559, -36, -42, 2652, 3755);

             int pageWidth = paperInfo.getPaperWidthBorder();
             int pageHeight = paperInfo.getPaperHeightBorder();
             // 弄成横屏幕显示
             if(pageWidth < pageHeight){
                 pageHeight = paperInfo.getPaperWidthBorder();
                 pageWidth = paperInfo.getPaperHeightBorder();
             }
             //返回有几页的pdf文档
             if (this.PDFDocInit(pdfFilePath) > 0) {
            	 // 要转的图片路径与文件格式
                 jpgPath = dir + "/"+ pdfFilePath.substring(pdfFilePath.lastIndexOf('/') + 1,pdfFilePath.lastIndexOf('.')) + "_" + pageNo + ".jpg";
                try {
                    //由于原pdf不是标准A5比例小，因此强制进行A5比例转换！
                	int nRet = -1;
                	nRet = this.PDFPagetoImage(pageNo, jpgPath, pageWidth, pageHeight, 2);
                	Log.e(TAG, nRet+"");
                    if (nRet > 0) throw new Exception();
                }finally {
                    this.MPSTerm();
                }
             }
         }
    }
    
    
    /**
     * 将PDF文件转换为PNG图片
     * @param context
     * @param pdfFilePath
     * @param pageNo
     * @param dir
     * @return
     * @throws Exception
     */
    public void PDFtoPNG(Context context, String pdfFilePath, int pageNo, String dir) throws Exception{
        String jpgPath = null;
         if(context != null) {
             //此处必须每次转换前都要初始化，否则直接崩溃
             if (this.MPSInit() != 0) throw new Exception("转换PDF初始化错误！");

             PaperInfo paperInfo = new PaperInfo(2580, 3643, 2496, 3559, -36, -42, 2652, 3755);

             int pageWidth = paperInfo.getPaperWidthBorder();
             int pageHeight = paperInfo.getPaperHeightBorder();
             // 弄成横屏幕显示
             if(pageWidth < pageHeight){
                 pageHeight = paperInfo.getPaperWidthBorder();
                 pageWidth = paperInfo.getPaperHeightBorder();
             }
             //返回有几页的pdf文档
             if (this.PDFDocInit(pdfFilePath) > 0) {
            	 // 要转的图片路径与文件格式
                 jpgPath = dir + "/"+ pdfFilePath.substring(pdfFilePath.lastIndexOf('/') + 1,pdfFilePath.lastIndexOf('.')) + "_" + pageNo + ".png";
                try {
                    //由于原pdf不是标准A5比例小，因此强制进行A5比例转换！
                	int nRet = -1;
                	nRet = this.PDFPagetoImage(pageNo, jpgPath, pageWidth, pageHeight, 2);
                	Log.e(TAG, nRet+"");
                    if (nRet > 0) throw new Exception();
                }finally {
                    this.MPSTerm();
                }
             }
         }
    }


    /**
     * PDF文件转为BMP格式
     * @param context
     * @param pdfFilePath
     * @param dir
     * @return
     * @throws Exception
     */
    public void PDFtoBMP(Context context, String pdfFilePath, String dir)throws Exception 
    {
        int pageBoderHeight;
//        ArrayList bmps = new ArrayList();
        if(context != null) {
            if(this.MPSInit() != 0) throw new Exception();

           // Info_paper v12 = new PaperSize_constants().getStringId(context.getSharedPreferences("PrintSetting", 0).getInt("PAPER_SIZE", 0));
            int pageBoderWidth = 2100;//v12.getPaper_width_boder();
            //v12.getPaper_height_boder();

            int pages = this.PDFDocInit(pdfFilePath);
            try{
                if(pages > 0) {
                    for(int v11 = 0; v11 < pages; ++v11) {
//                        String v4 = String.valueOf(dir) +"/"+ pdfFilePath.substring(pdfFilePath.lastIndexOf(47)) + "_" + (v11 + 1) + ".bmp";
                    	String v4 = dir +"/"+ pdfFilePath.substring(pdfFilePath.lastIndexOf('/') + 1,pdfFilePath.lastIndexOf('.')) + "_" + (v11 + 1) + ".bmp";
                        int[] v9 = this.PDFGetPageAttributes(v11 + 1);
                        if(v9[0] > v9[1]) {
                            pageBoderHeight = pageBoderWidth;
                            pageBoderWidth = pageBoderHeight  * v9[0] /  v9[1];
                        } else {
                            pageBoderHeight = pageBoderWidth * v9[1] / v9[0];
                        }
                        int nRet = -1;
                        nRet = this.PDFPagetoImage(v11 + 1, v4, pageBoderWidth, pageBoderHeight,2);
                        if (nRet > 0) throw new Exception();
//                        bmps.add(v4);
                    }
                }
            }finally {
                this.MPSTerm();
            }
        }
    }


    public int getPageNum(String pdfFilePath) {
        int pages = 0;
        try {
            if(this.MPSInit() != 0)  throw new Exception();
            pages = this.PDFDocInit(pdfFilePath);
            this.MPSTerm();
        }
        catch(Exception v0) {
            v0.printStackTrace();
        }

        return pages;
    }
}

