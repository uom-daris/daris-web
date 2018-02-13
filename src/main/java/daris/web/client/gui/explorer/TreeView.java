package daris.web.client.gui.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.WhiteSpace;

import arc.gui.gwt.style.StyleRegistry;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.tree.TreeGUIEventHandler;
import arc.mf.client.util.ObjectUtil;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.object.tree.Node;
import daris.web.client.gui.DObjectGUIRegistry;
import daris.web.client.gui.object.tree.DObjectTreeGUI;
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

    private DObjectTreeGUI _treeGUI;

    private PagingControl _pc;

    private List<ContextView.Listener> _listeners;

    public TreeView(DObjectRef seekTo) {

        _seekTo = new Stack<DObjectRef>();
        if (seekTo != null) {
            _seekTo.push(seekTo);
            DObjectRef po = seekTo.parent();
            while (po != null) {
                _seekTo.push(po);
                po = po.parent();
            }
        }

        SimplePanel header = new SimplePanel();
        header.setWidth100();
        header.setHeight(HEADER_HEIGHT);
        header.setBackgroundImage(HEADER_BACKGROUND_IMAGE);

        _treeGUI = new DObjectTreeGUI(_seekTo.isEmpty() ? null : _seekTo.peek(), new TreeGUIEventHandler() {

            @Override
            public void clicked(Node n) {

            }

            @Override
            public void selected(Node n) {
                final DObjectTreeNode node = (DObjectTreeNode) n;
                if (node != null) {
                    _pc.setOffset(node.offset(), node.parentNode().numberOfChildren());
                }
                if (!_seekTo.isEmpty()) {
                    node.setSeekToChild(_seekTo.peek());
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
                notifyOfSelect((DObjectRef) node.object());
            }

            @Override
            public void addedToSelection(Node n) {
            }

            @Override
            public void deselected(Node n) {
                notifyOfDeselect((DObjectRef) n.object());
            }

            @Override
            public void opened(Node n) {
                DObjectTreeNode node = (DObjectTreeNode) n;
                if (node.equals(tree().root()) || node.equals(_treeGUI.selectedParentNode()) || !_seekTo.isEmpty()) {
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
                            } else if (_treeGUI.selectedNode() != null) {
                                for (DObjectRef co : cos) {
                                    if (co.equals(_treeGUI.selectedObject())) {
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
                if (ObjectUtil.equals(node, _treeGUI.selectedNode())) {
                    notifyOfOpen((DObjectRef) node.object());
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
        });
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
        _treeGUI.enableNodeDrag();
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

    private void notifyOfSelect(DObjectRef o) {
        if (_listeners != null) {
            for (Listener l : _listeners) {
                l.selected(o);
            }
        }
    }

    private void notifyOfDeselect(DObjectRef o) {
        if (_listeners != null) {
            for (Listener l : _listeners) {
                l.deselected(o);
            }
        }
    }

    private void notifyOfOpen(DObjectRef o) {
        if (_listeners != null) {
            for (Listener l : _listeners) {
                l.opened(o);
            }
        }
    }

    @Override
    public void gotoOffset(long offset) {
        DObjectTreeNode pn = _treeGUI.selectedParentNode();
        if (pn != null) {
            if (_treeGUI.isOpen(pn)) {
                _treeGUI.close(pn);
            }
            pn.setOffset(offset);
            _treeGUI.open(pn);
        }
    }

    @Override
    public void refreshSelected() {
        _treeGUI.refreshSelectedNode();
    }

    public void seekTo(DObjectRef o) {
        seekTo(o, true);
    }

    @Override
    public void open(DObjectRef o) {
        seekTo(o, true);
    }

    @Override
    public void seekTo(DObjectRef o, boolean refresh) {
        // Always refresh(rebuild) because no local caching.
        if (o != null) {
            _seekTo.clear();
            _seekTo.push(o);
            DObjectRef po = o.parent();
            while (po != null) {
                _seekTo.push(po);
                po = po.parent();
            }
            _treeGUI.reload();
        }
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

    @Override
    public BaseWidget widget() {
        return this;
    }

}
