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

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaAlbum;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaImage;

public class PicasaImageManager extends DataManager{

	public PicasaImageManager(SQLiteDatabase db) {
		super(db);
	}
	
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
	
	private PicasaImage createImageFromCursor(Cursor cursor) {
		PicasaImage image = new PicasaImage();
		
		image.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
		image.setAuthor(cursor.getString(cursor.getColumnIndex(AUTHOR)));
		image.setPublicationDate(Date.valueOf(cursor.getString(cursor.getColumnIndex(PUB_DATE))));
		byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(IMAGE));
		image.setImage(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
		byte[] thumbnailBytes = cursor.getBlob(cursor.getColumnIndex(IMAGE_THUMBNAIL));
		image.setThumbnail(BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length));
		
		return image;
	}
	
	public List<PicasaImage> getImagesByName(String name) {
		ArrayList<PicasaImage> imagesWithName = new ArrayList<PicasaImage>();
		String[] args = { name };
		String query = TITLE + "=?";
		Cursor cursor = db.query(IMAGE_TABLE, null, query, args, null, null, null);
		try {
			if(cursor.moveToFirst()) {
				while(!cursor.isLast()) {
					imagesWithName.add(createImageFromCursor(cursor));
					
					cursor.moveToNext();
				}
			}
		} finally {
			cursor.close();
		}
		return imagesWithName;
	}
	
	public PicasaImage getImageById(long imageId) {
		PicasaImage image = null;
		String[] args = { Long.toString(imageId) };
		String query = _ID + "=?";
		Cursor cursor = db.query(IMAGE_TABLE, null, query, args, null, null, null);
		try {
			if(cursor.moveToFirst()) {
				if(cursor.getCount() > 1) {
					// TODO: Log an error
					Log.e("ca.christophersaunders.tutorials.sqlite.db", "Error happened, fix this");
					Log.e("ca.christophersaunders.tutorials.sqlite.db", "Aborting...");
					System.exit(1);
				}
				image = createImageFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return image;
	}
	
	public long getIdForImage(PicasaImage image) {
		String[] args = { image.getTitle(), image.getAuthor(), image.getPublicationDate().toGMTString()};
		String query = String.format("%s=? AND %s=? AND %s=?", TITLE, AUTHOR, PUB_DATE);
		String[] projection = { _ID };
		Cursor cursor = db.query(IMAGE_TABLE, projection, query, args, null, null, null);
		long imageId = -1;
		try {
			if(cursor.moveToFirst()) {
				if(cursor.getCount() > 0) {
					// lolwut
				}
				imageId = cursor.getLong(cursor.getColumnIndex(_ID));
			}
		} finally {
			cursor.close();
		}
		return imageId;
	}
	
	public List<PicasaImage> getImagesForAlbum(PicasaAlbum album){
		ArrayList<PicasaImage> images = new ArrayList<PicasaImage>();
		
		PicasaAlbumManager albumTable = new PicasaAlbumManager(this.db);
		ImageAlbumRelationshipManager imageAlbumReln = new ImageAlbumRelationshipManager(this.db);
		long albumId = albumTable.getIdForAlbum(album);
		List<Long> imageIds = imageAlbumReln.getImageIdsForAlbum(albumId);
		for(Long id : imageIds) {
			images.add(getImageById(id.longValue()));
		}
		return images;
	}

}
