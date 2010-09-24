package ca.christophersaunders.tutorials.sqlite.db;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PicasaAlbumManager {
	
	private Context context;
	private SQLiteDatabase db;
	
	public PicasaAlbumManager(Context context) {
		this.context = context;
		this.db = new DatabaseHelper(this.context).getWritableDatabase();
	}
	
	public class PicasaAlbumTable {
		static final String ALBUM_TABLE = "picasaAlbum";
		static final String ALBUM_TABLE_SQL = "";
		
	}
	
	public class PicasaImageTable {
		static final String IMAGE_TABLE = "picasaImage";
		private static final String _ID = "_id"; // needed for android
		private static final String TITLE = "imageTitle";
		private static final String AUTHOR = "imageAuthor";
		private static final String PUB_DATE = "pubDate";
		private static final String IMAGE_THUMBNAIL = "imageThumbnail";
		private static final String IMAGE = "image";
		
		public static final String IMAGE_TABLE_SQL = 
			"CREATE TABLE " + IMAGE_TABLE +
			"( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TITLE + " STRING," +
				AUTHOR + " STRING," +
				PUB_DATE + " DATE," +
				IMAGE + " BLOB," +
				IMAGE_THUMBNAIL + "BLOB);";		
	}
	
	// The application doesn't need to know how the mapping
	// works between images and albums
	private class ImageAlbumRelationshipTable {
		static final String ALBUM_IMAGE_RELATION = "albumImages";
		
		static final String ALBUM_IMAGE_RELATION_SQL = "";
		
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
			tableNamesAndSQL.put(PicasaAlbumTable.ALBUM_TABLE, PicasaAlbumTable.ALBUM_TABLE_SQL);
			tableNamesAndSQL.put(PicasaImageTable.IMAGE_TABLE, PicasaImageTable.IMAGE_TABLE_SQL);
			tableNamesAndSQL.put(ImageAlbumRelationshipTable.ALBUM_IMAGE_RELATION, ImageAlbumRelationshipTable.ALBUM_IMAGE_RELATION_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("PicasaAlbumManager.DatabaseHelper", "Upgrade required. Migrating from version " + oldVersion + " to " + newVersion + " will destroy existing data!");
			String dropTableSQL = "DROP TABLE IF EXISTS ";
			db.execSQL(dropTableSQL + PicasaAlbumTable.ALBUM_TABLE);
			db.execSQL(dropTableSQL + PicasaImageTable.IMAGE_TABLE);
			db.execSQL(dropTableSQL + ImageAlbumRelationshipTable.ALBUM_IMAGE_RELATION);
			onCreate(db);
			
		}
	}

}
