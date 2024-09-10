### DESCRIPTION<br>
A visual programming environment build with JavaFX.<br>

See example at: https://www.youtube.com/watch?v=Oev1IJZvAPw
<br><br>

### QUICK START
* Left drag: selection rectangle
* Right drag: pan
* Right click: radial menu
* Double click: open block search box
* Mouse wheel: zoom
<br>

### SHORTCUTS
* CMD/CTRL-C: copy selected blocks
* CMD/CTRL-V: paste copied blocks
* CMD/CTRL-G: group selected blocks
* CMD/CTRL-O: load graph
* CMD/CTRL-S: save graph
* CMD/CTRL-N: new graph
* Space: zoom to fit
* Del/Backspace: delete selected blocks
<br>

### FUNCTIONALITIES
* Input functions to generate primitve data types e.g. String, Boolean, Integer, Long, Double
* Math functions such as multiply, divide, add and substract
* List functions to get, add, remove and replace items
* Json functions to get properties
<br>

### CUSTOMIZE
* Create custom blocks by copying TemplateBlock.java and change its behaviour
* Create your own library with block types and add it to "build/lib/"
<br>

### STYLE
* Change styles in App.java to either flat white, flat dark or default
* Customize your own style by using one of the above styles as template
<br>


### TODO
* General UI/UX
    * Start screen
    * ✅ Menu bar
    * Undo/redo
    * Auto save/load
    * Save canvas size, position and zoom factor
    * Zoom controls
<br>

* Block behaviour
    * Hints
    * ✅ Remove connections
    * Exceptions
    * Save block size
<br>

* Block library extension
    * ✅ Load external libraries (subclasses and static methods)
    * ✅ Load blocks through reflection
    * ✅ Migrate existing blocks to external libraries
<br>

* Select Block
    * Search through tags
<br>

* Bugs
    * ✅ Fix align submenu of radial menu
<br>

* Other
    * ✅ Remove Obj library
    * Customization functionality through config file
    * Comments
    * Scripting block
    * Looping groups/areas
    * Improve grouping
    * BUG text and blocks are copied/pasted simultaneously
