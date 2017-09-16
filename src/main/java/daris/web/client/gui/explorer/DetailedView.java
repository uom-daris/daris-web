package daris.web.client.gui.explorer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.ResizeListener;
import arc.gui.gwt.widget.Resizeable;
import arc.gui.gwt.widget.Selectable;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.object.display.ObjectDetailsDisplay;
import arc.gui.object.register.ObjectGUI;
import arc.gui.object.register.ObjectGUIRegistry;
import arc.gui.object.register.ObjectUpdateHandle;
import arc.gui.object.register.ObjectUpdateListener;
import arc.gui.object.register.SystemObjectGUIRegistry;
import arc.gui.util.HTMLUtil;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.MustBeValid;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.util.StateChangeListener;
import arc.mf.client.util.Validity;

public class DetailedView extends ContainerWidget implements ObjectDetailsDisplay, MustBeValid, StateChangeListener {

    private static class ObjectDetails {
        private Object _o;
        private Object _details;

        public ObjectDetails(Object o, Object details) {
            _o = o;
            _details = details;
        }

        public Object object() {
            return _o;
        }

        public Object details() {
            return _details;
        }

        public void setDetails(Object details) {
            _details = details;
        }

        public boolean isSameObject(Object o) {
            // Jason: don't check the class - that does not account for
            // some scenarias (e.g. update to something that matches but a
            // different class)
            //
            return _o.equals(o);
        }

    }

    private SimplePanel _view;
    private boolean _forEdit;
    private Stack<ObjectDetails> _os;
    private ObjectGUIRegistry _ogr;
    private ScrollPolicy _sp;

    private Object _currentObject;
    private ObjectUpdateHandle _ouh;

    private DropHandler _dh;
    private boolean _displayLoading;
    private boolean _displayContextMenu;
    private boolean _allowDrop;
    private Widget _emptyContent;

    private MustBeValid _mbv;
    private List<StateChangeListener> _stateChangeListeners;
    private Map<Object, Object> _settings;
    private boolean _readOnly;

    public DetailedView() {
        this(ScrollPolicy.BOTH);
    }

    public DetailedView(ScrollPolicy sp) {
        _view = new SimplePanel();
        _view.setBackgroundColour(RGB.WHITE);
        _view.setSelectable(Selectable.TEXT);

        makeDropTarget(new DropHandler() {
            @Override
            public DropCheck checkCanDrop(Object data) {
                if (_dh != null) {
                    return _dh.checkCanDrop(data);
                }

                return DropCheck.CANNOT;
            }

            @Override
            public void drop(BaseWidget target, List<Object> data, DropListener dl) {
                if (_dh != null) {
                    _dh.drop(target, data, dl);
                    return;
                }

                dl.dropped(DropCheck.CANNOT);
            }
        });

        _os = new Stack<ObjectDetails>();
        _forEdit = false;
        _ogr = SystemObjectGUIRegistry.get();
        _currentObject = null;
        _ouh = null;
        _dh = null;
        _sp = sp;

        _view.fitToParent();
        initWidget(_view);

        // if ( sp == null || sp.equals(ScrollPolicy.NONE) ) {
        // _view.fitToParent();
        // initWidget(_view);
        // } else {
        // initWidget(new ScrollPanel(_view,sp));
        // }

        fitToParent();
        _emptyContent = null;
        _displayLoading = true;
        _displayContextMenu = false;
        _allowDrop = true;
        _mbv = null;
        _stateChangeListeners = null;
        _settings = new HashMap<Object, Object>();
    }

    /**
     * Will the loading message be shown?
     * 
     * @return
     */
    public boolean displayLoadingMessage() {
        return _displayLoading;
    }

    /**
     * Should the loading information display be shown?
     * 
     * @param display
     */
    public void setDisplayLoadingMessage(boolean display) {
        _displayLoading = display;
    }

    /**
     * Returns the currently configured empty panel content
     * 
     * @return
     */
    public Widget emptyContent() {
        return _emptyContent;
    }

    /**
     * Configures empty panel content
     * 
     * @param display
     */
    public void setEmptyContent(Widget w) {
        _emptyContent = w;
    }

    /**
     * renders the empty content message
     * 
     * @return
     */
    public void showEmptyContent() {
        if (_emptyContent != null) {
            _view.setContent(_emptyContent);
        }
    }

    /**
     * Padding around the view content. If not set, defaults to 0.
     * 
     * @param p
     */
    public void setViewContentPadding(int p) {
        _view.setPadding(p);
    }

    /**
     * Is the display a read-only view? This will be passed to context menu
     * construction.
     * 
     * @return
     */
    public boolean readOnly() {
        return _readOnly;
    }

