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
package ca.christophersaunders.tutorials.sqlite.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import ca.christophersaunders.tutorials.sqlite.R;
import ca.christophersaunders.tutorials.sqlite.db.PicasaImageManager;

public class ImageCursorAdapter extends SimpleCursorAdapter {
	
	private class ImageDataContainer {
		private String title;
		private String author;
		private Bitmap thumbnail;
	}
	
	private Context context;
	private Cursor cursor;
	private ArrayList<ImageDataContainer> imageDataCache = new ArrayList<ImageDataContainer>();

	public ImageCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
	}
	
	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.picasa_image_row, null);
		}
		
		String imageTitle, imageAuthor;
		Bitmap imageThumbnail;
		
		if(pos < imageDataCache.size()) {
			ImageDataContainer container = imageDataCache.get(pos);
			imageTitle = container.title;
			imageAuthor = container.author;
			imageThumbnail = container.thumbnail;
			
		} else {
			this.cursor.moveToPosition(pos);
			
			imageTitle = cursor.getString(cursor.getColumnIndex(PicasaImageManager.TITLE));
			imageAuthor = cursor.getString(cursor.getColumnIndex(PicasaImageManager.AUTHOR));
			imageThumbnail = null;
			
			byte thumbnailBytes[] = cursor.getBlob(cursor.getColumnIndex(PicasaImageManager.IMAGE_THUMBNAIL));
			imageThumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
			
			ImageDataContainer container = new ImageDataContainer();
			container.title = imageTitle;
			container.author = imageAuthor;
			container.thumbnail = imageThumbnail;
			imageDataCache.add(pos, container);
		}
		
		ImageView thumbnail = (ImageView) v.findViewById(R.id.imageThumbnail);
		thumbnail.setImageBitmap(imageThumbnail);
		
		TextView titleLabel = (TextView) v.findViewById(R.id.imageTitleLabel);
		titleLabel.setText(imageTitle);
		
		TextView authorLabel = (TextView) v.findViewById(R.id.imageAuthorLabel);
		authorLabel.setText(imageAuthor);
		
		return v;
	}

}
