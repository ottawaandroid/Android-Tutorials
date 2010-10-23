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
package ca.christophersaunders.tutorials.sqlite;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import ca.christophersaunders.tutorials.sqlite.adapters.AlbumCursorAdapter;
import ca.christophersaunders.tutorials.sqlite.db.ImageAlbumDatabaseHelper;
import ca.christophersaunders.tutorials.sqlite.db.PicasaAlbumManager;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaAlbum;
import ca.christophersaunders.tutorials.sqlite.picasa.PicasaHandler;

public class AlbumViewingActivity extends Activity implements OnItemClickListener {
	private AlbumCursorAdapter albumCursorAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageAlbumDatabaseHelper databaseHelper = new ImageAlbumDatabaseHelper(getApplicationContext());
        PicasaAlbumManager albumManager = databaseHelper.getAlbumManager();
        
        Cursor albumCursor = albumManager.getAlbumCursor();
        
        // Let the activity manage the cursor so that our cursors get closed
        // when our activity is finished
        startManagingCursor(albumCursor);
        
        String[] columns = new String[] { PicasaAlbumManager.TITLE, PicasaAlbumManager.AUTHOR };
        int[] names = new int[] {R.id.picasaAlbumTitle, R.id.picasaAlbumAuthor };
        
        albumCursorAdapter = new AlbumCursorAdapter(this, R.layout.picasa_album_row, albumCursor, columns, names);
        
        ListView albumList = (ListView) findViewById(R.id.picasaAlbumList);
        albumList.setAdapter(albumCursorAdapter);
        albumList.setOnItemClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_screen_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.addFeed:
    		Log.i("Add Feed", "I'm afraid I cannot allow you to do that quite yet");
    		break;
    	case R.id.populateDefaults:
    		populateDataWithDefaults();
    		break;
    	default:
    		return false;
    	}
    	return true;
    }
    
    /**
     * Hardcoded example showing how we use everything that has been put together.
     * This will pull down the RSS feed from a specific stream, parse it, then pull
     * down all the images required and store them in the database.
     */
    public void populateDataWithDefaults() {
    	try {
	        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
	        PicasaHandler picasaHandler = new PicasaHandler();
	        URL picasaFeed = new URL("http://picasaweb.google.com/data/feed/base/user/c.saunders322/albumid/5513471319225508497?alt=rss&kind=photo&hl=en_US");
	        parser.parse(picasaFeed.openStream(), picasaHandler);
	        PicasaAlbum album = picasaHandler.getParsedAlbum();
	        Log.d("Parser Results:", "There are " + album.getAlbumImages().size() + " images in the " + album.getTitle() +" album created by " + album.getAuthor());
	        
	        ImageAlbumDatabaseHelper databaseHelper = new ImageAlbumDatabaseHelper(getApplicationContext());
	        PicasaAlbumManager albumManager = databaseHelper.getAlbumManager();
	        if(!albumManager.albumExists(album.getTitle())) {
	        	long albumId = albumManager.addAlbum(album);
		        if(albumId > 0) {
		        	Log.d(getClass().toString(), "Successfully added album to the database");
		        } else {
		        	Log.e(getClass().toString(), "Something went wrong when trying to add new information to disk");
		        }
	        }
	        // Since our data has changed, we need to inform the cursor to update itself
	        albumCursorAdapter.getCursor().requery();
    	} catch (SAXException saxException) {
        	saxException.printStackTrace();
        } catch (MalformedURLException murlException) {
        	murlException.printStackTrace();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        } catch (ParserConfigurationException pce) {
        	pce.printStackTrace();
        }
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		long albumId = albumCursorAdapter.getItemId(position);
		
		// We need to pass the album information over to the next activity
		Bundle extras = new Bundle();
		extras.putLong(AlbumImageViewingActivity.ALBUM_ID, albumId);
		
		Intent imageBrowser = new Intent(this, AlbumImageViewingActivity.class);
		imageBrowser.putExtras(extras);
		
		startActivity(imageBrowser);
	}
}
