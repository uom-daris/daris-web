package daris.web.client.gui.query.item;

import arc.gui.form.FieldDefinition;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.dtype.EnumerationType;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObject.Type;

public class DObjectTypeFilterField extends FilterField<DObject.Type> {

    private DObject.Type _type;

    public DObjectTypeFilterField() {
        super(new FieldDefinition("type", new EnumerationType<DObject.Type>(DObject.Type.values()), "Object type", null,
                0, 1));
        this.addListener(new FormItemListener<DObject.Type>() {

            @Override
            public void itemValueChanged(FormItem<Type> f) {
                _type = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<Type> f, Property property) {

            }
        });
    }

    @Override
    public void save(StringBuilder sb) {
        if (_type != null) {
            sb.append("model='om.pssd.").append(_type.toString()).append("'");
        }
    }

}
