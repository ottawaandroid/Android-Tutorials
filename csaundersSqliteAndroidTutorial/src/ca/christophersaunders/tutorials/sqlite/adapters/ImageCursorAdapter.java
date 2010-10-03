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

import java.util.HashMap;

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
	
	private Context context;
	private Cursor cursor;
	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public ImageCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.picasa_image_row, null);
		}
		
		this.cursor.moveToPosition(pos);
		
		
		String imageTitle = cursor.getString(cursor.getColumnIndex(PicasaImageManager.TITLE));
		String imageAuthor = cursor.getString(cursor.getColumnIndex(PicasaImageManager.AUTHOR));
		Bitmap imageThumbnail = null;
		String cacheKey = imageTitle + imageAuthor;
		if(cache.containsKey(cacheKey)) {
			imageThumbnail = cache.get(cacheKey);
		} else {
			byte thumbnailBytes[] = cursor.getBlob(cursor.getColumnIndex(PicasaImageManager.IMAGE_THUMBNAIL));
			imageThumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
			cache.put(cacheKey, imageThumbnail);
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
