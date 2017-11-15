package arc.gui.gwt.form.item;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import arc.gui.form.Field;
import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.Form;
import arc.gui.form.FormItemListener;
import arc.gui.form.SpecialFieldValueDefinition;
import arc.gui.gwt.data.CompoundDataSource;
import arc.gui.gwt.data.DataLoadAction;
import arc.gui.gwt.data.DataLoadHandler;
import arc.gui.gwt.data.DataSource;
import arc.gui.gwt.data.LocalDataSource;
import arc.gui.gwt.data.filter.Filter;
import arc.gui.gwt.data.filter.PrefixFilter;
import arc.gui.gwt.dimension.Dimension;
import arc.gui.gwt.dimension.NormalizedDimension;
import arc.gui.gwt.form.FormItemFactory;
import arc.gui.gwt.widget.ContainerWidget;
import arc.gui.gwt.widget.combo.ComboBox;
import arc.gui.gwt.widget.combo.ComboBoxEntry;
import arc.gui.gwt.widget.combo.ComboBoxSeparator;
import arc.gui.gwt.widget.label.Label;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.util.StringUtil;
import arc.mf.client.util.Transform;
import arc.mf.client.util.Transformer;
import arc.mf.client.util.Validity;
import arc.mf.dtype.DataType;
import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.EnumerationType.Value;
//@formatter:off
@SuppressWarnings({"unchecked", "rawtypes", "incomplete-switch"})
public class EnumerationTypeFormItem implements FormItem {
    
    public Widget create(Form form,final Field f,FormItemFocusListener fl,FormSubmitOnEnter fse) {
        FieldDefinition defn = f.definition();
        EnumerationType type = (EnumerationType)defn.type();
        
        Widget w;
        if ( type.multiSelect() ) {
            w = createMultiSelect(f,type);
        } else {
            w = createSingleSelect(form,f,fl,type);
        }
        
        return w;
    }
    
    /**
     * The special values data source will only come into effect if there is a filter active.
     * E.g. someone has typed some text into a search / filter box. This means the behavior of 
     * pull down (the primary list) is different to searching (filter) when there are special
     * values.
     * 
     * @author jason
     *
     */
    private static class SpecialValuesDataSource extends LocalDataSource<ComboBoxEntry> {

        public SpecialValuesDataSource(List<ComboBoxEntry> ses) {
            super(ses);
        }
        
        @Override
        public void load(Filter f, long start, long end, DataLoadHandler<ComboBoxEntry> lh) {
            if ( f == null ) {
                lh.loaded(start, end, 0, null, DataLoadAction.ADD);
            } else {
                super.load(f, start, end, lh);
            }
        }
        
    }
    
