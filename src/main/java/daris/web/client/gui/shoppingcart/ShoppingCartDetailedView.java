package daris.web.client.gui.shoppingcart;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.Button;
import arc.gui.gwt.widget.button.ButtonBar;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.TabPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.mf.client.util.Validity;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessageResponse;
import daris.web.client.gui.Resource;
import daris.web.client.gui.util.ButtonUtil;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.LoadingMessage;
import daris.web.client.model.shoppingcart.ActiveShoppingCart;
import daris.web.client.model.shoppingcart.ContentItem;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;
import daris.web.client.model.shoppingcart.ShoppingCartCollectionRef;
import daris.web.client.model.shoppingcart.ShoppingCartRef;
import daris.web.client.model.shoppingcart.messages.ShoppingCartContentClear;
import daris.web.client.model.shoppingcart.messages.ShoppingCartContentRemove;
import daris.web.client.model.shoppingcart.messages.ShoppingCartModify;
import daris.web.client.model.shoppingcart.messages.ShoppingCartOrder;
import daris.web.client.model.shoppingcart.messages.ShoppingCartOutputRetrieve;

public class ShoppingCartDetailedView extends ValidatedInterfaceComponent {

    public static final arc.gui.image.Image ICON_APPLY = new arc.gui.image.Image(
            Resource.INSTANCE.tickGreen16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_REMOVE = new arc.gui.image.Image(
            Resource.INSTANCE.remove16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_CLEAR = new arc.gui.image.Image(
            Resource.INSTANCE.clear16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_ORDER = new arc.gui.image.Image(
            Resource.INSTANCE.submit16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_DOWNLOAD = new arc.gui.image.Image(
            Resource.INSTANCE.downloadGold16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_REFRESH = new arc.gui.image.Image(
            Resource.INSTANCE.refreshGreen16().getSafeUri().asString(), 16, 16);

    private ShoppingCart _cart;

    private SimplePanel _sp;

    private VerticalPanel _vp;
    private TabPanel _tp;
    private int _settingsTabId;
    private int _contentsTabId;
    private int _statusTabId;
    private int _activeTabId;
    private ShoppingCartSettingsForm _settingsForm;
    private ShoppingCartContentsForm _contentsForm;
    private ShoppingCartStatusForm _statusForm;
    private HTML _status;

    private SimplePanel _bbSP;

    private Button _applyButton;
    private Button _removeButton;
    private Button _clearButton;
    private Button _orderButton;
    private Button _downloadButton;
    private Button _refreshButton;

    public ShoppingCartDetailedView() {

        _sp = new SimplePanel();
        _sp.fitToParent();

        _vp = new VerticalPanel();
        _vp.fitToParent();
        _sp.setContent(_vp);

        _tp = new TabPanel() {
            protected void activated(int id) {
                _activeTabId = id;
                if (_statusTabId == _activeTabId) {
                    statusTabActivated();
                } else if (_settingsTabId == _activeTabId) {
                    settingsTabActivated();
                } else if (_contentsTabId == _activeTabId) {
                    contentsTabActivated();
                }
                if (_bbSP != null) {
                    updateButtons();
                }
            }
        };
        _tp.fitToParent();
        _vp.add(_tp);

        _settingsForm = new ShoppingCartSettingsForm();
        addMustBeValid(_settingsForm);
        _settingsTabId = _tp.addTab("Settings", "Shopping cart settings", _settingsForm.gui());

        _contentsForm = new ShoppingCartContentsForm() {
            protected void itemsAdded() {
                refresh();
            }
        };
        _contentsForm.addSelectionHandler(new SelectionHandler<ContentItem>() {

            @Override
            public void selected(ContentItem o) {
                updateButtons();
            }

            @Override
            public void deselected(ContentItem o) {
                updateButtons();
            }
        });
        addMustBeValid(_contentsForm);
        _contentsTabId = _tp.addTab("Contents", "Shopping cart contents", _contentsForm.gui());

        _statusForm = new ShoppingCartStatusForm(null);
        _statusTabId = _tp.addTab("Status", "Shopping cart status", _statusForm.gui());

        _status = new HTML();
        _status.setFontSize(DefaultStyles.FONT_SIZE);
        _status.setColour(RGB.RED);
        _status.setFontWeight(FontWeight.BOLD);
        _status.setWidth100();
        _status.setHeight(22);
        _status.setTextAlign(TextAlign.CENTER);
        _status.element().getStyle().setLineHeight(22, Unit.PX);
        _status.setBorderBottom(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _vp.add(_status);

        _bbSP = new SimplePanel();
        _bbSP.setHeight(36);
        _bbSP.setWidth100();
        _vp.add(_bbSP);

        /*
         * init buttons
         */

        /*
         * apply button
         */
        _applyButton = ButtonUtil.createButton(ICON_APPLY, "Apply", "Apply changes to shopping cart settings", true);
        _applyButton.setEnabled(false);
        _settingsForm.addChangeListener(() -> {
            _applyButton.enable();
        });
        _applyButton.addClickHandler(e -> {
            _applyButton.disable();
            applySettings(null);
        });

        /*
         * removeButton
         */
        _removeButton = ButtonUtil.createButton(ICON_REMOVE, "Remove", "Remove selected objects", true);
        _removeButton.setEnabled(_contentsForm.haveSelections());
        _removeButton.addClickHandler(e -> {
            _removeButton.disable();
            if (_contentsForm.haveSelections()) {
                new ShoppingCartContentRemove(_cart, _contentsForm.selections()).send(r -> {
                    refresh();
                });
            }
        });

        /*
         * clear button
         */
        _clearButton = ButtonUtil.createButton(ICON_CLEAR, "Clear", "Empty shopping cart", true);
        _clearButton.setEnabled(_cart != null && _cart.numberOfContentItems() > 0);
        _clearButton.addClickHandler(e -> {
            _clearButton.disable();
            if (_contentsForm.haveSelections()) {
                new ShoppingCartContentClear(_cart).send(r -> {
                    refresh();
                });
            }
        });

        /*
         * order button
         */
        _orderButton = ButtonUtil.createButton(ICON_ORDER, "Order", "Submit the shopping cart for processing", true);
        _orderButton.setEnabled(_cart != null && _cart.status() == ShoppingCart.Status.EDITABLE && valid().valid());
        addChangeListener(() -> {
            boolean valid = valid().valid();
            _orderButton.setEnabled(_cart.status() == ShoppingCart.Status.EDITABLE && valid);
        });
        _orderButton.addClickHandler(e -> {
            applySettings(r -> {
                order();
            });
        });

        /*
         * download button
         */
        _downloadButton = ButtonUtil.createButton(ICON_DOWNLOAD, "Download", "Download the shopping cart content",
                true);
        _downloadButton.setEnabled(_cart != null && _cart.status() == ShoppingCart.Status.DATA_READY);
        _downloadButton.addClickHandler(e -> {
            _downloadButton.disable();
            new ShoppingCartOutputRetrieve(_cart).send(r -> {
                _downloadButton.enable();
            });
        });

        /*
         * refresh button
         */
        _refreshButton = ButtonUtil.createButton(ICON_REFRESH, "Refresh", "Reload shopping cart", true);
        _refreshButton.setEnabled(true);
        _refreshButton.addClickHandler(e -> {
            refresh();
        });
        if (_cart != null && _cart.status() == Status.EDITABLE) {
            if (_settingsForm.valid().valid()) {
                _tp.setActiveTabById(_contentsTabId);
            } else {
                _tp.setActiveTabById(_settingsTabId);
            }
        } else {
            _tp.setActiveTab(0);
        }
        updateButtons();
    }

    private void applySettings(ObjectMessageResponse<Null> rh) {
        if (_settingsForm.sink() != null) {
            _cart.importDeliveryArgsFromSink(_settingsForm.sink());
        }
        new ShoppingCartModify(_cart).send(rh);
    }

    private void order() {
        /*
         * Stop listening system events
         */
        ShoppingCartCollectionRef.ALL_CARTS.unsubscribe();
        ActiveShoppingCart.unsubscribe();
        new ShoppingCartOrder(_cart).send(r -> {
            ActiveShoppingCart.resolve(true, ac -> {
                ActiveShoppingCart.subscribe();
                ActiveShoppingCart.notifyOfActiveCartChange();

                ShoppingCartCollectionRef.ALL_CARTS.subscribe();
                ShoppingCartCollectionRef.ALL_CARTS.reset();
                ShoppingCartCollectionRef.ALL_CARTS.notifyOfCollectionChange();
            });
            ordered(_cart);
        });
    }

    protected void ordered(ShoppingCart cart) {

    }

    void refresh() {
        if (_cart != null) {
            displayCart(new ShoppingCartRef(_cart.id()));
        }
    }

    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            _status.clear();
        } else {
            _status.setHTML(v.reasonForIssue());
        }
        return v;
    }

    @Override
    public Widget gui() {
        return _sp;
    }

    public BaseWidget widget() {
        return _sp;
    }

    public void displayCart(ShoppingCart cart) {
        _cart = cart;
        _sp.setContent(_vp);
        _settingsForm.setCart(_cart);
        _contentsForm.setCart(_cart);
        _statusForm.setCart(_cart);
        if (_cart != null) {
            if (_cart.status() == Status.EDITABLE) {
                if (_settingsForm.valid().valid()) {
                    _tp.setActiveTabById(_contentsTabId);
                } else {
                    _tp.setActiveTabById(_settingsTabId);
                }
            } else {
                _tp.setActiveTabById(_statusTabId);
            }
        } else {
            _tp.setActiveTab(0);
        }
        updateButtons();
        notifyOfChangeInState();
    }

    public void displayCart(ShoppingCartRef cart) {
        showLoadingMessage();
        cart.resolve(c -> {
            displayCart(c);
        });
    }

    public void showLoadingMessage() {
        _sp.setContent(new LoadingMessage("Loading shopping cart..."));
    }

    public void clear() {
        _sp.clear();
    }

    public void activateSettingsTab() {
        _tp.setActiveTabById(_settingsTabId);
    }

    public void activateContentsTab() {
        _tp.setActiveTabById(_contentsTabId);
    }

    private void updateButtons() {

        if (_cart == null || _cart.numberOfContentItems() <= 0) {
            _removeButton.disable();
            _clearButton.disable();
            _downloadButton.disable();
        } else {
            _removeButton.setEnabled(_contentsForm.haveSelections());
            _clearButton.setEnabled(_contentsForm.totalNumberOfItems() > 0);
            _downloadButton.setEnabled(_cart.status() == Status.DATA_READY);
        }

        ButtonBar bb = new ButtonBar(ButtonBar.Position.BOTTOM, ButtonBar.Alignment.CENTER);
        bb.setHeight(32);
        bb.setWidth100();
        _bbSP.setContent(bb);

        if (_activeTabId == _settingsTabId) {
            if (_cart != null && _cart.status() == Status.EDITABLE) {
                bb.add(_applyButton);
            }
        } else if (_activeTabId == _contentsTabId) {
            bb.add(_removeButton);
            bb.add(_clearButton);
        }
        if (_cart != null && _cart.status() == Status.EDITABLE) {
            bb.add(_orderButton);
        }
        if (_cart != null && _cart.status() == Status.DATA_READY) {
            bb.add(_downloadButton);
        }
        bb.add(_refreshButton);
    }

    protected void statusTabActivated() {

    }

    protected void settingsTabActivated() {

    }

    protected void contentsTabActivated() {

    }

}
