package daris.web.client.model.object.exports;

import arc.mf.client.xml.XmlElement;

public class PathExpression {

    public final String name;
    public final String expression;
    public final String projectCID;

    public PathExpression(String name, String expression, String projectCID) {
        this.name = name;
        this.expression = expression;
        this.projectCID = projectCID;
    }

    public PathExpression(XmlElement ee) {
        this(ee.value("@name"), ee.value(), ee.value("@project"));
    }

    @Override
    public String toString() {
        return this.name;
    }

}
