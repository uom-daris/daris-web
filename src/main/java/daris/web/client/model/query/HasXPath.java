package daris.web.client.model.query;

import java.util.Set;

public interface HasXPath {

    Set<XPath> xpaths();
    
    void addXPath(String xpath, String ename);
    
    boolean hasXPaths();

}
