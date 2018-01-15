package daris.web.client.gui.shoppingcart;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;

import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.HorizontalSplitPanel;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.window.Window;
import arc.gui.gwt.widget.window.WindowCloseListener;
import arc.gui.window.WindowProperties;
import daris.web.client.gui.Resource;
import daris.web.client.model.shoppingcart.ActiveShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCartCollectionRef;
import daris.web.client.model.shoppingcart.ShoppingCartRef;

public class ShoppingCartManagerDialog implements ActiveShoppingCart.Listener, ShoppingCartCollectionRef.Listener {

    public static final arc.gui.image.Image ICON_RIGHT = new arc.gui.image.Image(
            Resource.INSTANCE.doubleRightChevron16().getSafeUri().asString(), 16, 16);;
    public static final arc.gui.image.Image ICON_LEFT = new arc.gui.image.Image(
            Resource.INSTANCE.doubleLeftChevron16().getSafeUri().asString(), 16, 16);

    private boolean _showList;

    private ShoppingCartCollectionRef _scc;

    private SimplePanel _sp;
    private HorizontalSplitPanel _hsp;

    private ShoppingCartListView _list;
    private HorizontalPanel _detailHP;
    private AbsolutePanel _switchAP;
    private SwitchButton _switchButton;
    private ShoppingCartDetailedView _detail;

    private arc.gui.gwt.widget.window.Window _win;

    private ShoppingCartManagerDialog(boolean showList) {

        _showList = showList;

        _sp = new SimplePanel();
        _sp.fitToParent();

        _hsp = new HorizontalSplitPanel(5);
        _hsp.fitToParent();
        _sp.setContent(_hsp);

        _scc = ShoppingCartCollectionRef.ALL_CARTS;
        _list = new ShoppingCartListView(_scc);
        _list.widget().setPreferredWidth(0.4);
        _scc.addListener(this);

        _detailHP = new HorizontalPanel();
        _detailHP.fitToParent();

        _switchAP = new AbsolutePanel() {
            protected void doLayoutChildren() {
                super.doLayoutChildren();
                _switchButton.setTop((_switchAP.height() - _switchButton.height()) / 2);
            }
        };
        _switchAP.setWidth(SwitchButton.WIDTH);
        _switchAP.setHeight100();
        _switchAP.setBackgroundColour(new RGB(0xc7, 0xc7, 0xc7));
        _switchAP.setBorderRight(1, BorderStyle.SOLID, RGB.GREY_BBB);
        _detailHP.add(_switchAP);

        _switchButton = new SwitchButton(_showList) {
            protected void stateUpdated(boolean showList) {
                _showList = showList;
                showHideList();
                if (!_showList) {
                    _list.refresh();
                }
            }
        };
        _switchButton.setPosition(Position.ABSOLUTE);
        _switchButton.setLeft(0);

        _switchAP.add(_switchButton);

        _detail = new ShoppingCartDetailedView();
        _detailHP.add(_detail.widget());

        _list.addSelectionHandler(new SelectionHandler<ShoppingCartRef>() {

            @Override
            public void selected(ShoppingCartRef o) {
                if (o != null) {
                    _detail.displayCart(o);
                }
                if (_win != null) {
                    ActiveShoppingCart.resolve(false, ac -> {
                        if (ac.id() == o.id()) {
                            _win.setTitle("Active shopping cart " + (o == null ? "" : o.id()));
                        } else {
                            _win.setTitle("Shopping cart " + (o == null ? "" : o.id()));
                        }
                    });

                }
            }

            @Override
            public void deselected(ShoppingCartRef o) {
                _detail.clear();
                if (_win != null) {
                    _win.setTitle("Shopping cart ");
                }
            }
        });

        _hsp.add(_list.widget());
        _hsp.add(_detailHP);

        showHideList();

    }

    private void showHideList() {
        _hsp.setPanelVisible(0, _showList);
    }

    public void refresh() {
        _list.refresh();
    }

    public void show(arc.gui.window.Window owner, boolean showActiveCart) {
        if (_win == null) {
            WindowProperties wp = new WindowProperties();
            wp.setOwnerWindow(owner);
            wp.setCanBeClosed(true);
            wp.setCanBeMaximised(true);
            wp.setCanBeMoved(true);
            wp.setCanBeResized(true);
            wp.setModal(false);
            wp.setSize(0.7, 0.7);
            wp.setTitle("Shopping cart");
            _win = Window.create(wp);
            _win.addCloseListener(new WindowCloseListener() {

                @Override
                public void closed(Window w) {
                    _win = null;
                }
            });
        }
        _win.setContent(_sp);
        if (!_win.isShowing()) {
            _win.show();
        }
        _win.centerInPage();
    }

    public void discard() {
        ActiveShoppingCart.removeListener(this);
        _scc.unsubscribe();
        _scc.removeListener(this);
    }

    @Override
    public void activateShoppingCartUpdated() {
        _detail.showLoadingMessage();
        ActiveShoppingCart.resolve(false, cart -> {
            _detail.displayCart(cart);
        });
    }

    @Override
    public void shoppingCartCollectionUpdated() {
        _list.refresh();
    }

    private static class SwitchButton extends SimplePanel {

        public static final int WIDTH = 12;

        public static final arc.gui.image.Image ICON_OFF = new arc.gui.image.Image(
                Resource.INSTANCE.doubleRightChevron16().getSafeUri().asString(), 10, 10);;
        public static final arc.gui.image.Image ICON_ON = new arc.gui.image.Image(
                Resource.INSTANCE.doubleLeftChevron16().getSafeUri().asString(), 10, 10);
        private boolean _on;

        SwitchButton(boolean on) {
            _on = on;
            setWidth(WIDTH);
            setHeight(60);
            setBackgroundColour(new RGB(0xf0, 0xf0, 0xf0));
            setBorder(1, BorderStyle.SOLID, RGB.GREY_BBB);
            setBorderRadius(3);
            setCursor(Cursor.POINTER);
            addMouseOverHandler(e -> {
                SwitchButton.this.setBackgroundColour(RGB.GREY_BBB);
                SwitchButton.this.setBorder(1, BorderStyle.SOLID, RGB.GREY_EEE);
            });
            addMouseOutHandler(e -> {
                setBackgroundColour(new RGB(0xf0, 0xf0, 0xf0));
                setBorder(1, BorderStyle.SOLID, RGB.GREY_BBB);
            });
            updateToolTip();
            addClickHandler(e -> {
                _on = !_on;
                updateIcon();
                setBackgroundColour(new RGB(0xf0, 0xf0, 0xf0));
                setBorder(1, BorderStyle.SOLID, RGB.GREY_BBB);
                updateToolTip();
                stateUpdated(_on);
            });
            updateIcon();
        }

        private void updateToolTip() {
            if (_on) {
                setToolTip("Show active Shopping cart.");
            } else {
                setToolTip("Show all shopping carts.");
            }
        }

        protected void stateUpdated(boolean on) {

        }

        private void updateIcon() {
            arc.gui.image.Image icon = _on ? ICON_ON : ICON_OFF;
            element().setInnerHTML(
                    "<img style=\"position:absolute;margin:auto;top:0;bottom:0;left:0;right:0;width:8px;height:8px;\" src=\""
                            + icon.path() + "\">");
        }

    }

    private static ShoppingCartManagerDialog _instance;

    public static ShoppingCartManagerDialog get() {
        if (_instance == null) {
            _instance = new ShoppingCartManagerDialog(true);
        }
        return _instance;
    }

    public static void reset() {
        if (_instance != null) {
            _instance.discard();
            _instance = null;
        }
    }

}
