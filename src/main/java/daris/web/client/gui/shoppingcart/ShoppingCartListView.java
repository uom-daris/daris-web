package daris.web.client.gui.shoppingcart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.InterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.button.ImageButton;
import arc.gui.gwt.widget.dialog.Dialog;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.format.WidgetFormatter;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.list.ListGridHeader;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.panel.AbsolutePanel;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.progress.ProgressBar;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.gui.gwt.widget.table.Table.Row;
import daris.web.client.gui.Resource;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.HtmlBuilder;
import daris.web.client.gui.widget.ListGridStyles;
import daris.web.client.model.shoppingcart.ActiveShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.Progress;
import daris.web.client.model.shoppingcart.ShoppingCart.ProgressListener;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;
import daris.web.client.model.shoppingcart.ShoppingCartCollectionRef;
import daris.web.client.model.shoppingcart.ShoppingCartRef;
import daris.web.client.model.shoppingcart.messages.ShoppingCartDestroy;

public class ShoppingCartListView implements InterfaceComponent {

    public static final arc.gui.image.Image ICON_DELETE = new arc.gui.image.Image(
            Resource.INSTANCE.remove16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_CLEAR = new arc.gui.image.Image(
            Resource.INSTANCE.clear16().getSafeUri().asString(), 16, 16);
    public static final arc.gui.image.Image ICON_REFRESH = new arc.gui.image.Image(
            Resource.INSTANCE.refreshBlue16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_BLACK = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartBlack16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_PURPLE = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartPurple16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_GREEN = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartGreen16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_YELLOW = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartYellow16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_RED = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartRed16().getSafeUri().asString(), 16, 16);

    public static final arc.gui.image.Image ICON_SHOPPINGCART_COLOR = new arc.gui.image.Image(
            Resource.INSTANCE.shoppingcartColor16().getSafeUri().asString(), 16, 16);

    private ShoppingCartCollectionRef _scc;
    private ShoppingCartRef _sc;

    private List<SelectionHandler<ShoppingCartRef>> _shs;

    private VerticalPanel _vp;
    private ListGrid<ShoppingCartRef> _list;
    private AbsolutePanel _buttonAP;
    private HorizontalPanel _buttonHP;
    private ImageButton _destroyButton;
    private ImageButton _clearButton;
    private ImageButton _refreshButton;

    private Map<Long, Timer> _timers;

    public ShoppingCartListView(ShoppingCartCollectionRef scc) {

        _scc = scc;

        _timers = new HashMap<Long, Timer>();

        _vp = new VerticalPanel();
        _vp.setHeight100();
        _vp.setWidth(250);

        _list = new ListGrid<ShoppingCartRef>(new DataSource<ListGridEntry<ShoppingCartRef>>() {

            @Override
            public boolean isRemote() {
                return true;
            }

            @Override
            public boolean supportCursor() {
                return false;
            }

            @Override
            public void load(Filter f, long start, long end, DataLoadHandler<ListGridEntry<ShoppingCartRef>> lh) {
                _scc.resolve(carts -> {
                    if (carts != null && !carts.isEmpty()) {
                        List<ListGridEntry<ShoppingCartRef>> entries = new ArrayList<ListGridEntry<ShoppingCartRef>>(
                                carts.size());
                        int total = carts.size();
                        for (int i = total - 1; i >= 0; i--) {
                            ShoppingCartRef cart = carts.get(i);
                            ListGridEntry<ShoppingCartRef> entry = new ListGridEntry<ShoppingCartRef>(cart);
                            entry.set("id", cart.id());
                            entry.set("status", cart.status());
                            entry.set("title", "shopping cart " + cart.id());
                            entries.add(entry);
                        }
                        lh.loaded(0, entries.size(), entries.size(), entries, DataLoadAction.REPLACE);
                    } else {
                        lh.loaded(0, 0, 0, null, null);
                    }

                });
            }
        }, ScrollPolicy.AUTO) {
            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<ShoppingCartRef>> entries) {
                if (entries != null && !entries.isEmpty()) {
                    ActiveShoppingCart.resolve(false, c -> {
                        select(c);
                    });
                }
            }
        };
        _list.fitToParent();
        _list.setMultiSelect(false);
        _list.setEmptyMessage("No shopping carts.");
        _list.setLoadingMessage("Loading shopping carts...");
        _list.setCursorSize(Integer.MAX_VALUE);
        _list.setMinRowHeight(ListGridStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _list.setClearSelectionOnRefresh(false);
        _list.setSelectionHandler(new SelectionHandler<ShoppingCartRef>() {

            @Override
            public void selected(ShoppingCartRef sc) {
                _sc = sc;
                updateImageButtons();
                notifyOfSelect(sc);
            }

            @Override
            public void deselected(ShoppingCartRef sc) {
                _sc = null;
                updateImageButtons();
                notifyOfDeselect(sc);
            }
        });

        _list.addColumnDefn("status", null, null, new WidgetFormatter<ShoppingCartRef, ShoppingCart.Status>() {

            @Override
            public BaseWidget format(ShoppingCartRef cart, Status status) {
                arc.gui.image.Image icon = iconForStatus(status);
                HTML w = ListGridStyles.formatCellHtml(
                        "<img style=\"width:16px;height:16px;vertical-align:middle;\" src=\"" + icon.path() + "\">");
                ActiveShoppingCart.resolve(false, c -> {
                    if (c.id() == cart.id()) {
                        w.setHTML("<img style=\"width:16px;height:16px;vertical-align:middle;\" src=\""
                                + ICON_SHOPPINGCART_COLOR.path() + "\">");
                    }
                });
                return w;
            }
        }).setWidth(20);
        _list.addColumnDefn("title", "Shopping Cart", "Shopping Cart", ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER)
                .setWidth(130);
        _list.addColumnDefn("status", "Status", "Status", new WidgetFormatter<ShoppingCartRef, Status>() {

            @Override
            public BaseWidget format(ShoppingCartRef cart, Status status) {

                ProgressBar pb = new ProgressBar(false, false);
                pb.setPosition(Position.ABSOLUTE);
                pb.fitToParent();
                switch (status) {
                case PROCESSING:
                    startProgressMonitor(cart);
                    break;
                case DATA_READY:
                    stopProgressMonitor(cart);
                    pb.setProgress(1.0);
                    break;
                case ABORTED:
                    stopProgressMonitor(cart);
                    pb.setProgress(0.5);
                    break;
                default:
                    stopProgressMonitor(cart);
                    pb.setProgress(0.0);
                    break;
                }
                HTML title = new HtmlBuilder().setFontFamily(DefaultStyles.FONT_FAMILY).setFontSize(11)
                        .setLineHeight(22).setHtml(status).setTextAlign(TextAlign.CENTER).build();
                title.setPosition(Position.ABSOLUTE);
                title.fitToParent();
                if (status == Status.ERROR) {
                    title.setColour(RGB.RED);
                    title.element().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
                    title.setCursor(Cursor.POINTER);
                    title.setToolTip("Double click to see error details.");
                    title.addDoubleClickHandler(e -> {
                        cart.resolve(c -> {
                            List<ShoppingCart.Log> logs = c.logs();
                            if (logs != null && !logs.isEmpty()) {
                                Dialog.warn(widget().window(), "Shopping Cart " + cart.id() + " Error",
                                        new HTML(logs.get(0).message), null);
                            }
                        });
                    });
                }
                AbsolutePanel ap = new AbsolutePanel();
                ap.setHeight(22);
                ap.setWidth(220);
                ap.add(pb);
                ap.add(title);
                return ap;
            }
        }).setWidth(250);

        _vp.add(_list);

        _buttonAP = new AbsolutePanel();
        _buttonAP.setHeight(20);
        _buttonAP.setWidth100();
        _buttonAP.setBackgroundImage(new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM,
                ListGridHeader.HEADER_COLOUR_LIGHT, ListGridHeader.HEADER_COLOUR_DARK));
        _buttonAP.setPaddingRight(2);

        _buttonHP = new HorizontalPanel();
        _buttonHP.setPosition(Position.ABSOLUTE);
        _buttonHP.setHeight(PagingControl.DEFAULT_HEIGHT);
        _buttonHP.setRight(0);

        _buttonAP.add(_buttonHP);

        _destroyButton = new ImageButton(ICON_DELETE);
        _destroyButton.setCursor(Cursor.POINTER);
        _destroyButton.setToolTip("Delete selected shopping cart");
        _destroyButton.addClickHandler(e -> {
            if (_sc != null) {
                _sc.resolve(cart -> {
                    if (cart.canDestroy()) {
                        new ShoppingCartDestroy(cart).send();
                    }
                });
            }
        });

        _clearButton = new ImageButton(ICON_CLEAR);
        _clearButton.setCursor(Cursor.POINTER);
        _clearButton.setToolTip("Clear finished shopping carts.");
        _clearButton.addClickHandler(e -> {
            new ShoppingCartDestroy(Status.ABORTED, Status.DATA_READY, Status.ERROR, Status.WITHDRAWN, Status.REJECTED)
                    .send();
        });

        _refreshButton = new ImageButton(ICON_REFRESH);
        _refreshButton.setCursor(Cursor.POINTER);
        _refreshButton.setToolTip("Refresh shopping cart list.");
        _refreshButton.addClickHandler(e -> {
            refresh();
        });

        updateImageButtons();

        _vp.add(_buttonAP);

    }

