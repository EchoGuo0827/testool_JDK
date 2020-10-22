package com.example.highplattest.android;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android26.java 
 * Author 			: chencm
 * version 			: 
 * DATE 			: 20180820
 * directory 		: 
 * description 		: Android7.0作用域目录访问
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  chencm	       20180820 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 * @param <DocumentFile>
 ************************************************************************/
public class Android26 extends UnitFragment{
	public final String TAG = Android26.class.getSimpleName();
	private String TESTITEM = "作用域目录访问(A7)";
	private Gui gui = new Gui(myactivity, handler);
	public static final int request_code = 2;
	public static final int REQUEST_SCOPED_PERMISSION = 3;
	private Intent intent;
	private Uri uri = null;
    private static final int OPEN_DIRECTORY_REQUEST_CODE = 1;
    private static final String[] DIRECTORY_SELECTION = new String[]{
        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
    };
    public String fileName;
    public String mimeType;
    
    public void android26()
    {
    	if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
    	{
    		try {
    			testAndroid26();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
    	}
    	else
    	{
    		gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr, "SDK版本低于24，不支持该案例");
    	}
    }
    
    @TargetApi(24)
	private void testAndroid26()
	{
		StorageManager sm = (StorageManager)myactivity.getSystemService(Context.STORAGE_SERVICE);
		StorageVolume volume = sm.getPrimaryStorageVolume();
				
		////打开PICTURES目录uri
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&myactivity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
			myactivity.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    	else
    	{
			gui.cls_show_msg1(gScreenTime, "%s正在访问PICTURES目录URI", TESTITEM);		
			intent = volume.createAccessIntent(Environment.DIRECTORY_PICTURES);
			myactivity.startActivityForResult(intent, REQUEST_SCOPED_PERMISSION); 
			 
			 if(gui.cls_show_msg("PICTURES目录URI，[确认]是，[其他]否")!=ENTER)
				{
				 gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，无法获取PICTURES目录URI", Tools.getLineInfo(), TESTITEM);
					if(!GlobalVariable.isContinue)
						return;	
				}		
		
    	}
		
		//打开DOWNLOADS目录
		gui.cls_show_msg1(gScreenTime, "%s正在访问DOWNLOADS目录", TESTITEM);
    	intent = volume.createAccessIntent(Environment.DIRECTORY_DOWNLOADS);
    	myactivity.startActivityForResult(intent, request_code);
    	 if(gui.cls_show_msg("访问DOWNLOADS目录URI，[确认]是，[其他]否")!=ENTER)
			{
				gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，无法获取DOWNLOADS目录URI", Tools.getLineInfo(), TESTITEM);
				if(!GlobalVariable.isContinue)
					return;	
			}
		
		//打开DCIM目录
		gui.cls_show_msg1(gScreenTime, "%s正在访问DCIM目录", TESTITEM);
		intent = volume.createAccessIntent(Environment.DIRECTORY_DCIM);
    	myactivity.startActivityForResult(intent, request_code);
    	 if(gui.cls_show_msg("访问DCIM目录URI，[确认]是，[其他]否")!=ENTER)
			{
				gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，无法获取DCIM目录URI"+uri.toString(), Tools.getLineInfo(), TESTITEM);
				if(!GlobalVariable.isContinue)
					return;	
			}
		
	 
		//打开MUSIC目录
		gui.cls_show_msg1(gScreenTime, "%s正在访问MUSIC目录", TESTITEM);
		intent = volume.createAccessIntent(Environment.DIRECTORY_MUSIC);
		myactivity.startActivityForResult(intent, request_code);
		if(gui.cls_show_msg("访问MUSIC目录URI，[确认]是，[其他]否")!=ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，无法获取MUSIC目录URI", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;	
		}
	 
		gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"%s测试通过", TESTITEM);
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
		
		// 回调
	        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
	            // 向用户获取权读取内部存储和外部存储的权限
	        	myactivity.getContentResolver().takePersistableUriPermission(data.getData(),
	                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
	            updateDirectoryEntries(data.getData());
	            Log.d("111111", "onActivityResult:Uri= "+data.getData());
	        }
	    }
	   
	   private void updateDirectoryEntries(Uri uri) {

	        // 获取内容获得者
	        ContentResolver contentResolver = myactivity.getContentResolver();

	        // 根据URI和id，建立一个URI代表目标去访问内容提供者
	        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
	                DocumentsContract.getTreeDocumentId(uri));
	        // 访问URI的子目录
	        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri,
	                DocumentsContract.getTreeDocumentId(uri));

	        // 查询URI提供者
	        try (Cursor docCursor = contentResolver
	                .query(docUri, DIRECTORY_SELECTION, null, null, null)) {
	            while (docCursor != null && docCursor.moveToNext()) {
	            
	            	if(gui.cls_show_msg("URI"+docCursor.getString(docCursor.getColumnIndex(
	                        DocumentsContract.Document.COLUMN_DISPLAY_NAME))+"，[确认]是，[其他]否")!=ENTER)
					{
						gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，"+uri, Tools.getLineInfo(), TESTITEM);
						if(!GlobalVariable.isContinue)
							return;	
					}

	            }
	        }

	        // 查询子目录
	        try (Cursor childCursor = contentResolver
	                .query(childrenUri, DIRECTORY_SELECTION, null, null, null)) {
	            while (childCursor != null && childCursor.moveToNext()) {

	                fileName = childCursor.getString(childCursor.getColumnIndex(
	                        DocumentsContract.Document.COLUMN_DISPLAY_NAME));
	                mimeType = childCursor.getString(childCursor.getColumnIndex(
	                        DocumentsContract.Document.COLUMN_MIME_TYPE));
	                if(gui.cls_show_msg("文件和文件类型相关信息"+fileName+"\n"+mimeType+"，[确认]是，[其他]否")!=ENTER)
	        		{
	        			gui.cls_show_msg1_record(TAG, "android26", gKeepTimeErr,"line %d:%s测试不通过，文件和文件类型相关信息异常"+fileName+"\n"+mimeType, Tools.getLineInfo(), TESTITEM);
	        			if(!GlobalVariable.isContinue)
	        				return;	
	        		}
	                Log.e("2222222", "updateDirectoryEntries: "+fileName+"\n"+mimeType);
	            }
	        }
	    }

	@Override
	public void onTestUp() {
		
		
	}

	@Override
	public void onTestDown() {
		
		
	}


}
