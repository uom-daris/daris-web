package daris.web.client;

import com.google.gwt.user.client.History;

import arc.mf.object.ObjectResolveHandler;
import daris.web.client.gui.DObjectExplorer;
import daris.web.client.model.CiteableIdUtils;
import daris.web.client.model.object.DObjectPath;
import daris.web.client.model.object.DObjectPathRef;
import daris.web.client.model.object.DObjectRef;

/**
 * 
 * @author wliu5
 *
 */
public class HistoryManager {

    // @formatter:off
    //
    // <>_<>                       // list projects
    // <>_<cid>                    // list projects, view project <cid>
    // <pid>_<>                    // list objects in <pid>
    // <pid>_<n>                   // list objects in <pid>, view child <n>
    //
    // @formatter:on

    public static String PATTERN_LIST_PROJECT = "^_(\\d+(\\.\\d+)*)*$";

    public static String PATTERN_LIST_OBJECT = "^(\\d+(\\.\\d+)*)_(\\d+)?$";

    private static boolean _initialized = false;

    public static void initialize() {

        if (!_initialized) {
            History.addValueChangeHandler(e -> {
                String token = e.getValue();
                if (token == null || token.equals("") || token.matches(PATTERN_LIST_PROJECT)) {
                    String projectCid = null;
                    if (token != null && token.length() > 1) {
                        projectCid = token.substring(1);
                    }
                    if (projectCid == null) {
                        DObjectExplorer.get().list(null, false);
                    } else {
                        DObjectExplorer.get().view(projectCid, false);
                    }
                } else if (token.matches(PATTERN_LIST_OBJECT)) {
                    int idx = token.indexOf('_');
                    String parentCid = token.substring(0, idx);
                    String ordinal = token.substring(idx + 1);
                    String cid = ordinal.isEmpty() ? null : (parentCid + "." + ordinal);
                    if (cid == null) {
                        DObjectExplorer.get().list(parentCid, false);
                    } else {
                        DObjectExplorer.get().view(cid, false);
                    }
                } else {
                    throw new AssertionError("Unknown token: " + token);
                }
            });
            History.newItem("_", false);
            _initialized = true;
        }
    }

    public static String tokenFor(DObjectRef parent, DObjectRef object) {
        if (parent == null) {
            if (object == null) {
                return "_";
            } else {
                return "_" + object.citeableId();
            }
        } else {
            if (object == null) {
                return parent.citeableId() + "_";
            } else {
                return parent.citeableId() + "_" + CiteableIdUtils.ordinal(object.citeableId());
            }
        }
    }

    public static void newItem(DObjectRef parent, DObjectRef object, boolean issueEvent) {
        History.newItem(tokenFor(parent, object), issueEvent);
    }

    public static void newItem(DObjectRef object, final boolean issueEvent) {
        if (object == null || object.isDataset()) {
            newItem(null, object, issueEvent);
        }
        new DObjectPathRef(object.citeableId()).resolve(new ObjectResolveHandler<DObjectPath>() {
            @Override
            public void resolved(DObjectPath o) {
                newItem(o.directParent(), o.object(), issueEvent);
            }
        });
    }

}
