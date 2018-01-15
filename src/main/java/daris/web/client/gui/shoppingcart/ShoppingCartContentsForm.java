package daris.web.client.gui.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.dnd.DropCheck;
import arc.gui.gwt.dnd.DropHandler;
import arc.gui.gwt.dnd.DropListener;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.event.SelectionHandler;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.gui.gwt.widget.list.ListGrid;
import arc.gui.gwt.widget.list.ListGridEntry;
import arc.gui.gwt.widget.paging.PagingControl;
import arc.gui.gwt.widget.paging.PagingListener;
import arc.gui.gwt.widget.panel.HorizontalPanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.IsNotValid;
import arc.mf.client.util.IsValid;
import arc.mf.client.util.Validity;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.ListGridStyles;
import daris.web.client.model.object.HasAssetId;
import daris.web.client.model.object.HasCiteableId;
import daris.web.client.model.shoppingcart.ContentItem;
import daris.web.client.model.shoppingcart.ContentItemCollectionRef;
import daris.web.client.model.shoppingcart.ShoppingCart;
import daris.web.client.model.shoppingcart.ShoppingCart.Status;
import daris.web.client.model.shoppingcart.messages.ShoppingCartContentAdd;
import daris.web.client.util.SizeUtil;

public class ShoppingCartContentsForm extends ValidatedInterfaceComponent implements PagingListener {

    public static final int PAGE_SIZE = 1000;

    private ShoppingCart _cart;
    private ContentItemCollectionRef _ic;

    private VerticalPanel _vp;
    private ListGrid<ContentItem> _list;
    private HTML _summary;
    private PagingControl _pc;

    private List<SelectionHandler<ContentItem>> _shs;

