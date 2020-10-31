# Foodwise

## Smart Food Scanner

[Google Play](https://play.google.com/store/apps/details?id=com.darx.foodwise)

In the grocery store, we cannot always quickly determine whether a food product is right for us or not, how safe and healthy it is. The situation becomes more complicated if a person has dietary restrictions, various allergies, or is on a diet. Foodwise Android app lets you scan a product barcode and effortlessly see if it should be consumed.

The app has three main sections: Profile, Camera and History.

In the "Profile" your preferences are set: in the "Ingredients" section you can exclude any of the 60,000 ingredients included in the database and read information about E-supplements. "Groups" allow you to exclude a whole block of ingredients at once. For example, if you select "Vegetarian", then all products containing meat will be highlighted in red.

There are two modes in the "Camera" section: barcode scanning and fruit and vegetable recognition. After scanning the barcode, you will receive all the information about the product. The ingredients you excluded will be highlighted in red.

All previously scanned products will be saved in the "History". This section is equipped with text and voice search.

The fruit and vegetable recognition mode provides information on their nutritional and energy value. For example, one apple contains approximately 25 grams.
carbohydrates, which is unacceptable for people on a low-carb diet.

The application is written in Kotlin, the "Camera" uses the ML Kit to scan barcodes and identify fruits and vegetables. The backend consists of two services: a server API with a database, in
which stores 60,000 ingredients and formulations of 100,000 products, as well as a neural network written in Python and Tensorflow.