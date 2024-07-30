### DESCRIPTION<br>
A visual programming environment build with JavaFX.<br>

See example at: https://www.youtube.com/watch?v=Oev1IJZvAPw
<br><br>

### QUICK START
* Left drag: selection rectangle
* Right drag: pan
* Right click: radial menu
* Double click: open hub search box
* Mouse wheel: zoom
<br>

### SHORTCUTS
* CTRL-C: copy selected hubs
* CTRL-V: paste copied hubs
* CTRL-G: group selected hubs
* CTRL-O: load graph
* CTRL-S: save graph
* CTRL-N: new graph
* Space: zoom to fit
* Del: delete selected hubs
<br>

### FUNCTIONALITIES
* Input functions to generate primitve data types e.g. String, Boolean, Integer, Long, Double
* Math functions such as multiply, divide, add and substract
* List functions to get, add, remove and replace items
* Json functions to get properties
<br>

### CUSTOMIZE
* Create custom hubs by copying TemplateHub.java and change its behaviour
* Create your own library with hub types and add it to "build/ext/"
<br>

### STYLE
* Change styles in VPLTester.java to either flat white, flat dark or default
* Customize your own style by using one of the above styles as template
<br>


### TODO
* General UI/UX
    * Start screen
    * Menu bar
    * Undo/redo
    * Auto save/load
    * Save canvas size, position and zoom factor
    * Zoom controls

* Hub behaviour
    * Hints
    * Remove connections
    * Exceptions
    * Save hub size

* Hub library extension
    * Load external libraries (subclasses and static methods)
    * Load hubs through reflection
    * Migrate existing hubs to external libraries

* Bugs
    * Fix align submenu of radial menu

* Other
    * Remove GPLv3 code
    * Customization functionality through config file
    * Comments
    * Labels to radial menu
    * Scripting hub
    * Looping groups/areas
    * Improve grouping
    * BUG text and hubs are copied/pasted simultaneously
