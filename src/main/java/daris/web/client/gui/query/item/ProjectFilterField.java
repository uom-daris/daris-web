package daris.web.client.gui.query.item;

import arc.gui.form.FieldDefinition;
import arc.gui.form.FieldRenderOptions;
import arc.gui.form.FormItem;
import arc.gui.form.FormItemListener;
import arc.mf.dtype.EnumerationType;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.project.ProjectEnumDataSource;

public class ProjectFilterField extends FilterField<DObjectRef> {

    private DObjectRef _project;
    private boolean _includeProject;

    public ProjectFilterField() {
        this(0, false);
    }

    public ProjectFilterField(boolean includeProject) {
        this(0, includeProject);
    }

    public ProjectFilterField(int minOccurs, boolean includeProject) {
        super(new FieldDefinition("project", new EnumerationType<DObjectRef>(new ProjectEnumDataSource()),
                "Project filter", null, minOccurs, 1));
        _includeProject = includeProject;
        setRenderOptions(new FieldRenderOptions().setWidth100());
        addListener(new FormItemListener<DObjectRef>() {

            @Override
            public void itemValueChanged(FormItem<DObjectRef> f) {
                _project = f.value();
            }

            @Override
            public void itemPropertyChanged(FormItem<DObjectRef> f, Property property) {
            }
        });
    }

    @Override
    public void save(StringBuilder sb) {
        if (_project != null) {
            if (_includeProject) {
                sb.append("(cid='" + _project.citeableId() + "' or ");
            }
            sb.append("cid starts with '" + _project.citeableId() + "'");
            if (_includeProject) {
                sb.append(")");
            }
        }
    }

    public boolean includeProject() {
        return _includeProject;
    }

    public DObjectRef project() {
        return _project;
    }

}
