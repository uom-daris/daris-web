package daris.web.client.model.project;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class ProjectCiteableRootNameSetRef extends ObjectRef<Set<String>> {

    public static final String ASSET_NAMESPACES_DICT = "daris:pssd.project.cid.rootnames";

    private static ProjectCiteableRootNameSetRef _instance;

    public static ProjectCiteableRootNameSetRef get() {
        if (_instance == null) {
            _instance = new ProjectCiteableRootNameSetRef();
        }
        return _instance;
    }

    private ProjectCiteableRootNameSetRef() {

    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {
        w.add("dictionary", ASSET_NAMESPACES_DICT);
    }

    @Override
    protected String resolveServiceName() {
        return "dictionary.entries.list";
    }

    @Override
    protected Set<String> instantiate(XmlElement xe) throws Throwable {
        Collection<String> terms = xe.values("term");
        return (terms == null || terms.isEmpty()) ? null : new TreeSet<String>(terms);
    }

    @Override
    public String referentTypeName() {
        return null;
    }

    @Override
    public String idToString() {
        return null;
    }

}