    /**
     * Sets the display to read-only. This affects menu items.
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        _readOnly = readOnly;
    }

    /**
     * Is this viewer showing objects in edit mode?
     * 
     * @return
     */
    public boolean forEdit() {
        return _forEdit;
    }

    /**
     * Indicates the objects displayed in this navigator should be displayed for
     * editing or not.
     * 
     * @param forEdit
     */
    public void setForEdit(boolean forEdit) {
        _forEdit = forEdit;
    }

    public void setObjectRegistry(ObjectGUIRegistry ogr) {
        _ogr = ogr;
    }

    /**
     * Returns the object GUI (handler) for the given object.
     * 
     * @param o
     * @return
     */
    public ObjectGUI objectGUI(Object o) {
        if (_ogr != null) {
            ObjectGUI og = _ogr.guiFor(o);
            if (og != null) {
                return og;
            }
        }

        return null;
    }

    /**
     * Is the given object being displayed currently?
     * 
     * @param o
     * @return
     */
    public boolean displaying(Object o) {
        ObjectDetails od = current();
        if (od == null) {
            return false;
        }

        if (ObjectUtil.equals(od.object(), o)) {
            return true;
        }

        return false;
    }

    public void clear() {
        clearDisplay();
        _os.clear();
    }

    public void clear(Object o) {
        if (o == null) {
            return;
        }

        ObjectDetails od = detailsFor(o);
        if (od != null) {
            _os.remove(od);
        }

        clearDisplay();

        ObjectDetails cd = current();
        if (cd != null) {
            _currentObject = cd.object();
            showDetails(cd.object(), cd.details());
        }
    }

    private void clearDisplay() {
        if (_mbv != null) {
            _mbv.removeChangeListener(this);
            _mbv = null;
        }

        cancelUpdateMonitor();

        _currentObject = null;
        _dh = null;
        _view.clear();
        _view.setContextMenu(null);
        showEmptyContent();

    }

    private ObjectDetails current() {
        if (_os.isEmpty()) {
            return null;
        }

        return _os.peek();
    }

    private ObjectDetails detailsFor(Object o) {
        if (_os.isEmpty()) {
            return null;
        }

        for (int i = _os.size() - 1; i >= 0; i--) {
            ObjectDetails ods = _os.get(i);
            if (ods.isSameObject(o)) {
                return ods;
            }
        }

        return null;
    }

    public void display(Object o, Object details) {

        // See if new details for the same object. That is, we may have
        // pushed another object onto the viewer stack since requesting
        // the display data..
        ObjectDetails ods = detailsFor(o);
        if (ods != null) {

            // If still viewing this object, then display..
            if (ods.isSameObject(o)) {

                if (!ObjectUtil.equals(_currentObject, o)) {
                    clearDisplay();

                    _currentObject = o;

                    if (details instanceof MustBeValid) {
                        _mbv = (MustBeValid) details;
                        _mbv.addChangeListener(this);
                    }
                }

                if (details instanceof String) {
                    details = new HTML(details.toString());
                }

                ods.setDetails(details);
                showDetails(o, details);
            }
        }

    }

    public void failedToLoad(Object o) {

    }

    private ResizeListener _rl;

    private void showDetails(Object o, Object details) {
        Widget cc = _view.widget();
        if (cc != null && _rl != null) {
            if (cc instanceof Resizeable) {
                Resizeable r = (Resizeable) cc;
                r.removeResizeListener(_rl);
            }
        }

        if (details == null) {
            _view.clear();
        } else {

            ScrollPolicy sp = _sp;
            if (details instanceof BaseWidget) {
                BaseWidget bw = (BaseWidget) details;
                if (bw.isWidth100() && bw.isHeight100()) {
                    sp = null;
                } else if (bw.isWidth100()) {
                    if (sp != null && !sp.equals(ScrollPolicy.NONE)) {
                        sp = ScrollPolicy.VERTICAL;
                    }
                } else if (bw.isHeight100()) {
                    if (sp != null && !sp.equals(ScrollPolicy.NONE)) {
                        sp = ScrollPolicy.HORIZONTAL;
                    }
                }
            }

            if (sp != null && !sp.equals(ScrollPolicy.NONE)) {
                _view.setContent(new ScrollPanel((Widget) details, sp));
            } else {
                _view.setContent((Widget) details);
            }
        }

        _dh = null;

        if (_displayContextMenu || _allowDrop) {
            ObjectGUI og = objectGUI(o);

            if (_displayContextMenu) {
                _view.setContextMenu(og.actionMenu(window(), o, null, _readOnly));
            }

            if (_allowDrop) {
                _dh = og.dropHandler(o);
            }
        }

        if (details instanceof Resizeable) {
            if (_rl == null) {
                _rl = new ResizeListener() {
                    public Widget widget() {
                        return _view;
                    }

                    public void resized(long w, long h) {
                        _view.childResized();
                    }

                };
            }

            Resizeable r = (Resizeable) details;
            r.addResizeListener(_rl);
        }

        // Cancel any existing monitoring handle - including for the same
        // object.
        cancelUpdateMonitor();

        // Create a monitor - and if the object changes (state), then redisplay.
        final ObjectGUI og = objectGUI(o);
        _ouh = og.createUpdateMonitor(o, new ObjectUpdateListener() {
            @Override
            public void updated(Object o) {
                og.displayDetails(o, DetailedView.this, forEdit());
            }
        });

    }

