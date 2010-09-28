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

import java.sql.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaAlbum;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaImage;

public class PicasaAlbumManager extends DataManager {
	

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

	
	public PicasaAlbumManager(SQLiteDatabase db) {
		super(db);
		// TODO Auto-generated constructor stub
	}
	
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
			PicasaImageManager imageTable = new PicasaImageManager(this.db);
			ImageAlbumRelationshipManager imageAlbumRelationships = new ImageAlbumRelationshipManager(this.db);
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
	
	private PicasaAlbum getAlbumByQuery(String[] params, String query) {
		PicasaAlbum album = null;
		Cursor result = db.query(ALBUM_TABLE, null, query, params, null, null, null);
		try {
			if(result.getCount() == 1) {
				result.moveToFirst();
				album = new PicasaAlbum();
				
				album.setTitle(result.getString(result.getColumnIndex(TITLE)));
				album.setAuthor(result.getString(result.getColumnIndex(AUTHOR)));
				String pubDate = result.getString(result.getColumnIndex(PUB_DATE));
				album.setCreatedDate(Date.valueOf(pubDate));
				
				ImageAlbumRelationshipManager imageAlbumRels = new ImageAlbumRelationshipManager(this.db);
				PicasaImageManager imageTable = new PicasaImageManager(this.db);
				
				List<Long> imageIds = imageAlbumRels.getImageIdsForAlbum(result.getLong(result.getColumnIndex(_ID)));
				for(Long imageId : imageIds) {
					album.addImage(imageTable.getImageById(imageId.longValue()));
				}
			}
		} finally {
			result.close();
		}
		return album;
	}
	
	public PicasaAlbum getAlbumByName(String albumName) {
		String[] params = { albumName };
		String query = TITLE +"=?";
		return getAlbumByQuery(params, query);
	}
	
	public PicasaAlbum getAlbumById(long id) {
		String[] params = { Long.toString(id) };
		String query = _ID +"=?";
		return getAlbumByQuery(params, query);
	}
	
	public long getIdForAlbum(PicasaAlbum album) {
		String[] args = { album.getTitle(), album.getAuthor(), album.getCreatedDate().toGMTString() };
		String query = String.format("%s=? AND %s=? AND %s=?", TITLE, AUTHOR, PUB_DATE);
		String[] projection = { _ID };
		Cursor cursor = db.query(ALBUM_TABLE, projection, query, args, null, null, null);
		long albumId = -1;
		try{				
			if(cursor.moveToFirst()) {
				if(cursor.getCount() > 1) {
					Log.e("ca.christophersaunders.tutorials.sqlite.db.PicasaAlbumManager", "More than one instance has the same attributes");
					Log.e("ca.christophersaunders.tutorials.sqlite.db.PicasaAlbumManager", "Aborting...");
					System.exit(1);
				}
				albumId = cursor.getLong(cursor.getColumnIndex(_ID));
			}
		} finally {
			cursor.close();
		}
		return albumId;
	}
}
