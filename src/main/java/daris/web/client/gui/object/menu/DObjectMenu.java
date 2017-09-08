package daris.web.client.gui.object.menu;

import java.util.List;

import arc.gui.image.Image;
import arc.gui.menu.ActionEntry;
import arc.gui.menu.DynamicMenuListener;
import arc.gui.menu.Entry;
import arc.gui.object.action.ActionInterface;
import arc.gui.object.action.precondition.ActionPrecondition;
import arc.gui.object.action.precondition.ActionPreconditionListener;
import arc.gui.object.action.precondition.ActionPreconditionOutcome;
import arc.gui.object.menu.ObjectMenu;
import arc.mf.client.util.Action;
import daris.web.client.model.object.DObject;
import daris.web.client.model.object.DObjectRef;
import daris.web.client.model.object.DObjectSummary;
import daris.web.client.model.object.DObjectSummaryRef;

public abstract class DObjectMenu extends ObjectMenu<DObject> {

    private DObjectRef _po;
    private DObjectRef _o;
    private DObjectSummaryRef _os;

    private boolean _dirty;

    public DObjectMenu(DObjectRef po, DObjectRef o) {
        super((String) null);
        _po = po;
        _o = o;
        _os = _o == null ? null : new DObjectSummaryRef(_o);
        _dirty = true;
    }

    public DObjectMenu(DObjectRef o) {
        this(o.isProject() ? null : o.parent(), o);
    }

    @Override
    public void create(DynamicMenuListener ml) {
        if (_os == null) {
            ml.created(this);
        } else {
            _os.resolve(os -> {
                clear();
                updateMenuItems(this, _po, _o, os);
                ml.created(this);
            });
        }
    }

    public void add(ActionEntry ae) {
        super.add(ae);
        _dirty = true;
    }

    public void add(ActionInterface<DObject> ai) {
        add(null, ai.actionName(), ai);
    }

    public void add(Image icon, ActionInterface<DObject> ai) {
        add(icon, ai.actionName(), ai);
    }

    public void add(Image icon, String label, ActionInterface<DObject> ai) {
        add(new ActionEntry(icon, label, ai.actionDescription(), ai, true));
    }

    protected abstract void updateMenuItems(DObjectMenu menu, DObjectRef po, DObjectRef o, DObjectSummary os);

    public void preShow() {
        if (_dirty) {
            _dirty = false;
            executeAllPreconditions();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeAllPreconditions() {
        List<Entry> es = entries();
        if (es != null) {
            for (Entry e : es) {
                if (e instanceof ActionEntry) {
                    ActionEntry ae = (ActionEntry) e;
                    Action action = ae.action();
                    if (action instanceof ActionInterface) {
                        ActionInterface<DObject> ai = (ActionInterface<DObject>) action;
                        executePreconditions(ai, ae);
                    }
                }
            }
        }
    }

    private void executePreconditions(ActionInterface<DObject> ai, final ActionEntry ae) {
        List<ActionPrecondition> ps = ai.beforeInteractionPreconditions();
        if (ps == null || ps.isEmpty()) {
            return;
        }

        ae.disable();

        for (ActionPrecondition p : ps) {

            p.execute(new ActionPreconditionListener() {

                public void executed(ActionPreconditionOutcome outcome, String message) {

                    if (outcome.equals(ActionPreconditionOutcome.PASS)) {
                        // May have been disabled by some other precondition. If
                        // so, don't re-enable.
                        if (!ae.softDisabled()) {
                            ae.enable();
                        }
                    } else if (outcome.equals(ActionPreconditionOutcome.CONDITIONAL_PASS)) {
                        // May have been disabled by some other precondition. If
                        // so, don't re-enable.
                        if (!ae.softDisabled()) {
                            ae.enable();
                        }
                    } else {
                        String reason = ae.disabledReason();
                        if (reason != null) {
                            message = reason + " AND " + message;
                        }
                        ae.softDisable(message);
                    }
                }
            });
        }
    }

}
