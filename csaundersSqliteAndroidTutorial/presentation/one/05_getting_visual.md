!SLIDE bullets incremental transition=fade

# Displaying our Data #

* Since we are working with a lot of data we will be using Cursor Adapters
* These allow us to only pull the data into memory when we need it

!SLIDE smbullets center

## Album View ##

[picasa_album_row.xml](./src/ca/christophersaunders/tutorials/sqlite/res/layout/picasa_album_row.xml)

## Album Controller ##

* Handles populating row with data as well as handling user input

[AlbumCursorAdapter.java](./src/ca/christophersaunders/tutorials/sqlite/adapters/AlbumCursorAdapter.java)