package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.WhiteSpace;

import arc.gui.gwt.style.StyleRegistry;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.tree.TreeGUI;
import arc.gui.gwt.widget.tree.TreeGUIEventHandler;
import arc.gui.gwt.widget.tree.TreeNodeGUI;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.object.tree.Node;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.messages.DObjectChildCursorFromGet;
import daris.web.client.model.object.tree.DObjectTree;
import daris.web.client.model.object.tree.DObjectTreeNode;

public class TreeView extends ContainerWidget implements ContextView, PagingListener {

    public static final int HEADER_HEIGHT = ListGridHeader.HEIGHT;
    public static final Image HEADER_BACKGROUND_IMAGE = new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
            ListGridHeader.HEADER_COLOUR_LIGHT, ListGridHeader.HEADER_COLOUR_DARK);

    private Stack<DObjectRef> _seekTo;

    private DObjectTreeNode _selectedNode;

    private TreeNodeGUI _selectedNodeGUI;

    private TreeGUI _treeGUI;

    private PagingControl _pc;

    private List<ContextView.Listener> _listeners;

    public TreeView() {

        _seekTo = new Stack<DObjectRef>();

        SimplePanel header = new SimplePanel();
        header.setWidth100();
        header.setHeight(HEADER_HEIGHT);
        header.setBackgroundImage(HEADER_BACKGROUND_IMAGE);

        _treeGUI = new TreeGUI(new DObjectTree(), ScrollPolicy.AUTO, new TreeGUIEventHandler() {

            @Override
            public void clicked(Node n) {

            }

            @Override
            public void selected(Node n) {
                final DObjectTreeNode node = (DObjectTreeNode) n;
                _selectedNode = node;
                if (node != null) {
                    _pc.setOffset(_selectedNode.offset(), _selectedNode.parentNode().numberOfChildren());
                }
                if (!_seekTo.isEmpty()) {
                    new DObjectChildCursorFromGet(_seekTo.peek().citeableId(), tree().pageSize())
                            .send(new ObjectMessageResponse<Long>() {
                                @Override
                                public void responded(Long idx) {
                                    if (idx != null && idx > 0) {
                                        node.setOffset(idx - 1);
                                    } else {
                                        node.setOffset(0);
                                    }
                                    _treeGUI.open(node);
                                }
                            });
                }
            }

            @Override
            public void addedToSelection(Node n) {
            }

            @Override
            public void deselected(Node n) {
                if (n.equals(_selectedNode)) {
                    _selectedNode = null;
                }
            }

            @Override
            public void opened(Node n) {
                DObjectTreeNode node = (DObjectTreeNode) n;
                if (node.equals(tree().root()) || node.equals(selectedParentNode()) || !_seekTo.isEmpty()) {
                    node.resolveChildrenInCurrentPage(cos -> {
                        if (cos != null && !cos.isEmpty()) {
                            if (!_seekTo.isEmpty()) {
                                DObjectRef so = _seekTo.pop();
                                for (DObjectRef co : cos) {
                                    if (co.equals(so)) {
                                        _treeGUI.select(co);
                                        return;
                                    }
                                }
                            } else if (selectedNode() != null) {
                                for (DObjectRef co : cos) {
                                    if (co.equals(selectedObject())) {
                                        _treeGUI.select(co);
                                        return;
                                    }
                                }
                            } else {
                                _treeGUI.select(cos.get(0));
                            }
                        } else {
                            if (!_seekTo.isEmpty()) {
                                System.out.println("Cannot seek to: " + _seekTo.lastElement().citeableId() + "");
                                _seekTo.clear();
                            }
                        }
                    });
                }
            }

            @Override
            public void closed(Node n) {

            }

            @Override
            public void added(Node n) {

            }

            @Override
            public void removed(Node n) {

            }

            @Override
            public void changeInMembers(Node n) {

            }
        }) {
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

        };
        _treeGUI.setReloadOnOpen(true);
        _treeGUI.setMultiSelect(true);
        _treeGUI.setShowRoot(false);
        // @formatter:off
        _treeGUI.setLabelStyle(StyleRegistry.register("DTreeNodeLabel")
                .setFontFamily(DefaultStyles.FONT_FAMILY)
                .setFontWeight(FontWeight.BOLD)
                .setFontSize(11)
                .setLineHeight(100)
                .setPaddingTop(2)
                .setPaddingLeft(4)
                .setPaddingBottom(2)
                .setPaddingRight(10)
                .setWhiteSpace(WhiteSpace.NOWRAP)
                .setCursor(Cursor.DEFAULT));
        // @formatter:on
        _treeGUI.setObjectRegistry(DObjectGUIRegistry.get());
        _treeGUI.fitToParent();

        _pc = new PagingControl(tree().pageSize());
        _pc.addPagingListener(this);

        VerticalPanel vp = new VerticalPanel();
        vp.add(header);
        vp.add(_treeGUI);
        vp.add(_pc);

        initWidget(vp);
    }

    DObjectTree tree() {
        return (DObjectTree) _treeGUI.tree();
    }

    @Override
    public void gotoOffset(long offset) {
        DObjectTreeNode pn = selectedParentNode();
        if (pn != null) {
            if (_treeGUI.isOpen(pn)) {
                _treeGUI.close(pn);
            }
            pn.setOffset(offset);
            _treeGUI.open(pn);
        }
    }

    public DObjectRef selectedObject() {
        DObjectTreeNode selectedNode = selectedNode();
        return selectedNode == null ? null : (DObjectRef) selectedNode.object();
    }

    public DObjectTreeNode selectedNode() {
        return _selectedNode;
    }

    public DObjectTreeNode selectedParentNode() {
        DObjectTreeNode selectedNode = selectedNode();
        return selectedNode == null ? null : selectedNode.parentNode();
    }

    public void refreshSelectedNode() {
        DObjectTreeNode selectedNode = selectedNode();
        if (selectedNode != null) {
            selectedNode.refresh(_selectedNodeGUI);
        }
    }

    public void seekTo(DObjectRef o) {
        if (o != null) {
            _seekTo.clear();
            _seekTo.push(o);
            DObjectRef po = o.parent();
            while (po != null) {
                _seekTo.push(po);
                po = po.parent();
            }
            new DObjectChildCursorFromGet(_seekTo.peek().citeableId(), tree().pageSize())
                    .send(new ObjectMessageResponse<Long>() {
                        @Override
                        public void responded(Long idx) {
                            if (idx != null && idx > 0) {
                                tree().rootNode().setOffset(idx - 1);
                            } else {
                                tree().rootNode().setOffset(0);
                            }
                            tree().rootNode().children().cancel();
                            tree().rootNode().children().reset();
                            _treeGUI.reload();
                        }
                    });
        }
    }

    @Override
    public void open(DObjectRef o) {
        // TODO Auto-generated method stub

    }

    @Override
    public void seekTo(DObjectRef o, boolean refresh) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addListener(Listener l) {
        if (_listeners == null) {
            _listeners = new ArrayList<Listener>();
        }
        _listeners.add(l);
    }

    @Override
    public void removeListener(Listener l) {
        if (_listeners != null) {
            _listeners.remove(l);
        }
    }

}
