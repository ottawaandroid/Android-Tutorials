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

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PicasaHandler extends DefaultHandler {
	// XML elements we want to extract from the feed
	
	// General XML Elements
	private static final String CHANNEL_ELEMENT = "channel";
	private static final String CHANNEL_BUILD_DATE = "lastBuildDate";
	private static final String MANAGING_EDITOR = "managingEditor";
	
	private static final String ITEM = "item";
	private static final String ITEM_PUBLICATION_DATE = "pubDate";
	private static final String GROUP = "group";
	private static final String CREDIT = "credit";
	private static final String TITLE = "title";
	private static final String CONTENT = "content";
	private static final String THUMBNAIL = "thumbnail";
	
	// Thumbnail Attributes
	private static final String THUMBNAIL_WIDTH = "72";
	private static final String THUMBNAIL_HEIGHT = "49";
	
	// URIs we care about
	private static final String MEDIA_URI = "http://search.yahoo.com/mrss/";
	
	private PicasaAlbum album = null;
	private PicasaImage albumImageEntity = null;
	
	private boolean inItem = false;
	private boolean inMediaGroup = false;
	
	private StringBuilder buffer = new StringBuilder();
	
	@Override
	public void startElement(String uri, String localname, String qName, Attributes attributes) {
		if(localname.equalsIgnoreCase(CHANNEL_ELEMENT) && album == null) {
			album = new PicasaAlbum();
		}
		if(localname.equalsIgnoreCase(ITEM) && albumImageEntity == null) {
			albumImageEntity = new PicasaImage();
			inItem = true;
		}
		if(localname.equalsIgnoreCase(GROUP) && uri.equalsIgnoreCase(MEDIA_URI)) {
			inMediaGroup = true;
		}
		if(localname.equalsIgnoreCase(CONTENT) && inMediaGroup) {
			String imageLocation = attributes.getValue("url");
			if(imageLocation != null) {
				albumImageEntity.setImageLocation(imageLocation);
			}
		}
		if(localname.equalsIgnoreCase(THUMBNAIL) && inMediaGroup) {
			String height = attributes.getValue("height");
			String width = attributes.getValue("width");
			if(height.equalsIgnoreCase(THUMBNAIL_HEIGHT) && width.equalsIgnoreCase(THUMBNAIL_WIDTH)) {
				String thumbnailLocation = attributes.getValue("url");
				if(thumbnailLocation != null) {
					albumImageEntity.setThumbnailLocation(thumbnailLocation);
				}
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localname, String qName) {
		if(localname.equalsIgnoreCase(CHANNEL_BUILD_DATE)) {
			String dateString = buffer.toString();
			Date date = new Date(dateString);
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			album.setCreatedDate(sqlDate);
		}
		if(localname.equalsIgnoreCase(TITLE)) {
			String title = buffer.toString();
			if(inMediaGroup) {
				albumImageEntity.setTitle(title);
			} else if(!inItem){
				album.setTitle(title);
			}
		}
		if(localname.equalsIgnoreCase(MANAGING_EDITOR)) {
			String author = buffer.toString();
			album.setAuthor(author);
		}
		if(localname.equalsIgnoreCase(ITEM)) {
			album.addImage(albumImageEntity);
			albumImageEntity = null;
			inItem = false;
		}
		if(localname.equalsIgnoreCase(ITEM_PUBLICATION_DATE)) {
			java.util.Date pubDate = new java.util.Date(buffer.toString());
			java.sql.Date sqlPubDate = new java.sql.Date(pubDate.getTime());
			albumImageEntity.setPublicationDate(sqlPubDate);
		}
		if(localname.equalsIgnoreCase(GROUP) && inMediaGroup) {
			inMediaGroup = false;
		}
		if(localname.equals(MANAGING_EDITOR)) {
			String albumAuthor = buffer.toString();
			album.setAuthor(albumAuthor);
		}
		if(localname.equals(CREDIT) && inMediaGroup) {
			String imageAuthor = buffer.toString();
			albumImageEntity.setAuthor(imageAuthor);
		}
		
		// Clear the buffer at the end of every element
		buffer.replace(0, buffer.length(), "");
	}
	
	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}
	
	@Override
	public void endDocument() {
		// Going with the default for now - doing nothing
	}
	
	public PicasaAlbum getParsedAlbum() {
		return album;
	}

}
