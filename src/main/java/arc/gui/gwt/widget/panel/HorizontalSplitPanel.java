package arc.gui.gwt.widget.panel;

import java.util.ArrayList;
import java.util.List;

import arc.gui.gwt.colour.Colour;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.dimension.Dimension;
import arc.gui.gwt.dimension.DimensionParser;
import arc.gui.gwt.dimension.DimensionUtil;
import arc.gui.gwt.dimension.UnspecifiedDimension;
import arc.gui.gwt.dnd.DragAndDrop;
import arc.gui.gwt.dnd.MoveDragSource;
import arc.gui.gwt.style.Style;
import arc.gui.gwt.style.StyleRegistry;
import arc.gui.gwt.widget.BaseWidget;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.image.Image;
import arc.gui.gwt.widget.image.LinearGradient;
import arc.mf.client.util.ListUtil;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel with one or more horizontal components that can be resized with a
 * separator.
 * 
 * @author jason
 *
 * Modified by Wilson Liu, to fix the issue: separator is not displayed properly when dragging/moving the container window.
 */
// @formatter:off
public class HorizontalSplitPanel extends ContainerWidget {

    public static final Image HANDLE_BACKGROUND = new LinearGradient(LinearGradient.Orientation.LEFT_TO_RIGHT,new Colour[] { RGB.GREY_AAA, RGB.GREY_EEE, RGB.GREY_AAA });
    
    // public static final Image HANDLE_GRIP = new Image("resources/images/panel/HorizontalSplitHandle.png",2,6);

    static {
        Style s = StyleRegistry.register(HorizontalSplitPanel.class);
        
        s.relate(
            StyleRegistry.register(Separator.class)
                .setBackgroundImage(HANDLE_BACKGROUND)
                .setBackgroundColour(RGB.GREY_DDD)
                .setBorderLeft(1, BorderStyle.SOLID, RGB.GREY_AAA)
                .setBorderRight(1, BorderStyle.SOLID, RGB.GREY_999)
                .setCursor(Cursor.COL_RESIZE)
            );
    }
    
    
    private class Content extends SimplePanel {
        private Widget _w;
        
        public Content(Widget w) {
            _w = w;
            super.setPosition(Position.ABSOLUTE);

            add(w,false);
            
            super.setHeight100();
            super.setOverflow(Overflow.HIDDEN);
        }
        
        public int width() {
            if ( widthHasBeenSet() ) {
                return super.width();
            }
            
            if ( _w instanceof BaseWidget ) {
                return ((BaseWidget)_w).width();
            }
            
            return (int)DimensionParser.parse(_w.getElement().getStyle().getWidth()).transform(1.0);
        }
        
        public Dimension preferredWidth() {
            if ( super.preferredWidthSet() ) {
                return super.preferredWidth();
            }
            
            if ( _w instanceof BaseWidget ) {
                return ((BaseWidget)_w).preferredWidth();
            }
            
            return DimensionParser.parse(_w.getElement().getStyle().getWidth());
        }
        
        public ContainerWidget parent() {
            return HorizontalSplitPanel.this;
        }
        
        protected void doLayoutChildren() {
            Widget widget = widget();
            if ( widget instanceof BaseWidget ) {
                BaseWidget bw = (BaseWidget)widget;
                
                int w = BaseWidget.outerWidth(bw,width());
                int h = BaseWidget.outerHeight(bw,height());
                
                //setPreferredWidth(w);
                //setPreferredHeight(h);
                
                if ( !bw.resizeTo(w,h) ) {
                    BaseWidget.doLayout(bw);
                }
            }

        }
        
        
        protected boolean setWidth(int w,boolean doLayout) {
            return super.setWidth(w, doLayout);
        }
        
        /*
        protected void resized() {
            if ( !readyToResize() ) {
                return;
            }
            
            Widget widget = widget();
            if ( widget instanceof BaseWidget ) {
                (BaseWidget)bw.
            }
            if ( widget instanceof CanBeResized ) {
                CanBeResized cbr = (CanBeResized)widget;
                cbr.resizeTo(width(), height());
            }
        }
        */
    }
    
