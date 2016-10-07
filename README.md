# *Buzzard*

**Buzzard** is here to help tech-savvy drivers secure an on street parking space immediately. Instead of searching high and low, buzzard user's are navigated directly to an open space.

Upon opening the app, a new user will see the available spaces in their vicinity. When they tap the big blue button, they are dispatched a few of the freshest parking spaces to choose from. They can either select one of these spaces to be navigated to or they can press the navigate button and be navigated to the closest one.

For convenience, users can choose between being navigated in the app or externally by google maps.

Upon arriving at a parking spot, a buzzard driver can set a timer to be reminded of parking restrictions. The app will send a push notification before the time is up.

Buzzard provides a seamless solution for finding parking spaces, navigating to them and avoiding tickets along the way. Join us!

## User Stories

The following functionality is completed:

* [x] A user can see a map with available parking spaces
* [x] A user can search for an area and see available parking spaces
* [x] A user can park in a space
* [x] A user can report an available parking space
* [x] A user can navigate to a parking space within the app, guided by polylines
* [x] A user can navigate to a parking space using an external navigation app (i.e. Google Map).
* [x] A user can select a marker on the map and see details.

Optional:
* [x] The map displays multiple types of custom markers depicting how old a spot is
* [x] The map can display a group icon showing the number of spaces when the map is zoomed out.
* [x] A floating icon can be shown on top of Google Map while navigating so that a user can easily go back to the app.
* [x] A user can see its parking history.
* [x] A user can switch to indicate whether it always wants to launch external navigation app.
* [ ] A user can save a region to a list of favorites for quick checking
  * [ ] A user can subscribe to parking changes in an area with push notifications
  * [ ] A user can delete a saved region
* [x] Implement geofencing in order to automatically park in a space
* [x] A user can notify when they are going to leave a space
  * [ ] Display a marker indicating the space will be vacated.
* [ ] Polish the animations: pin a parking space
* [ ] NUX(i.e. new user experience) screen to show you how to use the app
* [x] A user can swipe up and see a few card details about any parking spot
* [x] A user can see google street view as the detail about any parking spot

Bonus:
* [ ] Notify a user parked in of parking restrictions ex. Street cleaning
* [ ] Display parking garage data if no spaces are available
* [ ] Display a persistent notification requesting action when a user is leaving their space
* [ ] Reroute a user based on parking availability or if a their spot gets taken
* [ ] Dispatch selected spaces to each users so not all users receive all spaces

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.giphy.com/l0HlDV9ILQpWweZz2.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />


http://gph.is/2duQmCu

## Wireframes
![A user opens the app and sees open spots in their vicinity.](https://s4.postimg.org/8e2o52twt/OpeningScreen.png)
![A user can search for parking in an area they are going and be directed to a spot through an external app.](https://s4.postimg.org/7r3rfjx0t/Search+Screen.png)
![A user can fill a spot and then see their car on a map to make finding their car in unfamiliar areas easier.](https://s4.postimg.org/3tghq5a7h/Park+Now.png)
![A user can signal when they are ready to leave to notify nearby drivers that a spot will be vacant soon.](https://s4.postimg.org/enxxs2tbh/Car+Parked.png)
![A user can leave and add their spot back into the available spaces.](https://s4.postimg.org/tvdxcfl65/Car+leaving.png)
![A user can choose a space on the map and navigate here through an external app.](https://s4.postimg.org/bb1pazkx9/navhere.png)
![A user can save an area to watch for available parking spaces.](https://s4.postimg.org/asujpia5p/Choose+geobounded region.png)
![A user can receive push notifications for available spots in saved regions.](https://s4.postimg.org/ia3r4pzot/Notifications.png)
