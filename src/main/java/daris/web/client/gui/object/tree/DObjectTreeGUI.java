package daris.web.client.gui.object.tree;

import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.tree.TreeGUI;
import arc.gui.gwt.widget.tree.TreeGUIEventHandler;
import arc.gui.gwt.widget.tree.TreeNodeGUI;
import arc.mf.object.tree.Node;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.tree.DObjectTree;
import daris.web.client.model.object.tree.DObjectTreeNode;

public class DObjectTreeGUI extends TreeGUI {

    private TreeNodeGUI _selectedNodeGUI;
    private DObjectTreeNode _selectedNode;

    public DObjectTreeGUI(DObjectRef seekToProject, TreeGUIEventHandler teh) {
        super(new DObjectTree(seekToProject), ScrollPolicy.AUTO);
        ((DObjectTree) tree()).setTreeGUI(this);
        setEventHandler(new TreeGUIEventHandler() {

            @Override
            public void clicked(Node n) {
                teh.clicked(n);
            }

            @Override
            public void selected(Node n) {
                _selectedNode = (DObjectTreeNode) n;
                teh.selected(n);
            }

            @Override
            public void addedToSelection(Node n) {
                teh.addedToSelection(n);
            }

            @Override
            public void deselected(Node n) {
                if (n.equals(_selectedNode)) {
                    _selectedNode = null;
                }
                teh.deselected(n);
            }

            @Override
            public void opened(Node n) {
                teh.opened(n);
            }

            @Override
            public void closed(Node n) {
                teh.closed(n);
            }

            @Override
            public void added(Node n) {
                teh.added(n);
            }

            @Override
            public void removed(Node n) {
                teh.removed(n);
            }

            @Override
            public void changeInMembers(Node n) {
                teh.changeInMembers(n);
            }
        });
    }

    @Override
    protected boolean selectNode(TreeNodeGUI ng, boolean add) {
        boolean r = super.selectNode(ng, add);
        if (r) {
            _selectedNodeGUI = ng;
            _selectedNode = (DObjectTreeNode) ng.node();
        }
        return r;
    }

    @Override
    protected boolean deselectNode(TreeNodeGUI ng, boolean removeLast) {
        boolean r = super.deselectNode(ng, removeLast);
        if (r) {
            _selectedNodeGUI = null;
            if (ng.node().equals(_selectedNode)) {
                _selectedNode = null;
            }
        }
        return r;
    }

    public void deselectAll() {
        if (_selectedNodeGUI != null) {
            deselectNode(_selectedNodeGUI,true);
        }
    }

    public DObjectTreeNode selectedNode() {
        return _selectedNode;
    }

    public TreeNodeGUI selectedNodeGUI() {
        return _selectedNodeGUI;
    }

    public DObjectRef selectedObject() {
        DObjectTreeNode selectedNode = selectedNode();
        return selectedNode == null ? null : (DObjectRef) selectedNode.object();
    }

    public DObjectTreeNode selectedParentNode() {
        DObjectTreeNode selectedNode = selectedNode();
        return selectedNode == null ? null : selectedNode.parentNode();
    }

    public void refreshSelectedNode() {
        if (_selectedNodeGUI != null) {
            _selectedNode.refresh(_selectedNodeGUI);
        }
    }

    public boolean isSelected(DObjectTreeNode node) {
        if (node == null || selectedNode() == null) {
            return false;
        }
        return selectedNode().object().equals(node.object());
    }

    public boolean isSelected(DObjectRef o) {
        if (o == null || selectedNode() == null) {
            return false;
        }
        return selectedNode().object().equals(o);
    }

}
