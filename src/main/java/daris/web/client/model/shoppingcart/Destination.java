package daris.web.client.model.shoppingcart;

import arc.mf.client.util.ObjectUtil;
import daris.web.client.model.shoppingcart.ShoppingCart.DeliveryMethod;
import daris.web.client.model.sink.SinkRef;

public class Destination {

    public static final String BROWSER = "browser";

    public final DeliveryMethod method;
    public final String url;

    private Destination(DeliveryMethod method, String url) {
        this.method = method;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof Destination)) {
            Destination d = (Destination) o;
            return d.method == this.method && ObjectUtil.equals(d.url, this.url);
        }
        return false;
    }

    @Override
    public final String toString() {
        if (this.method == DeliveryMethod.DOWNLOAD) {
            return BROWSER;
        } else {
            return this.url;
        }
    }

    public String description() {
        if (this.method == DeliveryMethod.DOWNLOAD) {
            return "Download via " + BROWSER;
        } else {
            return "Send to " + this.url;
        }
    }

    public SinkRef sink() {
        String sinkName = sinkName();
        if (sinkName != null) {
            return new SinkRef(sinkName);
        }
        return null;
    }

    public String sinkName() {
        if (this.method == DeliveryMethod.DEPOSIT && this.url != null && this.url.startsWith("sink:")) {
            return this.url.substring(5);
        } else {
            return null;
        }
    }

    public static Destination fromString(DeliveryMethod method, String url) {
        if (method == DeliveryMethod.DOWNLOAD) {
            return new Destination(method, null);
        } else {
            return new Destination(method, url);
        }
    }

}
