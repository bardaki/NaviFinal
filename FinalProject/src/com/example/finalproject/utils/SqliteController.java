package com.example.finalproject.utils;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteController extends SQLiteOpenHelper { 
	private static final String LOGCAT = null; 
	private SQLiteDatabase sqLiteDatabase;

	public SqliteController(Context applicationcontext) { 
		super(applicationcontext, "androidsqlite.db", null, 1); 
		Log.d(LOGCAT,"Created"); 
	}

	@Override 
	public void onCreate(SQLiteDatabase database) { 
		String query; 
		query = "CREATE TABLE Favorites ( Id INTEGER PRIMARY KEY, Start TEXT, End TEXT, MainId INTEGER)"; 
		database.execSQL(query); 
		query = "CREATE TABLE Places ( Id INTEGER PRIMARY KEY, MainId INTEGER, Place TEXT)"; 
		database.execSQL(query); 
		Log.d(LOGCAT,"Score Created"); 
	}

	@Override 
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) { 
		String query; 
		query = "DROP TABLE IF EXISTS Score"; 
		database.execSQL(query); 
		onCreate(database); 
	}

	public void deleteAll(){
		String query; 
		SQLiteDatabase database = this.getWritableDatabase();
		query = "delete from Favorites"; 
		database.execSQL(query); 
		query = "delete from Places"; 
		database.execSQL(query); 
	}
	
	public void delete(String id){
		String query; 
		SQLiteDatabase database = this.getWritableDatabase();
		query = "delete from Favorites WHERE Id = " + id; 
		database.execSQL(query); 
	}

	public void insertFavorite(HashMap<String, String> queryValues, HashMap<String, String> queryValues2) { 
		SQLiteDatabase database = this.getWritableDatabase(); 
		ContentValues values = new ContentValues(); 
		values.put("Id", queryValues.get("Id"));
		values.put("MainId", queryValues.get("MainId"));
		values.put("Start", queryValues.get("Start")); 
		values.put("End", queryValues.get("End")); 
		database.insert("Favorites", null, values); 
		values = new ContentValues(); 
		for(int i = 0; i < queryValues2.size(); i++){
			values.put("Id", queryValues2.get("Id" + i));
			values.put("MainId", queryValues2.get("MainId" + i));
			values.put("Place", queryValues2.get("Place" + i));
			database.insert("Places", null, values); 
		}		
		database.close(); 
	}

	public ArrayList<HashMap<String, String>> getAllFavorites_startEnd() { 
		ArrayList<HashMap<String, String>> wordList; 
		wordList = new ArrayList<HashMap<String, String>>(); 
		String selectQuery = "SELECT * FROM Favorites"; 
		SQLiteDatabase database = this.getWritableDatabase(); 
		Cursor cursor = database.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) { 
			do { 
				HashMap<String, String> map = new HashMap<String, String>(); 
				map.put("Id", cursor.getString(0)); 
				map.put("Start", cursor.getString(1)); 
				map.put("End", cursor.getString(2));
				map.put("MainId", cursor.getString(3)); 
				wordList.add(map);
			} 
			while (cursor.moveToNext()); 
		} 
		return wordList; 
	}
	
	public ArrayList<HashMap<String, String>> getAllFavorites_addresses(String id) { 
		ArrayList<HashMap<String, String>> wordList; 
		wordList = new ArrayList<HashMap<String, String>>(); 
		String selectQuery = "SELECT * FROM Places WHERE MainId='" + id + "'"; 
		SQLiteDatabase database = this.getWritableDatabase(); 
		Cursor cursor = database.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) { 
			do { 
				HashMap<String, String> map = new HashMap<String, String>(); 
				map.put("Id", cursor.getString(0)); 
				map.put("MainId", cursor.getString(1)); 
				map.put("Place", cursor.getString(2));
				wordList.add(map);
			} 
			while (cursor.moveToNext()); 
		} 
		return wordList; 
	}
}


