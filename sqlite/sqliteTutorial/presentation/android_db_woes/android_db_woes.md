!SLIDE bullets incremental transition=fade

# Issues with the Android DB #

* Not very convenient to write DB code
* Need to establish many of your relations yourself
* Personally found to be violating the DRY principle frequently
* Wasting time on the plumbing instead of solving the problems

!SLIDE bullets incremental

# Issues with the Android DB #

* **Writing all the DB code is boooooring**

!SLIDE center

# Example #

### [PicasaAlbumManager.java](http://github.com/csaunders/Android-Tutorials/blob/master/sqlite/sqliteTutorial/src/ca/christophersaunders/tutorials/sqlite/db/PicasaAlbumManager.java) ###
### [PicasaImageManager.java](http://github.com/csaunders/Android-Tutorials/blob/master/sqlite/sqliteTutorial/src/ca/christophersaunders/tutorials/sqlite/db/PicasaImageManager.java) ###

!SLIDE smbullets incremental

# How it could be better with ORM #

* Having access to ORM tools make it easier to represent how the data is stored. ([Example](http://docs.djangoproject.com/en/1.2/topics/db/models/))
* Keeps your code DRY
* Decreases the chances of having vulnerable or buggy SQL (SQL Injections, Improper Query Construction, etc.)
* Makes developers happy