    private arc.gui.image.Image iconForStatus(ShoppingCart.Status status) {
        switch (status) {
        case EDITABLE:
            return ICON_SHOPPINGCART_BLACK;
        case DATA_READY:
            return ICON_SHOPPINGCART_GREEN;
        case PROCESSING:
        case AWAIT_PROCESSING:
        case ASSIGNED:
        case FULFILLED:
            return ICON_SHOPPINGCART_YELLOW;
        case REJECTED:
        case ERROR:
        case WITHDRAWN:
        case ABORTED:
            return ICON_SHOPPINGCART_RED;
        default:
            return ICON_SHOPPINGCART_BLACK;
        }
    }

    private void updateImageButtons() {
        _buttonHP.removeAll();
        ActiveShoppingCart.resolve(false, ac -> {
            if (_list.haveSelections() && _sc != null) {
                switch (_sc.status()) {
                case ABORTED:
                case DATA_READY:
                case ERROR:
                case REJECTED:
                case WITHDRAWN:
                    _buttonHP.add(_destroyButton);
                    break;
                case EDITABLE:
                    if (ac.id() != _sc.id()) {
                        _buttonHP.add(_destroyButton);
                    }
                    break;
                default:
                    break;
                }
            }
            if (_scc.totalNumberOfMembers() > 0) {
                _buttonHP.add(_clearButton);
            }
            _buttonHP.add(_refreshButton);
        });
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    public BaseWidget widget() {
        return _vp;
    }

    public void addSelectionHandler(SelectionHandler<ShoppingCartRef> sh) {
        if (_shs == null) {
            _shs = new ArrayList<SelectionHandler<ShoppingCartRef>>();
        }
        _shs.add(sh);
    }

    public void removeSelectionHandler(SelectionHandler<ShoppingCartRef> sh) {
        if (_shs != null) {
            _shs.remove(sh);
        }
    }

    private void notifyOfSelect(ShoppingCartRef sc) {
        if (_shs != null) {
            for (SelectionHandler<ShoppingCartRef> sh : _shs) {
                sh.selected(sc);
            }
        }
    }

    private void notifyOfDeselect(ShoppingCartRef sc) {
        if (_shs != null) {
            for (SelectionHandler<ShoppingCartRef> sh : _shs) {
                sh.deselected(sc);
            }
        }
    }

    public List<ShoppingCartRef> selections() {
        return _list.selections();
    }

    public boolean haveSelections() {
        return _list.haveSelections();
    }

    public ShoppingCartRef selected() {
        return _sc;
    }

    private void startProgressMonitor(ShoppingCartRef cart) {
        Timer timer = _timers.get(cart.id());
        if (timer == null) {
            cart.resolve(o -> {
                _timers.put(cart.id(), o.startProgressMonitor(1000, new ProgressListener() {

                    @Override
                    public void progressed(Progress progress) {
                        Row row = _list.rowFor(cart);
                        if (row == null) {
                            stopProgressMonitor(cart);
                        } else {
                            if (progress != null) {
                                AbsolutePanel ap = (AbsolutePanel) row.cell(2).widget();
                                ProgressBar pb = (ProgressBar) ap.children().get(0);
                                pb.setProgress(progress.progress());
                                HTML title = (HTML) ap.children().get(1);
                                title.setHTML("processing: " + progress.completed + "/" + progress.total);
                            }
                        }
                    }
                }));
            });
        }
    }

    private void stopProgressMonitor(ShoppingCartRef cart) {
        Timer timer = _timers.get(cart.id());
        if (timer != null) {
            timer.cancel();
            _timers.remove(cart.id());
        }
    }

    public void select(ShoppingCartRef cart) {
        _list.select(cart);
    }

    public void refresh() {
        _scc.reset();
        _list.refresh();
    }
}
