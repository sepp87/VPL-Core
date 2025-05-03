### BlockSmith - Blocks to Scripts
A node flow environment built with JavaFX.<br>

See example at: https://www.youtube.com/watch?v=Oev1IJZvAPw
<br><br>

### QUICK START
Welcome to BlockSmith! Here’s how to get started:

Blocks generate, process and output data.
Double-click anywhere on the workspace to add a block.
Each block does something specific, like entering a number or doing math.
Need help? Click the (i) icon on a block to learn more about it.

Connect blocks by linking their ports.
Hover over a port to see what kind of data a port accepts or produces.
Example: A multiply block needs two numbers and gives you the result.
That’s it! You’re ready to build your first graph.
<br><br>

### CONTROLS
* Right drag: pan
* Scroll: zoom
* Left double click: create blocks
* Left click: select a block
* Left click + CMD/CTRL: multi select blocks
* Left drag: selection rectangle
* Left drag block: move selected block(s)
* Left click port: connect blocks
* Left click connection: remove connection
* Right click: radial menu
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
    * ✅ Start screen
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