    public ShoppingCartContentsForm(ShoppingCart cart) {
        _cart = cart;

        _shs = new ArrayList<SelectionHandler<ContentItem>>();

        if (_cart != null) {
            _ic = new ContentItemCollectionRef(_cart);
            _ic.setCountMembers(true);
            _ic.setPagingSize(PAGE_SIZE);
        }

        _vp = new VerticalPanel();
        _vp.fitToParent();

        _list = new ListGrid<ContentItem>(ScrollPolicy.AUTO) {
            @Override
            protected void postLoad(long start, long end, long total, List<ListGridEntry<ContentItem>> entries) {
                if (entries != null && !entries.isEmpty()) {
                    this.select(0);
                }
            }
        };
        _list.fitToParent();
        _list.addColumnDefn("cid", "ID", "Citeable ID", ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(100);
        _list.addColumnDefn("name", "Name", null, ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER).setWidth(500);
        _list.addColumnDefn("size", "Size", "Content size", ListGridStyles.getHtmlFormatter(TextAlign.RIGHT))
                .setWidth(90);
        _list.addColumnDefn("mimeType", "MIME Type", "Asset MIME type", ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER)
                .setWidth(160);
        _list.addColumnDefn("assetId", "Asset ID", "Asset ID", ListGridStyles.LIST_GRID_CELL_TEXT_FORMATTER)
                .setWidth(100);

        _list.setEmptyMessage("");
        _list.setLoadingMessage("loading...");
        _list.setMultiSelect(true);
        _list.setMinRowHeight(ListGridStyles.LIST_GRID_MIN_ROW_HEIGHT);
        _list.setClearSelectionOnRefresh(false);
        _list.setCursorSize(PAGE_SIZE);
        _list.enableDropTarget(false);
        _list.setSelectionHandler(new SelectionHandler<ContentItem>() {

            @Override
            public void selected(ContentItem o) {
                for (SelectionHandler<ContentItem> sh : _shs) {
                    sh.selected(o);
                }
            }

            @Override
            public void deselected(ContentItem o) {
                for (SelectionHandler<ContentItem> sh : _shs) {
                    sh.deselected(o);
                }
            }
        });
        _list.setDropHandler(new DropHandler() {

            @Override
            public DropCheck checkCanDrop(Object data) {
                if (_cart != null && _cart.status() == Status.EDITABLE && data != null
                        && (data instanceof HasCiteableId || data instanceof HasAssetId)) {
                    return DropCheck.CAN;
                }
                return DropCheck.CANNOT;
            }

            @Override
            public void drop(BaseWidget target, List<Object> data, DropListener dl) {
                if (_cart != null && _cart.status() == Status.EDITABLE && data != null) {
                    List<HasCiteableId> objects = new ArrayList<HasCiteableId>();
                    for (Object o : data) {
                        if (o instanceof HasCiteableId) {
                            objects.add((HasCiteableId) o);
                        }
                    }
                    if (!objects.isEmpty()) {
                        new ShoppingCartContentAdd(_cart.id(), objects).send(r -> {
                            dl.dropped(DropCheck.CAN);
                            itemsAdded();
                        });
                        return;
                    }
                }
                dl.dropped(DropCheck.CANNOT);
            }
        });

        _vp.add(_list);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setHeight(PagingControl.DEFAULT_HEIGHT);
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        hp.setBackgroundImage(new LinearGradient(LinearGradient.Orientation.TOP_TO_BOTTOM, RGB.GREY_AAA, RGB.GREY_777));
        hp.setWidth100();
        hp.setPaddingRight(2);

        _pc = new PagingControl(PAGE_SIZE);
        _pc.addPagingListener(this);
        hp.add(_pc);

        _summary = new HTML();
        _summary.setFontFamily(DefaultStyles.FONT_FAMILY);
        _summary.setFontSize(DefaultStyles.FONT_SIZE);
        _summary.setFontWeight(FontWeight.BOLD);
        _summary.setHeight(PagingControl.DEFAULT_HEIGHT);
        _summary.element().getStyle().setLineHeight(PagingControl.DEFAULT_HEIGHT, Unit.PX);
        _summary.setTextAlign(TextAlign.CENTER);
        _summary.setBorderLeft(1, BorderStyle.SOLID, RGB.GREY_CCC);
        _summary.setWidth(150);

        updateSummary();
        hp.add(_summary);

        _vp.add(hp);

        if (_ic != null) {
            gotoOffset(0);
        }
    }

    public ShoppingCartContentsForm() {
        this(null);
    }

    @Override
    public Widget gui() {
        return _vp;
    }

    private void updateSummary() {
        if (_cart == null) {
            _summary.setHTML("[ 0 items, 0 bytes ]");
        } else {
            _summary.setHTML("[ " + _cart.numberOfContentItems() + " items, "
                    + SizeUtil.getHumanReadableSize(_cart.sizeOfContentItems()) + " ]");
        }
    }

    @Override
    public Validity valid() {
        Validity v = super.valid();
        if (v.valid()) {
            if (_cart != null && _cart.numberOfContentItems() <= 0) {
                return new IsNotValid("No objects have been added to shopping cart " + _cart.id());
            }
        } else {
            if (_cart != null && _cart.status() != Status.EDITABLE) {
                return IsValid.INSTANCE;
            }
        }
        return v;
    }

    void setCart(ShoppingCart cart) {
        _cart = cart;
        if (_cart == null) {
            _ic = null;
            _list.setData(null);
            _pc.setOffset(0, 0, true);
        } else {
            _ic = new ContentItemCollectionRef(_cart);
            _ic.setCountMembers(true);
            _ic.setPagingSize(PAGE_SIZE);
            gotoOffset(0);
        }
        updateSummary();
    }

    @Override
    public void gotoOffset(long offset) {

        _list.setBusyLoading();
        _ic.resolve(offset, offset + _ic.pagingSize(), items -> {
            long total = _ic.totalNumberOfMembers();
            _pc.setOffset(offset, total, true);
            List<ListGridEntry<ContentItem>> entries = null;
            if (items != null && !items.isEmpty()) {
                entries = new ArrayList<ListGridEntry<ContentItem>>();
                for (ContentItem item : items) {
                    ListGridEntry<ContentItem> entry = new ListGridEntry<ContentItem>(item);
                    entry.set("cid", item.citeableId());
                    entry.set("type", item.objectType());
                    entry.set("name", item.objectName());
                    entry.set("size", SizeUtil.getHumanReadableSize(item.size()));
                    entry.set("version", item.version());
                    entry.set("assetId", item.assetId());
                    entry.set("mimeType", item.mimeType());
                    entries.add(entry);
                }
            }
            _list.setData(entries);
        });
    }

    public void addSelectionHandler(SelectionHandler<ContentItem> sh) {
        _shs.add(sh);
    }

    public void removeSelectionHandler(SelectionHandler<ContentItem> sh) {
        _shs.remove(sh);
    }

    public List<ContentItem> selections() {
        return _list.selections();
    }

    public boolean haveSelections() {
        return _list.haveSelections();
    }

    public long totalNumberOfItems() {
        if (_ic == null) {
            return -1;
        }
        return _ic.totalNumberOfMembers();
    }

    protected void itemsAdded() {

    }

}
