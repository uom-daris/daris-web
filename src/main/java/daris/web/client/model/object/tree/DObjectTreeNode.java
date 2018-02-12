package daris.web.client.model.object.tree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.tree.TreeNodeGUI;
import arc.gui.image.Image;
import arc.mf.client.util.DynamicBoolean;
import arc.mf.client.util.Fuzzy;
import arc.mf.client.util.ListUtil;
import arc.mf.client.util.ObjectUtil;
import arc.mf.event.Filter;
import arc.mf.event.SystemEvent;
import arc.mf.object.CollectionResolveHandler;
import arc.mf.object.ObjectResolveHandler;
import arc.mf.object.tree.Container;
import arc.mf.object.tree.Node;
import arc.mf.object.tree.NodeEventMonitor;
import arc.mf.object.tree.NodeListener;
import arc.mf.object.tree.RemoteNode;
import arc.mf.object.tree.TreeNodeAddHandler;
import arc.mf.object.tree.TreeNodeContentsHandler;
import arc.mf.object.tree.TreeNodeDescriptionHandler;
import arc.mf.object.tree.TreeNodeRemoveHandler;
import arc.mf.session.Session;
import daris.web.client.gui.Resource;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.HtmlBuilder;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectChildrenRef;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.event.DObjectEvent;
import daris.web.client.model.object.messages.DObjectExists;

public class DObjectTreeNode implements Container, RemoteNode {

