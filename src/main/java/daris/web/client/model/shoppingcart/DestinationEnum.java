package daris.web.client.model.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import arc.mf.dtype.DynamicEnumerationDataHandler;
import arc.mf.dtype.DynamicEnumerationDataSource;
import arc.mf.dtype.DynamicEnumerationExistsHandler;
import arc.mf.dtype.EnumerationType;
import daris.web.client.model.shoppingcart.ShoppingCart.DeliveryMethod;

public class DestinationEnum implements DynamicEnumerationDataSource<Destination> {

    private DestinationSetRef _ds;

    public DestinationEnum() {
        _ds = new DestinationSetRef();
    }

    @Override
    public boolean supportPrefix() {
        return false;
    }

    @Override
    public void exists(String value, DynamicEnumerationExistsHandler handler) {
        if (value == null) {
            handler.exists(value, false);
            return;
        } else if (value.equalsIgnoreCase(Destination.BROWSER)) {
            handler.exists(value, true);
            return;
        }
        final Destination d = Destination.fromString(DeliveryMethod.DEPOSIT, value);
        _ds.resolve(ds -> {
            handler.exists(value, ds != null && ds.contains(d));
        });
    }

    @Override
    public void retrieve(String prefix, long start, long end, DynamicEnumerationDataHandler<Destination> handler) {
        _ds.resolve(ds -> {
            if (ds != null && !ds.isEmpty()) {
                List<EnumerationType.Value<Destination>> vs = new ArrayList<EnumerationType.Value<Destination>>(
                        ds.size());
                for (Destination d : ds) {
                    EnumerationType.Value<Destination> v = new EnumerationType.Value<Destination>(d.toString(),
                            d.description(), d);
                    vs.add(v);
                }
                handler.process(0, ds.size(), ds.size(), vs);
                return;
            }
            handler.process(0, 0, 0, null);
        });
    }

}