    private void loading(Object o, String msg) {
        boolean isFirst = _os.isEmpty();

        if (isFirst) {
            clearDisplay();
        }

        // Already in the stack? Then remove and re-insert (at the top)..
        ObjectDetails ods = detailsFor(o);
        if (ods == null) {
            ods = new ObjectDetails(o, null);
        } else {
            _os.remove(ods);
        }

        _os.push(ods);

        if (isFirst && msg != null) {
            HTML hm = new HTML(msg);
            hm.setMargin(5);

            _view.setContent(hm);
        }
    }

    /**
     * Sets the current object for display - clears all other displays.
     * 
     * @param o
     */
    public void loadAndDisplayObject(Object o) {
        loadAndDisplayObject(o, forEdit(), false);
    }

    public void reloadAndDisplayObject(Object o) {
        clear(o);
        loadAndDisplayObject(o, forEdit(), true);
    }

    public void loadAndDisplayObject(Object o, boolean forEdit, boolean refresh) {
        if (!refresh && ObjectUtil.equals(o, _currentObject)) {
            return;
        }

        if (_currentObject != null) {
            removeLoadAndDisplayObject(_currentObject);
        }

        ObjectGUI og = objectGUI(o);

        _currentObject = o;

        if (og == null) {
            clear();
        } else {
            String msg = null;

            if (og.needToResolve(o)) {
                if (_displayLoading) {
                    String id = og.idToString(o);
                    msg = HTMLUtil.busy("Loading " + id + " .. ");
                }
            }

            loading(o, msg);

            og.displayDetails(o, this, forEdit);
        }
    }

    /**
     * Adds an object to the display stack.
     * 
     * @param o
     */
    public void addLoadAndDisplayObject(Object o) {
        ObjectGUI og = objectGUI(o);
        if (og == null) {
            return;
        }

        String msg = null;

        if (og.needToResolve(o)) {
            if (_displayLoading) {
                String id = og.idToString(o);
                msg = "Loading " + id + " .. ";
            }
        }

        loading(o, msg);

        og.displayDetails(o, this, false);
    }

    public void removeLoadAndDisplayObject(Object o) {
        ObjectDetails ods = detailsFor(o);
        if (ods == null) {
            return;
        }

        _os.remove(ods);

        ods = current();
        if (ods != null) {
            showDetails(o, ods.details());
        }
    }

    @Override
    public void notifyOfChangeInState() {
        if (_stateChangeListeners != null) {
            for (StateChangeListener l : _stateChangeListeners) {
                l.notifyOfChangeInState();
            }
        }
    }

    @Override
    public boolean changed() {
        if (_mbv == null) {
            return false;
        }

        return _mbv.changed();
    }

    @Override
    public void addChangeListener(StateChangeListener listener) {
        if (_stateChangeListeners == null) {
            _stateChangeListeners = new Vector<StateChangeListener>(2);
        }

        _stateChangeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(StateChangeListener listener) {
        if (_stateChangeListeners == null) {
            return;
        }

        _stateChangeListeners.remove(listener);
    }

    @Override
    public Validity valid() {
        if (_mbv == null) {
            return IsValid.INSTANCE;
        }

        return _mbv.valid();
    }

    @Override
    public Object settings(Object key) {
        return _settings.get(key);
    }

    @Override
    public void setSettings(Object key, Object settings) {
        _settings.put(key, settings);
    }

    @Override
    public void onDetach() {
        cancelUpdateMonitor();

        super.onDetach();
    }

    private void cancelUpdateMonitor() {
        if (_ouh == null) {
            return;
        }

        _ouh.cancel();
        _ouh = null;
    }

    public void setDisplayContextMenu(boolean displayContextMenu) {
        _displayContextMenu = displayContextMenu;
    }

    public boolean isCurrentObject(Object o) {
        if (o != null) {
            return o.equals(_currentObject);
        }
        return false;
    }
}