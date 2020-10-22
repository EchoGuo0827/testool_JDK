package com.example.highplattest.other;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other10.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180820
 * directory 		: 
 * description 		: 将PDF渲染为位图(Android7.0以上才支持)
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20180820	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other10 extends UnitFragment
{
	public final String TAG = Other10.class.getSimpleName();
	private final String TESTITEM = "PDF渲染为位图";
	private Gui gui = new Gui(myactivity, handler);
	
	@SuppressLint("NewApi") 
	public void other10()
	{
		// case1:将Font1转为位图并显示,都是单页的Pdf
		// case2:将Font2转为位图并显示
		// case3:将Font3转为位图并显示
		String[] pdfPaths = {"/mnt/sdcard/pdf/Font1.pdf","/mnt/sdcard/pdf/Font2.pdf","/mnt/sdcard/pdf/Font3.pdf"};
		gui.cls_show_msg("请先把SVN的pdf文件夹push到sdcard的目录下,已放置过请忽略,任意键继续");
		if(new File("/mnt/sdcard/pdf/").exists()==false)
		{
			gui.cls_printf("测试文件未放置,请先放置测试文件再进入本用例".getBytes());
			return;
		}
		for(String path:pdfPaths)
		{
			gui.cls_printf((path+"文件转换为位图中,任意键继续").getBytes());
			try {
				ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
				PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
				Page mCurrentPage = pdfRenderer.openPage(0);
				final Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);
				
				mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
				// 使用对话框方式在界面上显示bitmap图片
				myactivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						showBitmap(myactivity, bitmap);
					}
				});
				synchronized (g_lock) {
					try {
						g_lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mCurrentPage.close();
				pdfRenderer.close();
				fileDescriptor.close();
				if(gui.cls_show_msg("转换出的位图与原本的PDF文档是否一致,是[确认],否[取消]")==ESC)
				{
					gui.cls_show_msg1_record(TAG, "other10", gKeepTimeErr, "line %d:%s测试失败(path=%s)", Tools.getLineInfo(),TESTITEM,path);
					if(GlobalVariable.isContinue==false)
						return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gui.cls_show_msg1_record(TAG, "other10", gScreenTime, "%s测试通过", TESTITEM);
	}
	
	public void showBitmap(Activity activity, final Bitmap bitmap) 
	{
		ImageView iv = new ImageView(myactivity);
		iv.setImageBitmap(bitmap);
		new BaseDialog(activity, iv, "PDF渲染位图", "完成", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				synchronized (g_lock) {
					g_lock.notify();
				}
			}
		}).show();
		
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
