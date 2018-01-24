package daris.web.client.gui.query;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.ui.Widget;

import arc.gui.ValidatedInterfaceComponent;
import arc.gui.gwt.colour.RGB;
import arc.gui.gwt.widget.HTML;
import arc.gui.gwt.widget.panel.SimplePanel;
import arc.gui.gwt.widget.panel.VerticalPanel;
import arc.gui.gwt.widget.scroll.ScrollPanel;
import arc.gui.gwt.widget.scroll.ScrollPolicy;
import arc.mf.client.util.Validity;
import daris.web.client.gui.query.item.FilterForm;
import daris.web.client.gui.widget.DefaultStyles;
import daris.web.client.gui.widget.HtmlBuilder;
import daris.web.client.model.query.Filter;

public abstract class QuerySettingsForm extends ValidatedInterfaceComponent implements Filter {

    private VerticalPanel _vp;
    private SimplePanel _formSP;
    private FilterForm _form;
    private HTML _status;
    private String _prefixFilter;

    public QuerySettingsForm() {
        this(null);
    }

    public QuerySettingsForm(String prefixFilter) {
        _prefixFilter = prefixFilter;
        _vp = new VerticalPanel();
        _vp.fitToParent();
        _formSP = new SimplePanel();
        _formSP.fitToParent();
        _vp.add(_formSP);

        _status = new HtmlBuilder().setHeight(20).setLineHeight(20).setTextAlign(TextAlign.CENTER).setFontSize(11)
                .setFontFamily(DefaultStyles.FONT_FAMILY).build();
        _status.setWidth100();
        _status.setBorderTop(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _status.setBorderBottom(1, BorderStyle.SOLID, RGB.GREY_DDD);
        _status.setColour(RGB.RED);
        addChangeListener(() -> {
            Validity v = valid();
            _status.clear();
            if (!v.valid()) {
                _status.setHTML(v.reasonForIssue());
            }
        });
        _vp.add(_status);

        updateForm();
    }

    private void updateForm() {

        _formSP.clear();
        if (_form != null) {
            removeMustBeValid(_form);
        }
        _form = new FilterForm();
        addToForm(_form);
        _form.render();
        addMustBeValid(_form);
        _formSP.setContent(new ScrollPanel(_form, ScrollPolicy.AUTO));

    }

    public String prefixFilter() {
        return _prefixFilter;
    }

    public void setPrefixFilter(String prefixFilter) {
        _prefixFilter = prefixFilter;
    }

    protected abstract void addToForm(FilterForm form);

    @Override
    public void save(StringBuilder sb) {
        String query = _form.toQueryString();
        if (_prefixFilter != null) {
            if (query != null && !query.isEmpty()) {
                sb.append("(");
            }
            sb.append(_prefixFilter);
            if (query != null && !query.isEmpty()) {
                sb.append(")");
            }
        }
        if (query != null && !query.isEmpty()) {
            if (_prefixFilter != null) {
                sb.append(" and (");
            }
            sb.append(query);
            if (_prefixFilter != null) {
                sb.append(")");
            }
        }
    }

    public void reset() {
        if (_form != null) {
            _form.reset();
        }
    }

    @Override
    public String toQueryString() {
        StringBuilder sb = new StringBuilder();
        save(sb);
        return sb.toString();
    }

    @Override
    public Widget gui() {
        return _vp;
    }

}