    public static final arc.gui.image.Image FOLDER_ICON = new arc.gui.image.Image(
            Resource.INSTANCE.folder16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image FOLDER_OPEN_ICON = new arc.gui.image.Image(
            Resource.INSTANCE.folderOpen16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image DATASET_ICON = new arc.gui.image.Image(
            Resource.INSTANCE.document32().getSafeUri().asString(), 16, 16);

    private DObjectTree _tree;
    private DObjectTreeNode _pn;
    private DObjectRef _o;
    private long _stateId;
    private Map<Object, NodeListener> _listeners;
    private DObjectChildrenRef _children;

    private long _start = 0;

    private HTML _adornment;

    public DObjectTreeNode(DObjectTree tree, DObjectTreeNode pn, DObjectRef o) {
        _tree = tree;
        _pn = pn;
        _o = o;
        _stateId = 0;
        _listeners = new LinkedHashMap<Object, NodeListener>();
        _children = (_o != null && _o.isDataset()) ? null
                : new DObjectChildrenRef(_o, _tree.filter(_o), _tree.sortKey());
        _adornment = new HtmlBuilder().setFontFamily(DefaultStyles.FONT_FAMILY).setFontSize(9).setHeight(16)
                .setLineHeight(16).setTextAlign(TextAlign.CENTER).build();
        if (_o != null && _o.numberOfChildren() > 0) {
            long end = _start + _tree.pageSize();
            if (end > _o.numberOfChildren()) {
                end = _o.numberOfChildren();
            }
            if (_start == 0 && end == _o.numberOfChildren()) {
                _adornment.setHTML("" + _o.numberOfChildren());
            } else {
                _adornment.setHTML("" + _start + ".." + end + "/" + _o.numberOfChildren());
            }
        }
        _adornment.setColour(RGB.BLUE);
        _adornment.setBorder(1, BorderStyle.DOTTED, RGB.GREY_999);
        _adornment.setBorderRadius(3);
    }

    @Override
    public String type() {
        return _o.referentTypeName();
    }

    @Override
    public Image icon() {
        if (_o != null && _o.isDataset()) {
            return DATASET_ICON;
        } else {
            return FOLDER_ICON;
        }
    }

    @Override
    public String name() {
        if (_o.isProject()) {
            return _o.citeableId() + (_o.name() == null ? "" : ": " + _o.name());
        } else {
            return CiteableIdUtils.ordinal(_o.citeableId()) + (_o.name() == null ? "" : ": " + _o.name());
        }
    }

    @Override
    public String path() {
        return _o.citeableId();
    }

    @Override
    public void description(TreeNodeDescriptionHandler dh) {
        String description = _o.resolved() ? _o.referent().description()
                : (_o.referentTypeName() + " " + _o.citeableId());
        if (dh != null) {
            dh.description(description);
        }
    }

    @Override
    public List<BaseWidget> adornments() {
        if (numberOfChildren() == 0) {
            return null;
        }
        return ListUtil.list(_adornment);
    }

    @Override
    public Object object() {
        return _o;
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public Object subscribe(DynamicBoolean descend, NodeListener l) {
        Object key = NodeEventMonitor.subscribe(this, descend, l);
        _listeners.put(key, l);
        return key;
    }

    @Override
    public void unsubscribe(Object key) {
        NodeEventMonitor.unsubscribe(key);
        _listeners.remove(key);
    }

    @Override
    public void discard() {
        _listeners.clear();
    }

    @Override
    public Filter systemEventFilter(DynamicBoolean descend) {
        if (_o != null) {
            return new Filter("pssd-object", _o.citeableId(), descend);
        } else {
            return null;
        }
    }

    @Override
    public void process(final SystemEvent se, final NodeListener nl) {
        final DObjectEvent de = (DObjectEvent) se;
        DObjectEvent.isRelavent(de, r -> {
            if (r) {
                switch (de.action()) {
                case DESTROY:
                    destroyed(de.id(), de.objectRef(), nl);
                    break;
                case CREATE:
                    created(de.id(), de.objectRef(), nl);
                    break;
                case MODIFY:
                    modified(de.id(), de.objectRef(), nl);
                    break;
                case MEMBERS:
                    membersChanged(de.id(), de.objectRef(), nl);
                    break;
                default:
                    break;

                }
            }
        });
    }

    private void destroyed(long stateId, DObjectRef co, NodeListener nl) {
        _o.reset();
        _o.resolve(new ObjectResolveHandler<DObject>() {

            @Override
            public void resolved(DObject ooo) {
                nl.modified(DObjectTreeNode.this);
                if (_stateId != stateId) {
                    _stateId = stateId;
                }
                nl.removed(DObjectTreeNode.this, new DObjectTreeNode(_tree, DObjectTreeNode.this, co));
                if (_children != null) {
                    _start = 0L;
                    _children.reset();
                    nl.changeInMembers(DObjectTreeNode.this);
                }
            }
        });

    }

    private void created(long stateId, DObjectRef co, NodeListener nl) {
        co.resolve(new ObjectResolveHandler<DObject>() {
            @Override
            public void resolved(DObject coo) {
                _o.reset();
                _o.resolve(new ObjectResolveHandler<DObject>() {

                    @Override
                    public void resolved(DObject ooo) {
                        nl.modified(DObjectTreeNode.this);
                        if (_stateId != stateId) {
                            _stateId = stateId;
                        }
                        nl.added(DObjectTreeNode.this, new DObjectTreeNode(_tree, DObjectTreeNode.this, co), -1);
                        if (_children != null) {
                            _start = 0L;
                            _children.reset();
                            nl.changeInMembers(DObjectTreeNode.this);
                        }
                    }
                });
            }
        });
    }

    private void modified(long stateId, DObjectRef co, NodeListener nl) {
        _o.reset();
        _o.resolve(new ObjectResolveHandler<DObject>() {

            @Override
            public void resolved(DObject ooo) {
                if (_stateId != stateId) {
                    _stateId = stateId;
                }
                nl.modified(DObjectTreeNode.this);
            }
        });
    }

    private void membersChanged(long stateId, DObjectRef co, NodeListener nl) {
        if (_stateId != stateId) {
            _stateId = stateId;
        }
        if (_children != null) {
            _start = 0L;
            _children.reset();
            nl.changeInMembers(DObjectTreeNode.this);
        }
    }

    @Override
    public boolean sorted() {
        return false;
    }

    @Override
    public Image openIcon() {
        if (_o != null && _o.isDataset()) {
            return DATASET_ICON;
        } else {
            return FOLDER_OPEN_ICON;
        }
    }

    @Override
    public Fuzzy hasChildren() {
        if (_o == null) {
            if (_children.totalNumberOfMembers() > 0) {
                return Fuzzy.YES;
            } else if (_children.totalNumberOfMembers() == 0) {
                return Fuzzy.NO;
            } else {
                return Fuzzy.MAYBE;
            }
        } else {
            if (_o.numberOfChildren() < 0) {
                return Fuzzy.MAYBE;
            } else if (_o.numberOfChildren() == 0) {
                return Fuzzy.NO;
            } else {
                return Fuzzy.YES;
            }
        }
    }

    @Override
    public void contents(long start, long end, TreeNodeContentsHandler ch) {
        if (_children == null) {
            // data sets are leaf nodes, they have no children
            ch.loaded(0, 0, 0, null);
            _adornment.setHTML("" + 0);
        } else {
            if (start > 0) {
                _start = start;
            }
            if (end > _start + pageSize()) {
                end = _start + pageSize();
            }
            resolveChildrenInCurrentPage(cos -> {
                if (cos == null || cos.isEmpty()) {
                    ch.loaded(0, 0, 0, null);
                    _adornment.setHTML("" + 0);
                } else {
                    List<Node> nodes = new ArrayList<Node>(cos.size());
                    for (DObjectRef co : cos) {
                        nodes.add(new DObjectTreeNode(_tree, DObjectTreeNode.this, co));
                    }
                    ch.loaded(_start, _start + nodes.size(), numberOfChildren(), nodes);
                    if (_start == 0 && nodes.size() < pageSize()) {
                        _adornment.setHTML("" + nodes.size());
                    } else {
                        _adornment.setHTML("" + _start + ".." + (_start + nodes.size()) + "/" + numberOfChildren());
                    }
                }
            });
        }
    }

    public long numberOfChildren() {
        if (_children == null) {
            return 0;
        }
        if (_o == null) {
            return _children.totalNumberOfMembers();
        } else {
            return _children.totalNumberOfMembers() >= 0 ? _children.totalNumberOfMembers() : _o.numberOfChildren();
        }
    }

    public DObjectChildrenRef children() {
        return _children;
    }

    public int pageSize() {
        return _tree.pageSize();
    }

    @Override
    public void add(Node cn, TreeNodeAddHandler ah) {
        if (!_listeners.isEmpty()) {
            List<NodeListener> ls = ListUtil.copyOf(_listeners.values());
            for (NodeListener l : ls) {
                l.added(this, cn, -1);
            }
        }
        if (ah != null) {
            ah.added(cn);
        }
    }

    @Override
    public void remove(Node cn, TreeNodeRemoveHandler rh) {
        if (!_listeners.isEmpty()) {
            List<NodeListener> ls = ListUtil.copyOf(_listeners.values());
            for (NodeListener l : ls) {
                l.removed(this, cn);
            }
        }
        Session.execute("asset.exists", "<cid>" + _o.citeableId() + "</cid>", (xe, out) -> {
            if (!xe.booleanValue("exists")) {
                parentNode().remove(DObjectTreeNode.this, null);
            }
        });
        if (rh != null) {
            rh.removed(cn);
        }
    }

    @Override
    public boolean membersAreNodes() {
        return false;
    }

    @Override
    public String toString() {
        return _o.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof DObjectTreeNode)) {
            DObjectTreeNode n = (DObjectTreeNode) o;
            return ObjectUtil.equals(path(), n.path());
        }
        return false;
    }

    public DObjectTreeNode parentNode() {
        return _pn;
    }

    public long offset() {
        return _start;
    }

    public void setOffset(long offset) {
        _start = offset;
    }

    public void resolveChildrenInCurrentPage(CollectionResolveHandler<DObjectRef> rh) {
        if (_children == null) {
            try {
                rh.resolved(null);
            } catch (Throwable e) {
                Session.displayError("Resolving child objects of " + path(), e);
            }
        } else {
            _children.resolve(_start, _start + pageSize(), cos -> {
                long total = _children.totalNumberOfMembers();
                if (total == 0) {
                    _start = 0L;
                }
                rh.resolved(cos);
            });
        }

    }

    public void refresh(final TreeNodeGUI gui) {
        new DObjectExists(_o).send(exists -> {
            if (exists) {
                _o.reset();
                _o.resolve(o -> {
                    if (_listeners != null && !_listeners.isEmpty()) {
                        List<NodeListener> ls = new ArrayList<NodeListener>(_listeners.values());
                        for (NodeListener l : ls) {
                            l.modified(DObjectTreeNode.this);
                        }
                    }
                    _children.reset();
                    gui.reopen();
                });
            } else {
                parentNode().remove(DObjectTreeNode.this, null);
            }
        });
    }

}
