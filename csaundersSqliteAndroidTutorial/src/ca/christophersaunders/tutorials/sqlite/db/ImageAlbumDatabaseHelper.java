/*******************************************************************************
 * Copyright (c) 2010 - Christopher Saunders
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package ca.christophersaunders.tutorials.sqlite.db;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ImageAlbumDatabaseHelper {

	private Context context;
	private static SQLiteDatabase db;
	
	public ImageAlbumDatabaseHelper(Context context) {
		this.context = context;
		if(db == null || !db.isOpen()) {
			db = new DatabaseHelper(this.context).getWritableDatabase();
		}
	}
	
	private class DatabaseHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE = "picasa_albums.sqlite";
		private static final int VERSION = 1;

		private HashMap<String, String> tableNamesAndSQL = new HashMap<String, String>();
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			populateTableNamesAndSQL(); // This would be done with a dictionary/hash in a language such as python
			String formatString = "SELECT name FROM sqlite_master WHERE type='table' AND name='%s'";
			for(String tableName : tableNamesAndSQL.keySet()) {
				Cursor c = db.rawQuery(String.format(formatString, tableName), null);
				try {
					if(c.getCount() == 0) {
						// No table exists, create a new one
						db.execSQL(tableNamesAndSQL.get(tableName));
					}
				} finally {
					c.close();
				}
			}
		}
		
		private void populateTableNamesAndSQL() {
			tableNamesAndSQL.put(PicasaAlbumManager.ALBUM_TABLE, PicasaAlbumManager.CREATE_ALBUM_TABLE_SQL);
			tableNamesAndSQL.put(PicasaImageManager.IMAGE_TABLE, PicasaImageManager.CREATE_IMAGE_TABLE_SQL);
			tableNamesAndSQL.put(ImageAlbumRelationshipManager.IMAGE_ALBUM_RELATION, ImageAlbumRelationshipManager.CREATE_ALBUM_IMAGE_RELATION_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("PicasaAlbumManager.DatabaseHelper", "Upgrade required. Migrating from version " + oldVersion + " to " + newVersion + " will destroy existing data!");
			String dropTableSQL = "DROP TABLE IF EXISTS ";
			db.execSQL(dropTableSQL + PicasaAlbumManager.ALBUM_TABLE);
			db.execSQL(dropTableSQL + PicasaImageManager.IMAGE_TABLE);
			db.execSQL(dropTableSQL + ImageAlbumRelationshipManager.IMAGE_ALBUM_RELATION);
			onCreate(db);
			
		}
	}

}
