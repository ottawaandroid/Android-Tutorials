package ca.christophersaunders.tutorials.sqlite.db;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaAlbum;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaImage;

public class PicasaAlbumManager {
	
	private Context context;
	private static SQLiteDatabase db;
	
	public PicasaAlbumManager(Context context) {
		this.context = context;
		if(db == null || !db.isOpen()) {
			db = new DatabaseHelper(this.context).getWritableDatabase();
		}
	}
	
	public static class PicasaAlbumTable {
		static final String ALBUM_TABLE = "picasaAlbum";
		
		static final String _ID = "_ID";
		static final String TITLE = "albumTitle";
		static final String AUTHOR = "albumAuthor";
		static final String PUB_DATE = "createdDate";
		
		static final String CREATE_ALBUM_TABLE_SQL = 
			"CREATE TABLE " + ALBUM_TABLE +
			" (" + _ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
				TITLE + " STRING UNIQUE," +
				AUTHOR + " STRING," +
				PUB_DATE + "DATE)";
		
		/**
		 * 
		 * @return the primary key for the stored album or
		 * -1 if it failed
		 */
		protected long addAlbum(PicasaAlbum album) {
			ContentValues values = new ContentValues();
			
			values.put(TITLE, album.getTitle());
			values.put(AUTHOR, album.getAuthor());
			values.put(PUB_DATE, album.getCreatedDate().toGMTString());
			
			long albumId =  db.insert(ALBUM_TABLE, TITLE, values);
			if(albumId > 0 && album.getAlbumImages().size() > 0) {
				// Storing the album was successful and this album has images associated with it
				PicasaImageTable imageTable = new PicasaImageTable();
				ImageAlbumRelationshipTable imageAlbumRelationships = new ImageAlbumRelationshipTable();
				for(PicasaImage image : album.getAlbumImages()) {
					long imageId = imageTable.addOrUpdateImage(image);
					boolean storedNewRelation = imageAlbumRelationships.addImageAlbumRelation(albumId, imageId);
					if(!storedNewRelation) {
						Log.e("ca.christopersaunders.tutorials.sqlite.db.PicasaAlbumManager", "Could not store relation for album: " + album.getTitle() + " and image: " + image.getThumbnailLocation());
						Log.e("ca.christopersaunders.tutorials.sqlite.db.PicasaAlbumManager", "Aborting...");
						System.exit(1);
					}
				}
			}
			
			return albumId;
		}
		
		public PicasaAlbum getAlbumByName(String albumName) {
			return null;
		}
		
		public PicasaAlbum getAlbumById(long id) {
			return null;
		}
		
		public long getIdForAlbum(PicasaAlbum album) {
			return -1;
		}
		
	}
	
	public static class PicasaImageTable {
		static final String IMAGE_TABLE = "picasaImage";
		static final String _ID = "_id"; // needed for android
		static final String TITLE = "imageTitle";
		static final String AUTHOR = "imageAuthor";
		static final String PUB_DATE = "pubDate";
		static final String IMAGE_THUMBNAIL = "imageThumbnail";
		static final String IMAGE = "image";
		
		public static final String CREATE_IMAGE_TABLE_SQL = 
			"CREATE TABLE " + IMAGE_TABLE +
			" ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TITLE + " STRING," +
				AUTHOR + " STRING," +
				PUB_DATE + " DATE," +
				IMAGE + " BLOB," +
				IMAGE_THUMBNAIL + "BLOB);";
		
		protected long addImage(PicasaImage image) {
			
			ContentValues values = new ContentValues();
			
			values.put(TITLE, image.getTitle());
			values.put(AUTHOR, image.getAuthor());
			values.put(PUB_DATE, image.getPublicationDate().toGMTString());
			if(image.getThumbnail() != null) {
				values.put(IMAGE_THUMBNAIL, compressToPNGBytes(image.getThumbnail()));
			}
			if(image.getImage() != null) {
				values.put(IMAGE, compressToPNGBytes(image.getImage()));
			}
			
			return db.insert(IMAGE_TABLE, TITLE, values);
		}
		
		public long addOrUpdateImage(PicasaImage image) {
			// TODO: Have this check to see if the image already exists in the DB then
			// just update that row instead of inserting a new item
			return addImage(image);
		}
		
		private byte[] compressToPNGBytes(Bitmap bitmap) {
			ByteArrayOutputStream outputPNG = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0, outputPNG);
			return outputPNG.toByteArray();
		}
		
		public List<PicasaImage> getImagesByName(String name) {
			return null;
		}
		
		public PicasaImage getImageById(long imageId) {
			return null;
		}
		
		public long getIdForImage(PicasaImage image) {
			return -1;
		}
		
		public List<PicasaImage> getImagesForAlbum(PicasaAlbum album){
			return null;
		}
	}
	
	// The application doesn't need to know how the mapping
	// works between images and albums
	private static class ImageAlbumRelationshipTable {
		static final String ALBUM_IMAGE_RELATION = "albumImages";
		
		static final String _ID = "_id";
		static final String ALBUM_ID = "albumID";
		static final String IMAGE_ID = "imageID";
		
		static final String CREATE_ALBUM_IMAGE_RELATION_SQL = 
			"CREATE TABLE " + ALBUM_IMAGE_RELATION +
			" ( " + _ID + " INTEGER AUTOINCREMENT," +
				ALBUM_ID + "INTEGER NOT NULL," +
				IMAGE_ID + "INTEGER NOT NULL," +
				"PRIMARY KEY( "+ ALBUM_ID + ", " + IMAGE_ID +");";
		
		public long getAlbumIdForImage(long imageId) {
			return -1;
		}
		
		public List<Long> getImageIdsForAlbum(long albumId){
			return null;
		}
		
		protected boolean addImageAlbumRelation(long albumId, long imageId) {
			ContentValues values = new ContentValues();
			
			values.put(ALBUM_ID, albumId);
			values.put(IMAGE_ID, imageId);
			
			long storedItemId = db.insert(ALBUM_IMAGE_RELATION, ALBUM_ID, values);
			return storedItemId > 0;
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
			tableNamesAndSQL.put(PicasaAlbumTable.ALBUM_TABLE, PicasaAlbumTable.CREATE_ALBUM_TABLE_SQL);
			tableNamesAndSQL.put(PicasaImageTable.IMAGE_TABLE, PicasaImageTable.CREATE_IMAGE_TABLE_SQL);
			tableNamesAndSQL.put(ImageAlbumRelationshipTable.ALBUM_IMAGE_RELATION, ImageAlbumRelationshipTable.CREATE_ALBUM_IMAGE_RELATION_SQL);
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
