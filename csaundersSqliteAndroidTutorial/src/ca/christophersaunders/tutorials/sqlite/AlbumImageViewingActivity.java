package ca.christophersaunders.tutorials.sqlite;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import ca.christophersaunders.tutorials.sqlite.adapters.ImageCursorAdapter;
import ca.christophersaunders.tutorials.sqlite.db.ImageAlbumDatabaseHelper;
import ca.christophersaunders.tutorials.sqlite.db.PicasaAlbumManager;
import ca.christophersaunders.tutorials.sqlite.db.PicasaImageManager;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaAlbum;

public class AlbumImageViewingActivity extends Activity {
	
	public static final String ALBUM_ID = "album_id";
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.album_image_viewer);
		
		Bundle extras = getIntent().getExtras();
		if(extras.isEmpty()) {
			throw new IllegalArgumentException("No album id was passed in");
		}
		
		long albumId = extras.getLong(ALBUM_ID);
		
		ImageAlbumDatabaseHelper helper = new ImageAlbumDatabaseHelper(this);
		PicasaAlbumManager albumManager = helper.getAlbumManager();
		PicasaAlbum album = albumManager.getAlbumById(albumId);
		
		TextView title = (TextView) findViewById(R.id.albumTitleLabel);
		title.setText(album.getTitle());
		
		PicasaImageManager imageManager = helper.getImageManager();
		Cursor imagesCursor = imageManager.getImagesCursorForAlbumId(albumId);
		
		startManagingCursor(imagesCursor);
		
		String[] columns = new String[] {PicasaImageManager.TITLE, PicasaImageManager.AUTHOR, PicasaImageManager.IMAGE_THUMBNAIL};
		int[] names = new int[] {R.id.imageTitleLabel, R.id.imageAuthorLabel, R.id.imageThumbnail};
		
		ImageCursorAdapter imageAdapter = new ImageCursorAdapter(this, R.layout.picasa_image_row, imagesCursor, columns, names);
		ListView listing = (ListView) findViewById(R.id.albumImagesList);
		listing.setAdapter(imageAdapter);
	}

}
