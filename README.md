DESCRIPTION<br>
A visual programming environment build with JavaFX.

See example at: https://www.youtube.com/watch?v=Oev1IJZvAPw


QUICK START
* Left drag: selection rectangle
* Right drag: pan
* Right click: radial menu
* Double click: open hub search box
* Mouse wheel: zoom


SHORTCUTS
* CTRL-C: copy selected hubs
* CTRL-V: paste copied hubs
* CTRL-G: group selected hubs
* CTRL-O: load graph
* CTRL-S: save graph
* CTRL-N: new graph
* Space: zoom to fit
* Del: delete selected hubs


CUSTOMIZE
* Create custom hubs by copying TemplateHub.java and change its behaviour
* Create your own library with hub types and add it to "build/ext/"


STYLE
* Change styles in VPLTester.java to either flat white, flat dark or default
* Customize your own style by using one of the above styles as template


TODO
* Add hints
* Add exceptions

* Add start screen
* Add menu bar
* Add customization functionality through config file
* Add comments
* Add labels to radial menu
* Add scripting hub
* Add functionality to remove connections
* Improve grouping
* Improve saving of hub settings
* BUG text and hubs are copied / pasted simultaneously
* BUG TextHub line and underline do not align
* Create template plugin project (FatJar, OneJar or UberJar which omits existing dependencies)

* (DONE) BUG getFirstInList should put out the same class as the incoming node prescribes
*        When multi dock not allowed function hub.handle_IncomingConnectionAdded() is called
         Another bug could arise at deserializing when the connection order creation is not correct
* (DONE) BUG hub not copied to correct coordinates underneath cursor
* (DONE) Add external loading of hub types
* (DONE) Improve loading of hub types
* (DONE) Fix zoom on Windows


CREDITS
Based on Dominic Singer's C# WPF version (available at https://github.com/tumcms/TUM.CMS.VPLControl). For further attributions and licenses, see the license file.
