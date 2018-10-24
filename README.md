# Book Store Inventory

**Created for the *Grow with Google Challenge Scholarship:* Android Basics Course**

Used **API 28: Android 9 (Pie)**

This is my final project, or my capstone, from my Udacity Android Basics Nanodegree program.

## Overview of the application itself
This is a bookstore app, which allows the user to either populate the catalog view with dummy
data (The Magicians by Lev Grossman) or add a new book by clicking on the Floating Action Button
in the lower right hand corner. When a user wants to add a new book they are prompted enter
information about the following attributes: Title, Author, Price, Type of Book, Quantity (number
in stock), the supplier name, and supplier's phone number. There is an option to save this or
leave the editor without saving any information. After an item is saved it is displayed on the
home screen in the form of a "Catalog View" which lists each item in the inventory. Users may
sell items which in turn decreases the quantity of the number of books in stock. Users may also
click on each list item to see details about each book where they can edit or delete the item as
well as contact the supplier by phone! Back in the main activity, there is also the option to delete
all entries if they want to start over with a new inventory list.

## Screenshots of the UI:
[![Inventory-UI1.png](https://i.postimg.cc/6pHS8BNX/Inventory-UI1.png)](https://postimg.cc/xXzg7DCx) [![Inventory-UI2.png](https://i.postimg.cc/65cbPBqv/Inventory-UI2.png)](https://postimg.cc/SJXG2FWS)
[![Inventory-UI3.png](https://i.postimg.cc/qBLGJjrf/Inventory-UI3.png)](https://postimg.cc/vD4nX7DX) [![Inventory-UI4.png](https://i.postimg.cc/SKF97M47/Inventory-UI4.png)](https://postimg.cc/mPjhThtt)
[![Inventory-UI5.png](https://i.postimg.cc/DzTWb3Qx/Inventory-UI5.png)](https://postimg.cc/TKt2ZS4b) [![Inventory-UI6.png](https://i.postimg.cc/GmktRS0G/Inventory-UI6.png)](https://postimg.cc/CRxSH7xx)
[![Inventory-UI7.png](https://i.postimg.cc/1tY5Bk6h/Inventory-UI7.png)](https://postimg.cc/5YLVNknn) [![Inventory-UI8.png](https://i.postimg.cc/CxHhXBD4/Inventory-UI8.png)](https://postimg.cc/MnHJjG9c)

## About the Code
This app uses a ContentProvider, defined in BookProvider.java, to communicate with a SQLite
database via the Content URI. As seen in the hierarchy of my application file tree, I separated
out the data components that the database actually pulls from and the components that work
to read and manipulate that data. Inside the data directory you will find the contract where
I define all of my table attributes and their respective rules as well as the Provider and
Database Helper file. In the main Java file you will find the Cursor Adapter which retrieves
and parses the data which is then passed back into my activities and displayed in a catalog
view.

## Resources: Images and items
Most of my icons were downloaded from [Material Design] (https://material.io/tools/icons/?style=baseline).
The photo on my Empty View screen (when there is no data) is from [pngtree] (https://pngtree.com/freepng/vector-bookstore_2052400.html).
I used [Amazon] (https://www.amazon.com/Magicians-Novel-Trilogy/dp/0452296293) for my dummy data book!


## More to come
At present this app mostly contains components that meet the requirements for my class, however
I plan on adding certain support libraries like Butterknife and Snackbar in the future. I will also
be converting all of the layouts to more current standards like ConstraintLayout & CoordinatorLayout
as well as implementing RecyclerView in lieu of ListView. Stay tuned!