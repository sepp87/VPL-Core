package btscore;

/**
 *
 * @author joostmeulenkamp
 */
public class Notes {
    
}
// TODO / WIP
// put stylesheets stuff into AppStylesheetsHelper
//
//
// NOTES
// RemoveSelectedBlocksCommand - removes a block's connections twice
//      - 1) WorkspaceModel.removeConnectionModels(blockModel) removes and returns the removed connections for undo/redo
//      - 2) WorkspaceModel.removeBlockModel(blockModel) removes and returns the removed block for undo/redo. It also removes its connections, but in this case they have already been removed.
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
//
// GUIDELINES
// Put everything inside the models that needs to be saved and all business logic needed to make a script work in headless mode
// TODO naming conventions for event handlers & change listeners and placement of handlers / listeners in code
// When a dedicated listener is needed, it should be declared directly above the method it calls, so it easier to find it
// Naming
//            Name	Purpose                                 Implied Behavior                    Good For
//            *Index	Lookup tables, mostly passive           Read-heavy, structure-only          Groups, hierarchies
//            *Registry	Active coordination via registration	Event-aware, dynamic resolution     Plugins, wireless ports
//            *Manager	Heavy orchestration, lifecycles         Stateful control, complex logic     NodeManager, SessionManager
// 
//
// TESTS 
// Create connection - Link backward and link forward
// MethodBlock - lacing of lists
// Remove block - remove block and connections
// Auto-create connection undo/redo
//
// REMINDERS / THOUGTHS
// do block.onIncomingConnectionAdded/Removed make sense, what could their use be? not trigger processing