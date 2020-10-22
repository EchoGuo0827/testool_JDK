package com.example.highplattest.main.tools;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseContext extends ContextWrapper{

	public DatabaseContext(Context base) {
		super(base);
		// TODO Auto-generated constructor stub
	}

	@Override
	public File getDatabasePath(String name) {
		// TODO Auto-generated method stub
		LoggerUtil.e("name:"+name);
//		String dbfile ="/data/share/EpayParameter/" + name;
//		if (!dbfile.endsWith(".db")) {
//			dbfile += ".db";
//		}
		File result = new File(name);
//		if (!result.exists()) {
//			return null;
//		}
		return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		// TODO Auto-generated method stub
		 SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
	                getDatabasePath(name), null);

		return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		// TODO Auto-generated method stub
		 SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
	                getDatabasePath(name), null);
		return result;
	}

}
