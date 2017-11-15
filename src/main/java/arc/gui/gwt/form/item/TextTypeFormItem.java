package arc.gui.gwt.form.item;

import arc.gui.form.Field;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItemListener;
import arc.gui.form.TextFieldRenderOptions;
import arc.gui.gwt.dimension.Dimension;
import arc.gui.gwt.dimension.NormalizedDimension;
import arc.gui.gwt.form.FormItemFactory;
import arc.gui.gwt.widget.ResizeListener;
import arc.gui.gwt.widget.filter.KeyPressFilter;
import arc.gui.gwt.widget.input.AutoResizeTextArea;
import arc.gui.gwt.widget.input.TextArea;
import arc.gui.gwt.widget.input.TextWidget;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.util.ThrowableUtil;
import arc.mf.dtype.TextType;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;


public class TextTypeFormItem implements FormItem {
    
    public static enum Presentation {
        SCROLLING,
        AUTO_RESIZE;
    }
    
    private static Presentation _defaultPresentation = Presentation.SCROLLING;
    
    public static Presentation defaultPresentation() {
        return _defaultPresentation;
    }
    
    public static void setDefaultPresentation(Presentation p) {
        _defaultPresentation = p;
    }
    
    public Widget create(Form form,final Field f,FormItemFocusListener fl,FormSubmitOnEnter fse) {
        final TextType type = (TextType)f.definition().type();
        
        return createWidgetFor(form,f,fl,type.minLength(),type.maxLength(),60);
    }
    
    private static Boolean useAutoResizeTextWidget( final Field f ) {
        FieldRenderOptions ro = f.renderOptions();
        if ( ro != null ) {
            try {
                if ( ro.hasValue(TextFieldRenderOptions.AUTO_RESIZE) ) {
                    return ro.booleanValue( TextFieldRenderOptions.AUTO_RESIZE, false);
                }
                
            } catch ( Throwable t ) {
                ThrowableUtil.rethrowAsUnchecked(t);
                return false;
            }
        }
        
        switch ( _defaultPresentation ) {
            case AUTO_RESIZE:
                return true;
            default:
                return false;
        }
    }
    
    protected static Widget createWidgetFor(final Form form,final Field f,final FormItemFocusListener fl,final int minLength,final int maxLength,int height) {
        if ( useAutoResizeTextWidget( f) ) {
            return createAutoResizeWidgetFor(form, f, fl, minLength, maxLength, height);
        }
        
        return createDefaultWidgetFor(form, f, fl, minLength, maxLength, height);
    }
    
    private static Widget createDefaultWidgetFor(final Form form,final Field f,final FormItemFocusListener fl,final int minLength,final int maxLength,int height) {
        final TextArea text = new TextArea() {
            public void onAttach() {
                super.onAttach();

                if ( f.focus() ) {
                    super.setFocus(true);
                }
            }
            
        };
        
        FieldRenderOptions ro = f.renderOptions();
        if ( ro == null || ro.width() == null ) {
            if ( form.isWidth100() ) {
                text.setPreferredWidth(NormalizedDimension.ONE);
            } else {
                text.setWidth(200);
                text.setPreferredWidth(200);
            }
        } else {
            Dimension roWidth = ro.width();
            if (roWidth instanceof NormalizedDimension) {
                text.setPreferredWidth(roWidth);
            } else {
                text.setWidth((int)roWidth.transform(1));
                text.setPreferredWidth((int)roWidth.transform(1));
            }
        }
        
        if ( ro == null || ro.height() == null ) {
            text.setHeight(height);
        } else {
            Dimension roHeight = ro.height();
            if ( roHeight instanceof NormalizedDimension ) {
                text.setPreferredHeight(ro.height());   
            } else {
                text.setHeight((int)roHeight.transform(1));
                text.setPreferredHeight((int)roHeight.transform(1));
            }
        }
        
        performCommonInitialization(text, form, f, fl, minLength, maxLength, height);

        return text;

    }

