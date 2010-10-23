!SLIDE center transition=fade

# Picasa Objects #

[PicasaAlbum.java](./src/ca/christophersaunders/tutorials/sqlite/picasa/PicasaAlbum.java)

[PicasaImage.java](./src/ca/christophersaunders/tutorials/sqlite/picasa/PicasaImage.java)

!SLIDE center

# Setting up the Database #

Database Helper - [ImageAlbumDatabaseHelper.java](./src/ca/christophersaunders/tutorials/sqlite/db/ImageAlbumDatabaseHelper.java)

!SLIDE bullets incremental center

# Pulling the Data #

SAX Parser - [PicasaHanlder.java](./src/ca/christophersaunders/tutorials/sqlite/picasa/PicasaHandler.java)

!SLIDE center

# Storing the Data #

[PicasaAlbumManager.java](./src/ca/christophersaunders/tutorials/sqlite/db/PicasaAlbumManager.java)

[PicasaImageManager.java](./src/ca/christophersaunders/tutorials/sqlite/db/PicasaImageManager.java)

[ImageAlbumRelationshipManager.java](./src/ca/christophersaunders/tutorials/sqlite/db/ImageAlbumRelationshipManager.java)

!SLIDE center

# Putting It All Together #

populateDataWithDefaults() - [SQLiteAndroidTutorialMainActivity.java](./src/ca/christophersaunders/tutorials/sqlite/SQLiteAndroidTutorialMainActivity.java)

!SLIDE bullets incremental

# Displaying our Data #

* Since we are working with a lot of data we will be using Cursor Adapters
* These allow us to only pull the data into memory when we need it

!SLIDE smbullets center

## The View ##

[picasa_album_row.xml](./src/ca/christophersaunders/tutorials/sqlite/res/layout/picasa_album_row.xml)

## The Adapter ##

* Handles populating each row with data

[AlbumCursorAdapter.java](./src/ca/christophersaunders/tutorials/sqlite/adapters/AlbumCursorAdapter.java)

!SLIDE smbullets center

## Handling Input ##

* All we need to do is add a listener to the adapter
* In our case the listener is the activity who owns the
adapter in question

[AlbumViewingActivity.java](./src/ca/christophersaunders/tutorials/sqlite/AlbumViewingActivity.java)