    private static Widget createSingleSelect(Form form,final Field f,final FormItemFocusListener fl,final EnumerationType type) {
        final ComboBox cb;
        FieldRenderOptions ro = f.renderOptions();
        
        // The field might have special (out of band) values .. we'll change the display based on those 
        List<SpecialFieldValueDefinition> svs = f.specialValues();

        List<ComboBoxEntry> ses = null;
        if ( svs != null ) {
            // Add a separator (for the special values).. 
//          ses = ListUtil.addTo(ses,new ComboBoxSeparator());

            ses = Transform.transform(svs,new Transformer<SpecialFieldValueDefinition,ComboBoxEntry>() {
                @Override
                protected ComboBoxEntry doTransform(SpecialFieldValueDefinition ev) throws Throwable {
                    ComboBoxEntry cbe = new ComboBoxEntry(ev.value(),ev.value(),ev.description());
                    
                    // We want to allow people to select special values, but we don't want them showing
                    // up in the list of values (we could, but that's really tricky for dictionary values,
                    // so for consitency, we won't).
                    //cbe.setVisible(false);
                    return cbe;
                }
            });
            
        }

        // If there is no data source, then create the source from the static list.
        if ( type.dataSource() == null ) {

            List<ComboBoxEntry> es = Transform.transform(type.values(),new Transformer<EnumerationType.Value,ComboBoxEntry>() {
                @Override
                protected ComboBoxEntry doTransform(Value ev) throws Throwable {
                    if ( ev instanceof EnumerationType.Separator ) {
                        return new ComboBoxSeparator();
                    }
                    
                    return new ComboBoxEntry(ev.title(),ev.value(),ev.description());
                }
                
            });

            DataSource<ComboBoxEntry<?>> ds = new LocalDataSource(es);

            // If there are special values, then create a compound data source that serves 
            // the primary and then the special values.
            if ( ses != null ) {
                ds = new CompoundDataSource(ds,new SpecialValuesDataSource(ses));
            }

            cb = new ComboBox(ds) {
                public void onAttach() {
                    super.onAttach();

                    if ( f.focus() ) {
                        super.setFocus(true);
                    }
                }
            };

        } else {
            DataSource<ComboBoxEntry<?>> ds = createRemoteDataSource(type); // TODO: Modified by Wilson Liu
            
            if ( ses != null ) {
                ds = new CompoundDataSource(ds,new SpecialValuesDataSource(ses));
            }
            
            cb = new ComboBox(ds) {
                public void onAttach() {
                    super.onAttach();

                    if ( f.focus() ) {
                        super.setFocus(true);
                    }
                }
            };
        }

        cb.setEnabled(f.enabled());
        cb.setVisible(f.visible());
        cb.setEmptyMessage(type.emptyMessage());
        
        try {
            if (ro != null && ro.booleanValue(EnumerationType.OPTION_TOOLTIP_DISPLAY_DESCRIPTION)) {
                cb.setTooltipDisplayDescription(true);
            }
        } catch (Throwable t) {}
        
        if (fl != null) {
            cb.addFocusListener(new ComboBox.FocusListener() {
                @Override
                public void focusOn(ComboBox cb) {
                    fl.focusOn(f);
                }

                @Override
                public void focusOff(ComboBox cb) {
                    fl.focusOff(f);
                }
            });
        }
        
        // Account for scrollbar width.
        int cbw = -1;
        if ( ro == null || ro.width() == null ) {
            // Account for padding in the combobox (left=5, right=5):
            
            int maxWordLength = type.maxWordLength();
            
            // Take into account any special values..
            if ( svs != null ) {
                String longestSpecialValue = StringUtil.longestString(svs);
                if ( longestSpecialValue.length() > maxWordLength ) {
                    maxWordLength = longestSpecialValue.length();
                }
            }
            

            cbw = widthForTextLength(maxWordLength);

            if (cb.showMultiSelect()) {
                cbw += 25;
            }
            
            if ( ro != null && cbw > ro.maxWidth() ) {
                cbw = ro.maxWidth();
            }
            
            cb.setWidth(cbw);
            cb.setPreferredWidth(cbw);
        } else {
            Dimension roWidth = ro.width();
            if (roWidth instanceof NormalizedDimension) {
                cb.setPreferredWidth(roWidth);
            } else {
                cb.setWidth((int)roWidth.transform(1));
                cb.setPreferredWidth((int)roWidth.transform(1));
            }
        }
        
        if ( f.value() != null ) {
            String t = type.titleForValue(f.value());
            
            // If the width of the value (invalid) is larger than the type
            // width, then use that to display properly...
            if ( cbw != -1 && t != null ) {
                int mtl = widthForTextLength(t.length());
                
                if ( ro != null && mtl > ro.maxWidth() ) {
                    mtl = ro.maxWidth();
                }

                if ( mtl > cbw ) {
                    cb.setWidth(mtl);
                    cb.setPreferredWidth(mtl);
                }
            }
            
            cb.setValue(t,f.value());
        }

        final FormItemListener fil = new FormItemListener() {
            public void itemValueChanged(arc.gui.form.FormItem f) {
                cb.setValue(type.titleForValue(f.value()));
            }

            public void itemPropertyChanged(arc.gui.form.FormItem f,arc.gui.form.FormItem.Property p) {
            }
        };
        
        f.addListener(fil);
        
        cb.addChangeListener(new ComboBox.ChangeListener() {
            public void changed(ComboBox cb) {
                validate(cb, f, type);

                // Since we have done the validation, then don't validate the field (again) - as that 
                // might override our validation (where the field is invalid).
                f.setValueNoValidate(cb.value(),fil);
            }
        });
        
        final ComboBox cbb = cb;
        form.addRenderListener(f,new FormItemListener() {
            public void itemPropertyChanged(arc.gui.form.FormItem f,arc.gui.form.FormItem.Property property) {
                switch ( property ) {
                case VISIBILITY:
                    cbb.setVisible(f.visible());
                    break;
                    
                case ENABLED:
                    cbb.setEnabled(f.enabled());
                    FormItemFactory.updateStyle(cbb, f);
                    break;
                    
                case FOCUS:
                    cbb.setFocus(f.focus());
                    break;
                    
                case SELECT:
                    cbb.selectValue();
                    break;
                }
            }

            public void itemValueChanged(arc.gui.form.FormItem f) {
                if ( !ObjectUtil.equals(f.value(),cbb.value()) ) {
                    cbb.setValue(type.titleForValue(f.value()));
                }
            }
            
        });

        FormItemStyle.applyReadWriteTo(cb.valueWidget());

        if ( f.loadAndSetToFirstEntry() ) {
            cb.loadAndSetValueToFirstEntry();
        }

        return cb;
    }

