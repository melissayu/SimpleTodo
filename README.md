# Pre-work - Melissa's Todo App

Melissa's Todo App is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item.

Submitted by: Melissa Yu

Time spent: 13 hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **successfully add and remove items** from the todo list
* [x] User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in the todo list.
* [x] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [x] Persist the todo items [into SQLite](http://guides.codepath.com/android/Persisting-Data-to-the-Device#sqlite) instead of a text file
* [x] Improve style of the todo items in the list [using a custom adapter](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
* [x] Add support for completion due dates for todo items (and display within listview item)
* [x] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) instead of new Activity for editing items
* [x] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [x] List anything else that you can get done to improve the app functionality!
* Added a Description field to add details for each task
* Added new launcher icon
* Added a Priority field to each item (high, medium, low) and shown by an icon on the list view
* Ability to sort the list by due date, priority, or title of task.
* Improve the UI of the "Sort by" spinner above the list, added an empty list message if there are no items.

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

<img src='https://dl.dropboxusercontent.com/u/3465192/2017_02_06_20_27_09.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

* Gif direct link if above doesn't work: https://dl.dropboxusercontent.com/u/3465192/2017_02_06_20_27_09.gif
* Updated gif with edit as modal: https://dl.dropboxusercontent.com/u/3465192/2017_02_06_22_40_06.gif
* Video link in case gif doesn't work: https://www.youtube.com/watch?v=SN6TMG9N5vc
* Updated gif with due date picker implemented: https://dl.dropboxusercontent.com/u/3465192/2017_02_16_01_00_09.gif

## Notes

Trying to capture video and upload the gif was harder than making the app! I have trouble running the emulator on this computer so it was captured on the actual device.

Updated to use SQLite DB and custom adapter to display the list.

Added columns to SQLite DB (with DBFlow) with a DB Migration, added description and due date columns. Updated add/edit dialog and list adapter UI to display the task title, description, and due date.

## License

    Copyright 2017 Melissa Yu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
