package daris.web.client.gui.query;

import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.input.CheckBox;
import arc.gui.gwt.widget.menu.ActionMenu;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.window.Window;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.Menu;
import arc.gui.window.WindowProperties;
import daris.web.client.gui.Resource;
import daris.web.client.gui.explorer.Explorer;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.util.ButtonUtil;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.query.Action;
import daris.web.client.model.query.DObjectQueryResultCollectionRef;
import daris.web.client.model.shoppingcart.ActiveShoppingCart;

public abstract class QueryInterface {

    public static final arc.gui.image.Image ICON_RESET = new arc.gui.image.Image(
            Resource.INSTANCE.arrowRefreshBlack16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_SEARCH = new arc.gui.image.Image(
            Resource.INSTANCE.arrowRightBlack16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_BACK = new arc.gui.image.Image(
            Resource.INSTANCE.arrowLeftBlack16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_REFRESH = new arc.gui.image.Image(
            Resource.INSTANCE.refreshGreen16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_ACTION = new arc.gui.image.Image(
            Resource.INSTANCE.caretDownBlack16().getSafeUri().asString(), 12, 12);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_ALL = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartColor16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_DOWNLOAD_ALL = new arc.gui.image.Image(
            Resource.INSTANCE.downloadGold16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_EXPORT_XML = new arc.gui.image.Image(
            Resource.INSTANCE.xml16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_EXPORT_CSV = new arc.gui.image.Image(
            Resource.INSTANCE.csv16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartGreen16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_DOWNLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.downloadBlue16().getSafeUri().asString(), 16, 16);

    private TabPanel _tp;
    private int _activeTabId;

    /*
     * query settings
     */
    private int _queryTabId;
    private VerticalPanel _queryVP;
    private QuerySettingsForm _queryForm;
    private Button _resetButton;
    private Button _searchButton;

    /*
     * result collection
     */
    private DObjectQueryResultCollectionRef _rc;

    /*
     * query result
     */
    private int _resultTabId;
    private VerticalPanel _resultVP;
    private QueryResultForm<DObjectRef> _resultForm;
    private Button _backButton;
    private Button _refreshButton;
    private Button _actionButton;
    private Menu _actionMenu;

    private boolean _viewSelectedResult = true;

    @SuppressWarnings("rawtypes")
    protected QueryInterface(String prefixFilter) {

        _tp = new TabPanel() {

            protected void activated(int id) {
                _activeTabId = id;
                if (_activeTabId == _resultTabId) {
                    if (_rc != null) {
                        _rc.setWhere(_queryForm.toQueryString());
                    }
                    if (_resultForm != null) {
                        _resultForm.refresh();
                    }
                    activatedResultTab();
                }
                if (_activeTabId == _queryTabId) {
                    activatedQueryTab();
                }
            }

        };
        _tp.fitToParent();

        /*
         * query settings
         */
        _queryVP = new VerticalPanel();
        _queryVP.fitToParent();

        _queryForm = new QuerySettingsForm(prefixFilter) {

            @Override
            protected void addToForm(FilterForm form) {
                addFilterItems(form);
            }
        };
        _queryVP.add(_queryForm.gui());

        ButtonBar queryBB = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.CENTER);
        queryBB.setHeight(32);

        _resetButton = ButtonUtil.createButton(ICON_RESET, "Reset", "Reset to default values", true);
        _resetButton.setWidth(100);
        _resetButton.addClickHandler(e -> {
            _queryForm.reset();
        });
        queryBB.add(_resetButton);

        _searchButton = ButtonUtil.createButton(ICON_SEARCH, "Search", "Search for specified objects", true);
        _searchButton.setWidth(100);
        _searchButton.setEnabled(_queryForm.valid().valid());
        _searchButton.addClickHandler(e -> {
            _tp.setActiveTabById(_resultTabId);
        });
        queryBB.add(_searchButton);

        _queryVP.add(queryBB);

        _queryTabId = _tp.addTab("Query", "Query specifications", _queryVP);

        /*
         * init result
         */
        _rc = new DObjectQueryResultCollectionRef(_queryForm.toQueryString());
        _rc.setAction(Action.GET_VALUE);

        initializeQuery(_rc);

        _resultVP = new VerticalPanel();
        _resultVP.fitToParent();

        _resultForm = new QueryResultForm<DObjectRef>(_rc);
        _resultForm.addSelectionHandler(new SelectionHandler<DObjectRef>() {

            @Override
            public void selected(DObjectRef o) {
                if (_viewSelectedResult) {
                    Explorer.updateHistoryToken(o.parent(), o, true);
                }
                updateActionMenu();
            }

            @Override
            public void deselected(DObjectRef o) {

            }
        });
        _resultVP.add(_resultForm.gui());

        ButtonBar resultBB = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.CENTER);
        resultBB.setHeight(32);

        _backButton = ButtonUtil.createButton(ICON_BACK, "Go back", "Go back to refine the query", true);
        _backButton.setWidth(100);
        _backButton.addClickHandler(e -> {
            _tp.setActiveTabById(_queryTabId);
        });
        resultBB.add(_backButton);

        _refreshButton = ButtonUtil.createButton(ICON_REFRESH, "Refresh", "Re-execute the query", true);
        _refreshButton.setWidth(100);
        _refreshButton.addClickHandler(e -> {
            _resultForm.refresh();
        });
        resultBB.add(_refreshButton);

        _actionButton = ButtonUtil.createButton(ICON_ACTION, "Action", "Action", true);
        _actionButton.setWidth(100);
        _actionButton.addClickHandler(e -> {
            ActionMenu.showAt(e.getClientX(), e.getClientY(), _actionMenu);
        });
        resultBB.add(_actionButton);

        CheckBox cb = new CheckBox("View selected result", _viewSelectedResult);
        cb.addChangeListener(new CheckBox.Listener() {

            @Override
            public void changed(CheckBox cb) {
                _viewSelectedResult = cb.checked();
            }
        });
        resultBB.add(cb);

        _resultVP.add(resultBB);

        _resultTabId = _tp.addTab("Result", "Query result", _resultVP);

        _tp.setActiveTabById(_queryTabId);

    }

    protected abstract void initializeQuery(DObjectQueryResultCollectionRef rc);

    protected abstract void addFilterItems(FilterForm form);

    protected void activatedResultTab() {

    }

    protected void activatedQueryTab() {

    }

    protected void updateActionMenu() {
        if (_actionMenu == null) {
            _actionMenu = new Menu();
        } else {
            _actionMenu.clear();
        }
        if (_rc.totalNumberOfMembers() > 0) {
            _actionMenu.add(new ActionEntry(ICON_SHOPPINGCART_ALL, "Add all to shopping cart",
                    "Add all to shopping cart", () -> {
                        ActiveShoppingCart.resolve(false, cart -> {
                            // TODO
                            // new ShoppingCartContentAdd(cart, _rc);
                        });
                    }));
            _actionMenu.add(
                    new ActionEntry(ICON_DOWNLOAD_ALL, "Download all as archive", "Download all as archive", () -> {
                        // TODO
                    }));
            _actionMenu.add(new ActionEntry(ICON_EXPORT_CSV, "Export CSV", "Export CSV", () -> {
                // TODO
            }));
            _actionMenu.add(new ActionEntry(ICON_EXPORT_XML, "Export XML", "Export XML", () -> {
                // TODO
            }));
            if (_resultForm.haveSelections()) {

            }
        }
    }

    protected abstract String title();

    public void show(arc.gui.window.Window owner) {
        WindowProperties wp = new WindowProperties();
        wp.setOwnerWindow(owner);
        wp.setCanBeClosed(true);
        wp.setCanBeMaximised(true);
        wp.setCanBeMoved(true);
        wp.setCanBeResized(true);
        wp.setModal(false);
        wp.setSize(0.6, 0.6);
        wp.setTitle(title());
        Window win = Window.create(wp);
        win.setContent(_tp);
        win.show();
        win.centerInPage();
    }

}