    public static int widthForTextLength(int textLen) {
        // Now 0.6 of the font size is used because the aspect ratio of most fonts ranges from
        // 0.4 - 0.55, so 0.55 gives a fair overhead.
        int w = (int)(textLen * ComboBox.defaultFontSize() * 0.55);

        // We know there is 10 pixels of padding.
        w += ComboBox.SCROLLBAR_WIDTH + ComboBox.NON_TEXT_WIDTH + 10;
        
        // Don't let the combo box get too big!
        if ( w > 300 ) {
            w = 300;
        }

        return w;
    }
    
    private static Widget createMultiSelect(Field f,EnumerationType type) {
        return new Label("TODO~");
    }
    
    /**
     * Creates a data source for the combo box from the enumeration type.
     * 
     * @param type
     * @return
     */
    private static <T> DataSource<ComboBoxEntry<T>> createRemoteDataSource(EnumerationType<T> type) {
        return createComboBoxDataSource(type.dataSource());
    }
    
    public static <T> DataSource<ComboBoxEntry<T>> createComboBoxDataSource(final DynamicEnumerationDataSource ds) {
    
        return new DataSource<ComboBoxEntry<T>>() {

            public boolean isRemote() {
                return true;
            }

            public boolean supportCursor() {
                return true;
            }

            public void load(Filter f, long start, long end, final DataLoadHandler<ComboBoxEntry<T>> lh) {
                
                String prefix = null;
                
                if ( f instanceof PrefixFilter ) {
                    PrefixFilter pf = (PrefixFilter)f;
                    prefix = pf.prefix();
                }
                     
                ds.retrieve(prefix, start, end, new DynamicEnumerationDataHandler<T>() {
                    public void process(long start, long end, long total,List<Value<T>> values) {
                        if ( values == null ) {
                            lh.loaded(start, end, total, null, null);
                            return;
                        }
                        
                        List<ComboBoxEntry<T>> cbes = new ArrayList<ComboBoxEntry<T>>(values.size());
                        for ( Value v : values ) {
                            cbes.add(new ComboBoxEntry<T>(v.title(),(T)v.value(),v.description()));
                        }
                        
                        lh.loaded(start, end, total, cbes, DataLoadAction.REPLACE);
                    }
                });
            }
        };
    }
    
    public static boolean validate(ContainerWidget widget, Field field, DataType dt) {
        ComboBox cb = ((ComboBox) widget);
        
        Validity v = cb.valid();
        
        if (v == null || v.valid() ) {
            field.clearInvalid();
            return true;
        }
        
        String message = "Expected a value in the dropdown list. Found value: " + cb.label();

        field.markInvalid(message);
        return false;
    }
}
