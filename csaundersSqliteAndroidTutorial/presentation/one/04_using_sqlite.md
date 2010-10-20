!SLIDE 
# Into SQLite #

!SLIDE bullets incremental transition=fade
# The Data #

## Picasa Feeds ##

* In particular mine (for now)

!SLIDE full-page center

![The Data](the_data.png)

[Full Data Feed](http://picasaweb.google.com/data/feed/base/user/c.saunders322/albumid/5513471319225508497?alt=rss&kind=photo&hl=en_US)

!SLIDE full-page center

![The Data we Care About](the_data_we_care_about.png)

!SLIDE full-screen center

# Data Model #

![Entity Relationship Diagram](image_album_er_diag.png)

!SLIDE center

# Picasa Objects #

[PicasaAlbum.java](./src/ca/christophersaunders/tutorials/sqlite/picasa/PicasaAlbum.java)

[PicasaImage.java](./src/ca/christophersaunders/tutorials/sqlite/picasa/PicasaImage.java)

!SLIDE bullets incremental

# Setting up the Database #

* onCreate( ) - Overridden by subclass, used to create the database
* onUpgrade( ) - Overridden by subclass, where migrations should occur

!SLIDE code center transition=fade

# Get the Code #

git clone git@github.com:csaunders/Android-Tutorials.git

!SLIDE code center

# Checkout Proper Revision #

git checkout step_one