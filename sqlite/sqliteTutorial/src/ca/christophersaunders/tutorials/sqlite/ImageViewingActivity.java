package ca.christophersaunders.tutorials.sqlite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import ca.christophersaunders.tutorials.sqlite.db.ImageAlbumDatabaseHelper;
import ca.christophersaunders.tutorials.sqlite.db.PicasaImageManager;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaImage;

public class ImageViewingActivity extends Activity {
	public static final String IMAGE_ID = "image_id";
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.image_viewer);
		
		Bundle extras = getIntent().getExtras();
		if(!extras.isEmpty()) {
			long imageId = extras.getLong(IMAGE_ID);
			ImageAlbumDatabaseHelper helper = new ImageAlbumDatabaseHelper(this);
			PicasaImageManager imageManager = helper.getImageManager();
			
			PicasaImage image = imageManager.getImageById(imageId);
			byte[] imageBytes = image.getImageBytes();
			
			Bitmap bmp =BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			
			ImageView v = (ImageView) findViewById(R.id.imageViewerImage);
			v.setImageDrawable(new BitmapDrawable(bmp));
		}
	}

}