    protected static class Separator extends SimplePanel {
        private Content _a;
        private Content _b;

        private int _x;
        private int _aw;
        private int _bw;
        private int _tw;
        private int _separatorWidth;
        
        public Separator(final HorizontalSplitPanel sp,final Content a,final Content b) {
            _a = a;
            _b = b;
            _separatorWidth = sp.separatorWidth();
            
            super.setPosition(Position.ABSOLUTE);
//            super.setZIndex(10000); // TODO: commmented out by Wilson Liu

            int sw = sp.separatorWidth();
            if ( sw <= 0 ) {
                sw = 1;
            }
            
            setWidth(sw);
            setHeight100();
            
            DragAndDrop.makeDraggable(new MoveDragSource() {
                public Cursor beginDrag(int x,int y) {
                    _aw = a.width();
                    _bw = b.width();
                    _tw = _aw + _bw;
                    
                    Separator.this.setBackgroundColour(RGB.GREY_AAA);
                    Separator.this.element().getStyle().setZIndex(Integer.MAX_VALUE); // TODO Added by Wilson Liu
                    return Cursor.COL_RESIZE;
                }

                public void moveTo(int x,int y) {
                    int sal = Separator.this.parent().absoluteLeft();
                    int hsw = sp.separatorWidth()/2;
                    
                    int sx = x;
                    if ( sx < sal + hsw ) {
                        sx = sal + hsw;
                    }

                    int br = b.isVisible() ? b.absoluteRight() : a.absoluteRight();
                    if ( sx + hsw > br ) {
                        sx = br - hsw;
                    }

                    int bl = a.absoluteLeft();
                    if ( sx < bl + hsw ) {
                        sx = bl + hsw;
                    }
                    
                    int sl = sx-sal-hsw;
                    
                    Separator.this.setLeft(sl);
                }

                public void endDrag() {
                    int sal = Separator.this.absoluteLeft();
                    int al = a.absoluteLeft();
                    int aw = sal - al;
                    
                    a.setWidth(aw);
                    
                    b.setLeft(Separator.this.right());

                    int bw = _tw - aw;
                    if ( bw < 0 ) {
                        bw = 0;
                    }

                    b.setWidth(bw);

                    Separator.this.setBackgroundColour(RGB.GREY_DDD);
                    Separator.this.element().getStyle().clearZIndex(); // TODO Added by Wilson Liu
                }

                public Widget widget() {
                    return Separator.this;
                }

                public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
                    return Separator.this.addDomHandler(handler, MouseDownEvent.getType());
                }

                public void fireEvent(GwtEvent<?> event) {
                    Separator.this.fireEvent(event);
                }

                public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
                    return Separator.this.addDomHandler(handler, MouseMoveEvent.getType());
                }

                public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
                    return Separator.this.addDomHandler(handler,MouseUpEvent.getType());
                }

                public boolean isDragDown(MouseDownEvent event) {
                    return true;
                }

