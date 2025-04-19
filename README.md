### BlockSmith - Blocks to Scripts
A node flow environment built with JavaFX.<br>

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
* CMD/CTRL-A: select all blocks
* CMD/CTRL-C: copy selected blocks
* CMD/CTRL-V: paste copied blocks
* CMD/CTRL-G: group selected blocks
* CMD/CTRL-O: load graph
* CMD/CTRL-S: save graph
* CMD/CTRL-N: new graph
* CMD/CTRL-PLUS: zoom in
* CMD/CTRL-MINUS: zoom out
* CMD/CTRL-Z: undo
* CMD/CTRL-Y: redo
* Space: zoom to fit
* Del/Backspace: delete selected blocks
<br>

### FUNCTIONALITIES
* Input blocks to generate primitve data types e.g. String, Boolean, Integer, Long, Double
* Math blocks such as multiply, divide, add and substract
* List blocks to get, add, remove and replace items
* Json blocks to get properties
* Date blocks
* File blocks
* Spreadsheet blocks
* String blocks
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
    * ✅ Undo/redo
    * Auto save/load
    * ✅ Menu bar
    * ✅ Save canvas size, position and zoom factor
    * ✅ Zoom controls
<br>

* Block behaviour
    * Hints
    * ✅ Behaviour info
    * ✅ Exceptions
    * ✅ Remove connections
    * ✅ Save block size
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
    * Customization functionality through config file
    * Comments
    * Scripting block
    * Looping groups/areas
    * ✅ Improve grouping
    * BUG text and blocks are copied/pasted simultaneously
    * ✅ Remove Obj library
