package ca.christophersaunders.tutorials.sqlite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import ca.christophersaunders.tutorials.sqlite.R;
import ca.christophersaunders.tutorials.sqlite.db.PicasaAlbumManager;

public class AlbumCursorAdapter extends SimpleCursorAdapter {
	
	private Cursor cursor;
	private Context context;

	public AlbumCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.cursor = c;
		this.context = context;
	}
	
	@Override
	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		
		if(v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.picasa_album_row, null);
			// create new view
		}
		
		cursor.moveToPosition(pos);
		
		String title = cursor.getString(cursor.getColumnIndex(PicasaAlbumManager.TITLE));
		String author = cursor.getString(cursor.getColumnIndex(PicasaAlbumManager.AUTHOR));
		
		TextView titleLabel = (TextView) v.findViewById(R.id.picasaAlbumTitle);
		titleLabel.setText(title);
		
		TextView authorLabel = (TextView) v.findViewById(R.id.picasaAlbumAuthor);
		authorLabel.setText(author);
		
		
		return v;
	}

}
