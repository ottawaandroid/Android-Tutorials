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
package ca.christophersaunders.tutorials.sqlite.picasa;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class PicasaImage {
	
	private String imageLocation;
	private Bitmap image;
	private byte[] imageBytes; 
	private String thumbnailLocation;
	private Bitmap thumbnail;
	private byte[] thumbnailBytes;
	private String title;
	private String author;
	private Date publicationDate;
	
	public byte[] getImageBytes() {
		if (imageBytes == null) {
			imageBytes = getBitmapBytesForLocation(imageLocation);
		}
		return imageBytes;
	}
	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}
	public Bitmap getImage() {
		if(image == null && imageLocation != null) {
			getImageBytes();
			image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		}
		return image;
	}
	public void setImage(Bitmap image) {
		this.image = image;
	}
	public byte[] getThumbnailBytes() {
		if(thumbnailBytes == null) {
			thumbnailBytes = getBitmapBytesForLocation(thumbnailLocation);
		}
		return thumbnailBytes;
	}
	public void setThumbnailBytes(byte[] thumbnailBytes) {
		this.thumbnailBytes = thumbnailBytes;
	}
	public Bitmap getThumbnail() {
		if(thumbnail == null && thumbnailLocation != null) {
			getThumbnailBytes();
		}
		if(thumbnail == null && thumbnailBytes.length > 0) {
			thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
		}
		return thumbnail;
	}
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getImageLocation() {
		return imageLocation;
	}
	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}
	public String getThumbnailLocation() {
		return thumbnailLocation;
	}
	public void setThumbnailLocation(String thumbnailLocation) {
		this.thumbnailLocation = thumbnailLocation;
	}
	public Date getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	//-- private utility functions
	private byte[] getBitmapBytesForLocation(String location) {
		try {
			URL thumbnailUrl = new URL(location);
			URLConnection conn = thumbnailUrl.openConnection();
			
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			
			ByteArrayBuffer imageBytesBuffer = new ByteArrayBuffer(100);
			byte[] buffer = new byte[1024];
			int current = 0;
			while( (current = bis.read(buffer)) != -1) {
				imageBytesBuffer.append(buffer, 0, current);
			}
			
			byte[] imageBytes = imageBytesBuffer.toByteArray();
			return imageBytes;
			
		} catch (Exception gottaCatchemAll) {
			Log.w("PicasaImage", "Gotta catch em' all!");
			Log.e("PicasaImage", String.format("Location string was probably bad: %s", location));
			gottaCatchemAll.printStackTrace();
		}
		return new byte[0];
	}
}
