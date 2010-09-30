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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ImageAlbumRelationshipManager extends DataManager{
	
	static final String IMAGE_ALBUM_RELATION = "albumImages";
	
	static final String _ID = "_id";
	static final String ALBUM_ID = "albumID";
	static final String IMAGE_ID = "imageID";
	
	static final String CREATE_ALBUM_IMAGE_RELATION_SQL = 
		"CREATE TABLE " + IMAGE_ALBUM_RELATION +
		" ( " + //_ID + " INTEGER AUTOINCREMENT," +
			ALBUM_ID + " INTEGER NOT NULL," +
			IMAGE_ID + " INTEGER NOT NULL," +
			"PRIMARY KEY( "+ ALBUM_ID + ", " + IMAGE_ID +") );";

	public ImageAlbumRelationshipManager(SQLiteDatabase db) {
		super(db);
		// TODO Auto-generated constructor stub
	}
	
	public long getAlbumIdForImage(long imageId) {
		// We can do a full projection
		long albumId = -1;
		
		String[] queryArgs = { Long.toString(imageId) };
		Cursor results = db.query(IMAGE_ALBUM_RELATION, null, IMAGE_ID+"=?", queryArgs, null, null, null);
		if(results.getCount() > 0) {
			if (results.getCount() > 1) {
				// Something has happened with the relationship
				Log.w("PicasaAlbumManager.ImageAlbumRelationship", "Image " + imageId + " points to more than one album.");
				Log.w("PicasaAlbumManager.ImageAlbumRelationship", "Going to use first result returned.");
			}
			results.moveToFirst();
			albumId = results.getLong(results.getColumnIndex(ALBUM_ID));
		}
		return albumId;
	}
	
	public List<Long> getImageIdsForAlbum(long albumId){
		ArrayList<Long> imageIds = new ArrayList<Long>();
		
		String[] queryArgs = { Long.toString(albumId) };
		Cursor results = db.query(IMAGE_ALBUM_RELATION, null, ALBUM_ID+"=?", queryArgs, null, null, null);
		if(results.getCount() > 0) {
			results.moveToFirst();
			while(!results.isAfterLast()) {
				long imageId = results.getLong(results.getColumnIndex(IMAGE_ID));
				imageIds.add(imageId);
				results.moveToNext();
			}
		}
		return imageIds;
	}
	
	protected boolean addImageAlbumRelation(long albumId, long imageId) {
		ContentValues values = new ContentValues();
		
		values.put(ALBUM_ID, albumId);
		values.put(IMAGE_ID, imageId);
		
		long storedItemId = db.insert(IMAGE_ALBUM_RELATION, ALBUM_ID, values);
		return storedItemId > 0;
	}

}