                @Override
                public boolean willBeDropped() {
                    return false;
                }

            });
            
        }
        
        /*
        public int absoluteLeft() {
            return _x;
        }
        */
        
        public void setLeft(int x) {
            super.setLeft(x);
            _x = x;
        }
        
        
        public void setLeftWidgetVisible(boolean visible) {
            if ( _a.visible() == visible ) {
                return;
            }
            
            if ( _a.visible() ) {
                _aw = _a.width();
            }
            
            if ( _b.visible() ) {
                _bw = _b.width();
            }
            
            if ( visible ) {
                _tw = _bw - _separatorWidth;
            } else {
                _tw = _aw + _bw + _separatorWidth;
            }
            
            _a.setVisible(visible);
            super.setVisible(visible);
            
            if ( visible ) {
                _a.reconfigureLayout();
            }
            
            changeInWidgetLayout();
        }
        
        public void setRightWidgetVisible(boolean visible) {
            if ( _b.visible() == visible ) {
                return;
            }

            if ( _a.visible() ) {
                _aw = _a.width();
            }
            
            if ( _b.visible() ) {
                _bw = _b.width();
            }
            
            if ( visible ) {
                _tw = _aw - _separatorWidth;
                
                // Remove the width of the right panel from the left if making the right visible.
                _aw -= _bw;
            } else {
                _tw = _aw + _bw + _separatorWidth;
            }
            
            _b.setVisible(visible);
            super.setVisible(visible);
            
            if ( visible ) {
                _b.reconfigureLayout();
            }
            
            changeInWidgetLayout();
        }
        
        private void changeInWidgetLayout() {
            int aw;
            int bw;
            
            if ( _a.visible() ) {
                aw = _b.visible() ? _aw : _tw;
            } else {
                aw = 0;
            }
            
            if ( _b.visible() ) {
                bw = _a.visible()? _bw : _tw;
            } else {
                bw = 0;
            }
            
            if ( _a.visible() ) {
                _a.setWidth(aw);
            }

            if ( Separator.this.isVisible() ) {
                Separator.this.setLeft(aw);
            }
            
            if ( _b.visible() ) {
                if ( aw + bw != _tw ) {
                    bw = _tw - aw;
                }
                
                _b.setWidth(bw);
                
                if ( _a.visible() ) {
                    _b.setLeft(Separator.this.right());
                } else {
                    _b.setLeft(0);
                }
            }
        }



    }
    
    private AbsolutePanel _ap;
    private List<Content> _cws;
    private List<Separator> _ss;
    private List<Integer> _nonVisible;
    
    private int _separatorWidth;
    private boolean _initial;
    
    public HorizontalSplitPanel() {
        this(2);
    }
    
    public HorizontalSplitPanel(int separatorWidth) {
        _ap = new AbsolutePanel() {
            public void doLayoutChildren() {
                // Do nothing .. must be done by split panel.
            }
        };
        
        _cws = new ArrayList<Content>();
        _ss = new ArrayList<Separator>();
        
        _separatorWidth = separatorWidth;
        _initial = true;
        
        initWidget(_ap);
    }
    
    public int separatorWidth() {
        return _separatorWidth;
    }
    
    /*
    public boolean resizeNeighbour() {
        return _resizeNeighbour;
    }
    */
    
    /**
     * Controls whether resizing one panel causes the next panel to resize.
     * By default resizing one panel, will change the size of the neighbour.
     * 
     * @param cascade
     */
    /*
    public void setResizeNeighbour(boolean resize) {
        _resizeNeighbour = resize;
    }
    */
    
    @Override
    public void doAdd(Widget w,boolean layout) {
        Content c = new Content(w);

        if ( _cws.size() > 0 ) {
            addSeparatorFor((Content)_cws.get(_cws.size()-1),c);
        }

        _ap.add(c,layout);
        _cws.add(c);
        
        _initial = true;
    }
    

    @Override
    public boolean doRemove(Widget w,boolean layout) {
        // TODO!!
        return false;
    }
    
    private void addSeparatorFor(Content a,Content b) {
        Separator ss = new Separator(this,a,b);
        
        Style s = separatorStyle();
        if ( s != null ) {
            s.applyTo(ss);
        }
        
        _ap.add(ss);
        _ss.add(ss);
    }

    protected Style separatorStyle() {
        return null;
    }
    
    public void setPanelVisible(int i, boolean visible) {
        
        // If already layed out then we an perform immediately, otherwise we need to defer until after the 
        // widths have been distributed / computed.
        if ( _initial ) {
            if ( visible ) {
                ListUtil.removeFrom(_nonVisible, i);
            } else {
                _nonVisible = ListUtil.addTo(_nonVisible, i);
            }
        } else {

            if ( i == _ss.size() ) {
                // Find the i'th separator, and tell it to set the top level widget height to that.
                Separator ss = _ss.get(i-1);

                ss.setRightWidgetVisible(visible);
            } else {
                // Find the i'th separator, and tell it to set the top level widget height to that.
                Separator ss = _ss.get(i);

                ss.setLeftWidgetVisible(visible);
            }

            //  
            //
            //      _cws.get(i).setVisible(visible);
            //      _ss.get(i-1).setVisible(visible);
            doLayoutChildren();
        }

    }
    
    protected void doLayoutChildren() {
        if ( _cws.isEmpty() ) {
            return;
        }
        
        if ( !_initial && !hasProportionalWidth() ) {
            return;
        }
        
        DimensionUtil.Value[] widths = computeContentWidths(_initial);
        int wi = 0;
        int off = 0;
        int si = 0;
        
        int height = height();
        int pw = 0;
        
        for ( Content c : _cws ) {
            int width = widths[wi].value;
            
            if ( width == -1 ) {
                throw new AssertionError("Horizontal split panel cannot have members with unspecified widths");
            } else {
                boolean visible = true;
                
                if ( widths[wi].widget instanceof BaseWidget ) {
                    BaseWidget bw = (BaseWidget)widths[wi].widget;
                    visible = bw.visible();
                }
                
                if ( visible ) {
                    
                    width += pw;
                    pw = 0;
                    
                    c.setLeft(off);
                    off += width;
                    
                    // c.setPreferredWidth(width);
                    // c.setPreferredHeight(height());
                    
                    resizeTo(c,width, outerHeight(c,height),false);
                    postLayoutAndLayoutChildren(c);
                    
                    if ( si < _ss.size() ) {
                        Separator s = (Separator)_ss.get(si);
                        
                        s.setLeft(off); 
                        off += _separatorWidth;
                        
                        s.setHeight(height());
                    }
                    
                } else {
                    pw += width;
                }
                
                si++;

            }

            wi++;
        }
        
        // We must set the width to allow scrolling adjustments. We know the width now
        // based on the next offset.
        setWidth(off,false);
        
        _initial = false;
        
        if ( _nonVisible != null ) {
            List<Integer> nvs = new ArrayList<Integer>(_nonVisible);
            
            // Set to null to avoid a stack overflow, because changing the panel visibility will cause a 
            // recursive call to doLayoutChildren (this method):
            _nonVisible = null;
            
            for ( Integer nv : nvs ) {
                setPanelVisible(nv, false);
            }
            
            _nonVisible = null;
        }
    }

    private DimensionUtil.Value[] computeContentWidths(boolean initial) {
        ContainerWidget pp = parent();
        
        boolean pis = parentIsSelf();
        
        int width;
        if ( isWidth100() ) {
            width = pis? innerWidth(pp) : innerWidth();
        } else {
            width = Integer.MAX_VALUE;
        }
        
        int totalSeparatorWidth = 0;

        for ( Separator s : _ss ) {
            if ( s.visible() ) {
                totalSeparatorWidth += _separatorWidth;
            }
        }
        
        int availableWidth = width - totalSeparatorWidth;
        if ( availableWidth < 0 ) {
            availableWidth = 0;
        }
        
        if ( initial ) {
            distributeUnspecifiedWidths();
            return DimensionUtil.computePreferredWidths(_cws.iterator(), availableWidth);
        }
        
        return DimensionUtil.computeWidths(_cws.iterator(), availableWidth);
    }
    
    
    private void distributeUnspecifiedWidths() {
        double total = 0;
        
        // Find the current proportional widths.
        int nbUnspecified = 0;
        for ( Content c : _cws ) {
            Dimension pw = c.preferredWidth();
            if ( pw instanceof UnspecifiedDimension ) {
                nbUnspecified++;
                total += 1.0;
            }
        }
        
        if ( nbUnspecified > 0 ) {
            double value = total / nbUnspecified;

            for ( Content c : _cws ) {
                Dimension pw = c.preferredWidth();
                if ( pw instanceof UnspecifiedDimension ) {
                    c.setPreferredWidth(value);
                }
            }
        }
    }
    
    
    protected void onDetach() {
        super.onDetach();
        
        // _initial = true;
    }


}