    private static Widget createAutoResizeWidgetFor(final Form form,final Field f,final FormItemFocusListener fl,final int minLength,final int maxLength,int height) {
        final AutoResizeTextArea text = new AutoResizeTextArea() {
            public void onAttach() {
                super.onAttach();

                if ( f.focus() ) {
                    super.setFocus(true);
                }
            }
            
        };
        
        FieldRenderOptions ro = f.renderOptions();
        if ( ro == null || ro.width() == null ) {
            if ( form.isWidth100() ) {
                text.setPreferredWidth(NormalizedDimension.ONE);
            } else {
                text.setMinWidth(200);
            }
        } else {
            text.setPreferredWidth(ro.width());
        }
        
        if ( ro == null || ro.height() == null ) {
            text.setMinAutoHeight(height);
        } else {
            text.setPreferredHeight(ro.height());
        }
        
        performCommonInitialization(text, form, f, fl, minLength, maxLength, height);
        
        return text;

    }
    
    private static void performCommonInitialization( 
            final TextWidget<com.google.gwt.user.client.ui.TextArea> text,
            final Form form,final Field f,final FormItemFocusListener fl,final int minLength,final int maxLength,int height ) {
        
        FormItemStyle.applyReadWriteTo(text);
        
        if ( fl != null ) {
            text.addFocusHandler(new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    fl.focusOn(f);
                }
            });
            
            text.addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    fl.focusOff(f);
                }
            });
        }
        

        text.setEnabled(f.enabled());
        text.setVisible(f.visible());

        if ( f.value() != null ) {
            text.setValue(f.value().toString());
        }
        
        if ( maxLength < Integer.MAX_VALUE ) {
            text.addKeyPressFilter(new KeyPressFilter() {
                public boolean allow(String value, char c) {
                    if ( value == null ) {
                        return true;
                    }
                    
                    if ( value.length() >= maxLength ) {
                        return false;
                    }
                    
                    return true;
                }
            });
        }

        text.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                String v = event.getValue();
                
                if ( v == null || v.equals("") ) {
                    f.setValue(null);
                } else {
                    f.setValue(v);
                    if ( v.length() < minLength ) {
                        f.markInvalid("The minimum length must be " + minLength + " character(s). The value is " + v.length() + " character(s).");
                    }
                }
            }
        });
        
        text.addResizeListener(new ResizeListener() {
            @Override
            public Widget widget() {
                return text;
            }

            @Override
            public void resized(long w, long h) {
                form.notifyOfFieldResize();
            }
            
        });
        
        text.enableValueMonitoring();
        
        form.addRenderListener(f,new FormItemListener() {
            public void itemPropertyChanged(arc.gui.form.FormItem f,arc.gui.form.FormItem.Property property) {
                
                switch ( property ) {
                case VISIBILITY:
                    text.setVisible(f.visible());
                    break;
                    
                case ENABLED:
                    text.setEnabled(f.enabled());
                    FormItemFactory.updateStyle(text, f);
                    break;
                    
                case FOCUS:
                    text.setFocus(f.focus());
                    break;
                    
                case SELECT:
                    text.selectAll();
                    break;
                }
            }

            public void itemValueChanged(arc.gui.form.FormItem f) {
                if ( !ObjectUtil.equals(f.value(),text.value()) ) {
                    if ( !text.monitoredValueChanged() ) {
                        text.setValue(f.valueAsString(),false);
                    }
                }
            }
            
        });

    }
    
    public static Widget createReadOnlyWidgetFor(Field f,int height) {
        if ( useAutoResizeTextWidget( f ) ) {
            return createAutoResizeReadOnlyWidgetFor(f, height);
        }
        
        return createDefaultReadOnlyWidgetFor(f, height);
    }

    private static Widget createDefaultReadOnlyWidgetFor(Field f,int height) {
        TextArea ta = new TextArea();
        if ( f.value() != null ) {
            ta.setValue(f.valueAsString());
        }

        ta.setReadOnly(true);

        ta.setWidth(200);
        ta.setHeight(height);
        
        FormItemStyle.applyReadOnlyTo(ta);
        
        return ta;
    }

    private static Widget createAutoResizeReadOnlyWidgetFor(Field f,int height) {
        TextArea ta = new TextArea();
        if ( f.value() != null ) {
            ta.setValue(f.valueAsString());
        }

        ta.setReadOnly(true);

        ta.setWidth(200);
        ta.setHeight(height);
        
        FormItemStyle.applyReadOnlyTo(ta);
        
        return ta;
    }
}
