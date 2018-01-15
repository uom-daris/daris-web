package daris.web.client.model.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.ObjectRef;

public class DestinationSetRef extends ObjectRef<List<Destination>> {

    public DestinationSetRef() {

    }

    @Override
    protected void resolveServiceArgs(XmlStringWriter w) {

    }

    @Override
    protected String resolveServiceName() {
        return "daris.shoppingcart.destination.list";
    }

    @Override
    protected List<Destination> instantiate(XmlElement xe) throws Throwable {
        List<XmlElement> des = xe.elements("destination");
        if (des != null && !des.isEmpty()) {
            List<Destination> ds = new ArrayList<Destination>(des.size());
            for (XmlElement de : des) {
                ShoppingCart.DeliveryMethod method = ShoppingCart.DeliveryMethod.fromString(de.value("@method"));
                Destination d = Destination.fromString(method, de.value());
                ds.add(d);
            }
            return ds;
        }
        return null;
    }

    @Override
    public String referentTypeName() {
        return "shopping cart destinations";
    }

    @Override
    public String idToString() {
        return null;
    }

}
