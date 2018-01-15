package daris.web.client.model.shoppingcart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;

import arc.mf.client.util.DateTime;
import arc.mf.client.util.ObjectUtil;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.Session;
import daris.web.client.gui.Resource;
import daris.web.client.model.object.exports.PathExpression;
import daris.web.client.model.shoppingcart.messages.ShoppingCartProcessingDescribe;
import daris.web.client.model.sink.Sink;
import daris.web.client.model.transcode.Transcode;
import daris.web.client.util.NumberUtil;
import daris.web.client.util.SizeUtil;

public class ShoppingCart {

    public static final String TYPE_NAME = "shoppingcart";

    public static enum Status {

        // @formatter:off
        EDITABLE("editable", Resource.INSTANCE.starBorderBlue16()), 
        AWAIT_PROCESSING("await processing", Resource.INSTANCE.waiting16()), 
        ASSIGNED("assigned",Resource.INSTANCE.arrowRightInCircle16()), 
        PROCESSING("processing", Resource.INSTANCE.loading16()), 
        DATA_READY("data ready", Resource.INSTANCE.arrowDownInCircleGold16()), 
        FULFILLED("fulfilled", Resource.INSTANCE.arrowRightInCircle16()), 
        REJECTED("rejected", Resource.INSTANCE.exclamationTriangle16()), 
        ERROR("error",Resource.INSTANCE.crossRed16()), 
        WITHDRAWN("withdrawn", Resource.INSTANCE.exclamationTriangle16()), 
        ABORTED("aborted",Resource.INSTANCE.exclamationTriangle16());
        // @formatter:on
        private String _value;
        private ImageResource _icon;

        Status(String value, ImageResource icon) {
            _value = value;
            _icon = icon;
        }

        @Override
        public final String toString() {
            return _value;
        }

        public final String value() {
            return _value;
        }

        public arc.gui.image.Image icon() {
            return new arc.gui.image.Image(_icon.getSafeUri().asString(), 16, 16);
        }

        public static Status fromString(String value) {
            if (value != null) {
                Status[] vs = values();
                for (Status v : vs) {
                    if (value.equalsIgnoreCase(v.value())) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    public static class Log {

        public final Date changed;
        public final Status status;
        public final String message;

        Log(Date changed, Status status, String message) {
            this.changed = changed;
            this.status = status;
            this.message = message;
        }

        static Log instantiate(XmlElement le) {
            try {
                Date changed = le.dateValue("@changed");
                Status status = Status.fromString(le.value("@status"));
                String message = le.value();
                return new Log(changed, status, message);
            } catch (Throwable e) {
                Session.displayError("Parsing shopping cart log", e);
                return null;
            }
        }

        static List<Log> instantiate(List<XmlElement> les) {
            if (les != null) {
                List<Log> logs = new ArrayList<Log>(les.size());
                for (XmlElement le : les) {
                    logs.add(Log.instantiate(le));
                }
                if (!logs.isEmpty()) {
                    return logs;
                }
            }
            return null;
        }

    }

    public static class Owner {
        public final String type;
        public final String name;

        Owner(XmlElement oe) {
            this.type = oe.value("@type");
            this.name = oe.value();
        }

        static Owner instantiate(XmlElement oe) {
            if (oe != null) {
                return new Owner(oe);
            }
            return null;
        }
    }

    public static enum DeliveryMethod {
        DOWNLOAD, DEPOSIT;

        @Override
        public final String toString() {
            return name().toLowerCase();
        }

        public final String value() {
            return name().toLowerCase();
        }

        public static DeliveryMethod fromString(String method) {

            if (method != null) {
                DeliveryMethod[] vs = values();
                for (DeliveryMethod v : vs) {
                    if (v.value().equals(method)) {
                        return v;
                    }
                }
            }
            return null;
        }
    }

    public static class DeliveryArg {

        public static final String SECURE_WALLET_KEY_PREFIX = "swk://";

        public static enum Type {

            DELIVERY_ARG, SECURE_DELIVERY_ARG, SECURE_WALLET_DELIVERY_ARG;

            @Override
            public final String toString() {
                return value();
            }

            public final String value() {
                return name().toLowerCase().replace('_', '-');
            }

            public static Type fromString(String type) {
                if (type != null) {
                    Type[] vs = values();
                    for (Type v : vs) {
                        if (type.equalsIgnoreCase(v.value())) {
                            return v;
                        }
                    }
                }
                return null;
            }
        }

        private Type _type;
        private String _name;
        private String _value;

        public DeliveryArg(Type type, String name, String value) {
            _type = type;
            _name = name;
            _value = value;
        }

        public DeliveryArg(XmlElement ae) {
            this(Type.fromString(ae.name()), ae.value("@name"), ae.value());
        }

        public Type type() {
            return _type;
        }

        public String name() {
            return _name;
        }

        public String value() {
            return _value;
        }

        public void saveXml(XmlWriter w) {
            w.add(type().value(), new String[] { "name", name() }, value());
        }

        public static Map<String, DeliveryArg> instantiateArgs(XmlElement ce) {
            Map<String, DeliveryArg> args = new LinkedHashMap<String, DeliveryArg>();
            Type[] types = Type.values();
            for (Type type : types) {
                List<XmlElement> es = ce.elements(type.value());
                if (es != null) {
                    for (XmlElement e : es) {
                        DeliveryArg arg = new DeliveryArg(e);
                        args.put(arg.name(), arg);
                    }
                }
            }
            if (!args.isEmpty()) {
                return args;
            }
            return null;
        }
    }

    public static class Packaging {

        public static final String PARAMETER_ISO_TYPE = "iso-type";
        public static final String PARAMETER_ENABLE_ROCKRIDGE = "enable-rockridge";
        public static final String PARAMETER_ENABLE_JOLIET = "enable-joliet";
        public static final String PARAMETER_PUBLISHER = "publisher";
        public static final String PARAMETER_VOLUME_NAME = "volume-name";
        public static final String PARAMETER_COMPRESSION_LEVEL = "compression-level";

        public static final int DEFAULT_COMPRESSION_LEVEL = 6;

        public static enum Type {

            // @formatter:off
            NONE("none", null, false, null), 
            ZIP("zip", Arrays.asList(PARAMETER_COMPRESSION_LEVEL), true, "zip"), 
            AAR("aar",Arrays.asList(PARAMETER_COMPRESSION_LEVEL), true, "aar"), 
            JAR("jar", Arrays.asList(PARAMETER_COMPRESSION_LEVEL), true, "jar"), 
            TAR("tar", null, false, "tar"), 
            COMPRESSED_TAR("compressed-tar", Arrays.asList(PARAMETER_COMPRESSION_LEVEL), true,"tar.gz"), 
            ISO9660("iso9660",Arrays.asList(PARAMETER_ISO_TYPE, PARAMETER_ENABLE_ROCKRIDGE,PARAMETER_ENABLE_JOLIET, PARAMETER_PUBLISHER, PARAMETER_VOLUME_NAME), false, "iso");
            // @formatter:on

            private String _value;
            private Set<String> _params = null;
            private boolean _compressible = false;
            private String _extension = null;

            Type(String value, Collection<String> params, boolean compressible, String extension) {
                _value = value;
                _compressible = compressible;
                _extension = extension;
                if (params != null && !params.isEmpty()) {
                    _params = new HashSet<String>(params);
                }
            }

            public boolean compressible() {
                return _compressible;
            }

            @Override
            public final String toString() {
                return _value;
            }

            public final String value() {
                return _value;
            }

            public String extension() {
                return _extension;
            }

            public Set<String> parameters() {
                if (_params != null && !_params.isEmpty()) {
                    return Collections.unmodifiableSet(_params);
                } else {
                    return null;
                }
            }

            public static Type fromString(String type) {

                if (type != null) {
                    Type[] vs = values();
                    for (Type v : vs) {
                        if (v.toString().equals(type)) {
                            return v;
                        }
                    }
                }
                return NONE;
            }

            public static Type[] archiveTypes() {
                return new Type[] { ZIP, AAR, JAR, TAR, COMPRESSED_TAR, ISO9660 };
            }

        }

        public static enum IsoType {
            CD, DVD_SINGLE, DVD_DOUBLE;

            @Override
            public final String toString() {

                return value();
            }

            public final String value() {
                return name().toLowerCase().replace("_", "-");
            }

            public static IsoType fromString(String isoType) {

                if (isoType != null) {
                    IsoType[] vs = values();
                    for (IsoType v : vs) {
                        if (v.toString().equals(isoType)) {
                            return v;
                        }
                    }
                }
                return CD;
            }

        }

        private Type _type;
        private Map<String, String> _params;

        public Packaging(Type type) {

            setType(type);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Packaging)) {
                return false;
            }
            Packaging ao = (Packaging) o;
            return _type == ao.type() && daris.web.client.util.CollectionUtil.mapEquals(_params, ao.params());
        }

        public void setParam(String name, Object value) {
            setParam(name, String.valueOf(value));
        }

        public void setParam(String name, String value) {

            if (name == null) {
                return;
            }
            if (_params == null) {
                _params = new java.util.HashMap<String, String>();
            }
            _params.put(name, value);
        }

        protected void removeParam(String name) {

            if (_params != null) {
                _params.remove(name);
            }
        }

        public String paramValue(String name) {
            if (_params != null) {
                return _params.get(name);
            }
            return null;
        }

        public boolean hasParams() {

            if (_params != null) {
                return !_params.isEmpty();
            }
            return false;
        }

        public Map<String, String> params() {
            if (hasParams()) {
                return Collections.unmodifiableMap(_params);
            } else {
                return null;
            }
        }

        public Type type() {

            return _type;
        }

        public void setType(Type type) {

            if (_type != type) {
                _type = type;
                resetDefaultParameters(_type);
            }
        }

        private void resetDefaultParameters(Type type) {
            if (_params != null && !_params.isEmpty()) {
                _params.clear();
            }
            switch (type) {
            case ZIP:
            case AAR:
            case JAR:
            case COMPRESSED_TAR:
                setParam(PARAMETER_COMPRESSION_LEVEL, DEFAULT_COMPRESSION_LEVEL);
                break;
            case ISO9660:
                setParam(PARAMETER_ENABLE_JOLIET, Boolean.TRUE);
                setParam(PARAMETER_ENABLE_ROCKRIDGE, Boolean.FALSE);
                setParam(PARAMETER_ISO_TYPE, IsoType.CD);
                setParam(PARAMETER_PUBLISHER, (String) null);
                setParam(PARAMETER_VOLUME_NAME, (String) null);
                break;
            default:
                break;
            }
        }

        public void saveXml(XmlWriter w) {
            w.push("packaging");
            w.add("package-method", _type.value());
            if (hasParams()) {
                Set<String> names = _params.keySet();
                for (String name : names) {
                    w.add("parameter", new String[] { "name", name }, _params.get(name));
                }
            }
            w.pop();
        }

        // @formatter:off
        /**
         * Instantiate an archive object from the xml element in the form of:
         *    :cart
         *        :packaging zip
         *            :parameter -name compression-level 6
         * @param ce the XML element represents the shopping cart.
         * @return
         * @throws Throwable
         */
        // @formatter:on
        public static Packaging instantiate(XmlElement ce) throws Throwable {

            XmlElement pe = ce.element("packaging");
            if (pe == null) {
                return null;
            } else {
                Type type = Type.fromString(pe.value());
                Packaging archive = new Packaging(type);
                List<XmlElement> paramElems = pe.elements("parameter");
                if (paramElems != null) {
                    for (XmlElement paramElem : paramElems) {
                        archive.setParam(paramElem.value("@name"), paramElem.value());
                    }
                }
                return archive;
            }
        }

    }

    public static class Layout {

        public static enum Type {
            CUSTOM, FLAT, PRESERVED;

            public final String value() {
                return name().toLowerCase();
            }

            public final String toString() {
                return value();
            }

            public static Type fromString(String type) {
                Type[] ts = values();
                for (Type t : ts) {
                    if (t.value().equalsIgnoreCase(type)) {
                        return t;
                    }
                }
                return null;
            }

        }

        private Type _type;
        private PathExpression _pattern;

        public Layout(Type type, PathExpression pattern) {
            _type = type;
            _pattern = pattern;
        }

        public Layout(String name, String pattern, String projectCID) {
            this(Type.CUSTOM, new PathExpression(name, pattern, projectCID));
        }

        public Layout(XmlElement le) {
            _type = Layout.Type.fromString(le.value());
            String pattern = le.value("layout-pattern");
            String name = le.value("layout-pattern/@name");
            String projectCID = le.value("layout-pattern/@project");
            if (pattern != null) {
                _pattern = new PathExpression(name, pattern, projectCID);
            }
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof Layout)) {
                return false;
            }
            Layout lo = (Layout) o;
            return _type == lo.type() && ObjectUtil.equals(_pattern, lo.pattern());
        }

        public Type type() {
            return _type;
        }

        public PathExpression pattern() {
            return _pattern;
        }

        public static Layout instantiate(XmlElement le) throws Throwable {
            if (le != null) {
                return new Layout(le);
            }
            return null;
        }

        public void saveXml(XmlWriter w) {
            w.add("layout", _type.value());
            if (_type == Type.CUSTOM) {
                w.add("layout-pattern", _pattern.expression);
            }
        }
    }

    public static class Progress {
        public final long cartId;
        public final Date startTime;
        public final double duration;
        public final String durationUnit;
        public final int completed;
        public final int total;

        public Progress(XmlElement pe) throws Throwable {
            this.cartId = pe.longValue("@order");
            this.startTime = pe.dateValue("start-time");
            this.duration = pe.doubleValue("duration");
            this.durationUnit = pe.stringValue("duration/@unit");
            this.completed = pe.intValue("completed", 0);
            this.total = pe.intValue("total", 0);
        }

        public double progress() {
            if (this.total <= 0) {
                return 0.0;
            } else {
                return ((double) this.completed) / ((double) this.total);
            }
        }

        public String summary() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.completed).append("/").append(this.total);
            sb.append(" [").append(NumberUtil.toFixed(this.duration, 2)).append(" ").append(this.durationUnit)
                    .append("]");
            return sb.toString();
        }
    }

    public static interface ProgressListener {
        void progressed(Progress progress);
    }

    private long _cartId;
    private Status _status;
    private Date _changed;
    private List<Log> _logs;
    private String _name;
    private String _description;
    private boolean _decompressArchive;
    private String _template;
    private Owner _owner;
    private boolean _canModify;
    private boolean _canReEdit;
    private boolean _canWithdraw;
    private boolean _canReprocess;
    private boolean _canDestroy;
    private String _assignedTo;
    private String _metadataOutput;
    private boolean _selfServiced;
    private int _numberofItems = 0;
    private long _sizeOfItems = 0;
    private Map<String, Integer> _mimeTypeCount;
    private Map<String, Transcode> _transcodes;
    private DeliveryMethod _deliveryMethod;
    private Destination _destination;
    private Map<String, DeliveryArg> _deliveryArgs;
    private Packaging _packaging;
    private Layout _layout;

    protected ShoppingCart(XmlElement ce) throws Throwable {

        /*
         * id
         */
        _cartId = ce.longValue("@id");

        /*
         * status
         */
        _status = Status.fromString(ce.value("status"));

        /*
         * changed
         */
        _changed = ce.dateValue("status/@changed");

        /*
         * log
         */
        _logs = Log.instantiate(ce.elements("log"));

        /*
         * name
         */
        _name = ce.value("name");

        /*
         * description
         */
        _description = ce.value("description");

        /*
         * decompress-archive
         */
        _decompressArchive = ce.booleanValue("decompress-archive", true);

        /*
         * template
         */
        _template = ce.value("template");

        /*
         * owner
         */
        _owner = Owner.instantiate(ce.element("owner"));

        /*
         * access
         */
        _canModify = ce.booleanValue("access/can-modify", false);
        _canReEdit = ce.booleanValue("access/can-re-edit", false);
        _canWithdraw = ce.booleanValue("access/can-withdraw", false);
        _canReprocess = ce.booleanValue("access/can-reprocess", false);
        _canDestroy = ce.booleanValue("access/can-destroy", false);

        /*
         * assigned-to
         */
        _assignedTo = ce.value("assigned-to");

        /*
         * metadata-output
         */
        _metadataOutput = ce.value("metadata-output");

        /*
         * self-serviced
         */
        _selfServiced = ce.booleanValue("self-serviced", true);

        /*
         * content-statistics
         */
        XmlElement cse = ce.element("content-statistics");
        if (cse != null) {
            _numberofItems = cse.intValue("item-count", 0);
            _sizeOfItems = cse.longValue("item-size", 0);
            XmlElement cmte = cse.element("content-mimetype");
            if (cmte != null) {
                List<XmlElement> nes = cse.elements("name");
                if (nes != null && !nes.isEmpty()) {
                    _mimeTypeCount = new HashMap<String, Integer>();
                    for (XmlElement ne : nes) {
                        _mimeTypeCount.put(ne.value(), ne.intValue("@count", 0));
                    }
                }
            }
        }

        /*
         * delivery method
         */
        _deliveryMethod = DeliveryMethod.fromString(ce.value("delivery-method"));

        /*
         * destination
         */
        _destination = Destination.fromString(_deliveryMethod, ce.value("delivery-destination"));

        /*
         * args
         */
        _deliveryArgs = DeliveryArg.instantiateArgs(ce);

        /*
         * packaging
         */
        _packaging = Packaging.instantiate(ce);

        /*
         * layout & layout pattern
         */
        _layout = Layout.instantiate(ce.element("layout"));

        /*
         * data-transformation/transcode
         */
        List<XmlElement> tes = ce.elements("data-transformation/transcode");
        if (tes != null) {
            _transcodes = Transcode.instantiateMap(tes);
        }

    }

    public long id() {

        return _cartId;
    }

    public Status status() {

        return _status;
    }

    public Date changed() {
        return _changed;
    }

    public List<Log> logs() {
        if (_logs == null || _logs.isEmpty()) {
            return null;
        }
        return Collections.unmodifiableList(_logs);
    }

    public Map<String, Integer> mimeTypeCounts() {
        if (_mimeTypeCount == null) {
            return null;
        }
        return Collections.unmodifiableMap(_mimeTypeCount);
    }

    public String name() {

        return _name;
    }

    public void setName(String name) {

        _name = name;
    }

    public String description() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public boolean decompressArchive() {
        return _decompressArchive;
    }

    public String template() {
        return _template;
    }

    public Owner owner() {
        return _owner;
    }

    public boolean canModify() {
        return _canModify;
    }

    public boolean canReEdit() {
        return _canReEdit;
    }

    public boolean canWithdraw() {
        return _canWithdraw;
    }

    public boolean canReprocess() {
        return _canReprocess;
    }

    public boolean canDestroy() {
        return _canDestroy;
    }

    public String assignedTo() {
        return _assignedTo;
    }

    public String medatadataOutput() {

        return _metadataOutput;
    }

    public void setMetadataOutput(String mo) {

        _metadataOutput = mo;
    }

    public boolean selfServiced() {
        return _selfServiced;
    }

    public int numberOfContentItems() {

        return _numberofItems;
    }

    public long sizeOfContentItems() {

        return _sizeOfItems;
    }

    public Collection<Transcode> transcodesAvailable() {
        if (_transcodes == null || _transcodes.isEmpty()) {
            return null;
        }
        return Collections.unmodifiableCollection(_transcodes.values());
    }

    public void setTranscode(Transcode transcode) {

        if (_transcodes == null) {
            _transcodes = new LinkedHashMap<String, Transcode>();
        }
        _transcodes.put(transcode.from(), transcode);
    }

    public boolean hasTranscodesAvailable() {
        return _transcodes != null && !_transcodes.isEmpty();
    }

    public DeliveryMethod deliveryMethod() {
        return _deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        if (_deliveryMethod != deliveryMethod) {
            _deliveryMethod = deliveryMethod;
            if (_deliveryMethod == DeliveryMethod.DOWNLOAD) {
                if (_destination != null) {
                    _destination = null;
                }
                if (_packaging == null || _packaging.type() == Packaging.Type.NONE) {
                    _packaging = new Packaging(Packaging.Type.ZIP);
                }
            }
        }
    }

    public Destination destination() {
        return _destination;
    }

    /**
     * Sets destination.
     * 
     * @param destination
     * @return whether the destination value is changed.
     */
    public boolean setDestination(Destination destination) {
        if (!ObjectUtil.equals(_destination, destination)) {
            _destination = destination;
            if (_destination == null) {
                if (_deliveryMethod != DeliveryMethod.DOWNLOAD) {
                    _deliveryMethod = DeliveryMethod.DOWNLOAD;
                }
                if (_packaging == null || _packaging.type() == Packaging.Type.NONE) {
                    _packaging = new Packaging(Packaging.Type.ZIP);
                }
            } else {
                if (_deliveryMethod != DeliveryMethod.DEPOSIT) {
                    _deliveryMethod = DeliveryMethod.DEPOSIT;
                }
                _deliveryMethod = destination.method;
                if (_deliveryMethod == DeliveryMethod.DEPOSIT) {
                    if (_packaging == null || _packaging.type() != Packaging.Type.NONE) {
                        _packaging = new Packaging(Packaging.Type.NONE);
                    }
                } else {
                    if (_packaging == null || _packaging.type() == Packaging.Type.NONE) {
                        _packaging = new Packaging(Packaging.Type.ZIP);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public Packaging packaging() {
        return _packaging;
    }

    public void setPackaging(Packaging packaging) {
        _packaging = packaging;
    }

    public Layout layout() {

        return _layout;
    }

    public void setLayout(Layout layout) {
        _layout = layout;
    }

    public void saveUpdateArgs(XmlWriter w) {

        w.add("sid", _cartId);

        if (_name != null) {
            w.add("name", _name);
        }

        if (_description != null) {
            w.add("description", _description);
        }

        /*
         * delivery method, destination, delivery args
         */
        w.add("delivery", _deliveryMethod);
        if (_deliveryMethod == DeliveryMethod.DEPOSIT) {
            w.add("delivery-destination", _destination);
            if (_deliveryArgs != null) {
                Collection<DeliveryArg> args = _deliveryArgs.values();
                if (args != null) {
                    for (DeliveryArg arg : args) {
                        arg.saveXml(w);
                    }
                }
            }
        }

        /*
         * layout
         */
        _layout.saveXml(w);

        /*
         * arcive/packaging
         */
        _packaging.saveXml(w);

        /*
         * transcodes
         */
        if (hasTranscodesToDo()) {
            w.push("data-transformation");
            Collection<Transcode> transcodes = transcodesToDo();
            for (Transcode transcode : transcodes) {
                w.push("transform");
                w.add("from", transcode.from());
                w.add("to", transcode.to());
                w.pop();
            }
            w.pop();
        }

        /*
         * metadata-output
         */
        if (_metadataOutput != null) {
            w.add("metadata-output", _metadataOutput);
        }

        /*
         * decompress-archive
         */
        w.add("decompress-archive", _decompressArchive);
        
    }

    public boolean idEquals(ShoppingCartRef cart) {
        if (cart != null) {
            return id() == cart.id();
        }
        return false;
    }

    public boolean idEquals(ShoppingCart cart) {
        if (cart != null) {
            return id() == cart.id();
        }
        return false;
    }

    public String summaryHTML() {
        return "<b>Shopping Cart " + _cartId + " [Status: " + _status.toString() + ", Number of Datasets: "
                + _numberofItems + ", Total Size: " + SizeUtil.getHumanReadableSize(_sizeOfItems, true) + "]</b>";
    }

    public String toHTML() {

        String html = "<table><thead><tr><th align=\"center\" colspan=\"2\">Shopping-cart</th></tr><thead>";
        html += "<tbody>";
        html += "<tr><td><b>id:</b></td><td>" + _cartId + "</td></tr>";
        if (_name != null) {
            html += "<tr><td><b>name:</b></td><td>" + _name + "</td></tr>";
        }
        html += "<tr><td><b>status:</b></td><td>" + _status + "</td></tr>";
        if (_numberofItems > 0) {
            html += "<tr><td><b>content:</b></td><td>" + _numberofItems + "items (size=" + _sizeOfItems + " bytes)";
            if (_mimeTypeCount != null) {
                for (String mimeType : _mimeTypeCount.keySet()) {
                    html += "<br/>" + mimeType + ": " + _mimeTypeCount.get(mimeType);
                }
            }
            html += "</td></tr>";
        }
        if (_destination != null) {
            html += "<tr><td><b>destination:</b></td><td>" + _destination + "</td></tr>";
        }
        if (_packaging != null && _packaging.type() != Packaging.Type.NONE) {
            html += "<tr><td><b>archive:</b></td><td>type: " + _packaging.type() + "</td></tr>";
        }
        if (_transcodes != null) {
            for (Transcode transcode : _transcodes.values()) {
                html += "<tr><td><b>transcode:</b></td><td>from: " + transcode.from() + " to: " + transcode.to()
                        + "</td></tr>";
            }
        }
        if (_logs != null) {
            for (Log log : _logs) {
                html += "<tr><td><b>log:</b></td><td>[" + DateTime.dateTimeAsClientString(log.changed) + " status: "
                        + log.status + "] " + log.message + "</td></tr>";
            }
        }
        html += "</tbody></table>";
        return html;
    }

    private Timer _monitorTimer = null;

    /**
     * 
     * @param delay
     *            Interval in milliseconds
     * @param pl
     *            Progress listener
     * @return
     */
    public Timer startProgressMonitor(int delay, ProgressListener pl) {
        if (_monitorTimer == null) {
            _monitorTimer = new Timer() {
                @Override
                public void run() {
                    new ShoppingCartProcessingDescribe(ShoppingCart.this).send(new ObjectMessageResponse<Progress>() {

                        @Override
                        public void responded(Progress progress) {
                            if (_monitorTimer != null) {
                                if (progress == null) {
                                    _monitorTimer.cancel();
                                } else {
                                    _monitorTimer.schedule(delay);
                                }
                            }
                            pl.progressed(progress);
                        }
                    });
                }

                public void cancel() {
                    super.cancel();
                    _monitorTimer = null;
                }
            };
            _monitorTimer.schedule(1000);
        }
        return _monitorTimer;
    }

    public void stopProgressMonitor() {
        if (_monitorTimer != null) {
            _monitorTimer.cancel();
            _monitorTimer = null;
        }
    }

    public void setDecompressArchive(boolean decompressArchive) {
        _decompressArchive = decompressArchive;
    }

    public Map<String, DeliveryArg> deliveryArgs() {
        return _deliveryArgs;
    }

    public void importDeliveryArgsFromSink(Sink sink) {
        if (_deliveryArgs != null) {
            _deliveryArgs.clear();
        }
        Map<String, String> sinkArgs = sink.args();
        if (sinkArgs != null && !sinkArgs.isEmpty()) {
            if (_deliveryArgs == null) {
                _deliveryArgs = new LinkedHashMap<String, DeliveryArg>();
            }
            Set<String> argNames = sinkArgs.keySet();
            for (String argName : argNames) {
                String argValue = sink.argValue(argName);
                if (argValue != null && sink.isMutableArg(argName) && !sink.isAdminArg(argName)) {
                    if (sink.isSecureArg(argName)) {
                        if (argValue.startsWith(DeliveryArg.SECURE_WALLET_KEY_PREFIX)) {
                            _deliveryArgs.put(argName,
                                    new DeliveryArg(DeliveryArg.Type.SECURE_WALLET_DELIVERY_ARG, argName, argValue));
                        } else {
                            _deliveryArgs.put(argName,
                                    new DeliveryArg(DeliveryArg.Type.SECURE_DELIVERY_ARG, argName, argValue));
                        }
                    } else {
                        _deliveryArgs.put(argName, new DeliveryArg(DeliveryArg.Type.DELIVERY_ARG, argName, argValue));
                    }
                }
            }
        }
    }

    public void applyDeliveryArgsToSink(Sink sink) {
        if (_deliveryArgs == null || _deliveryArgs.isEmpty()) {
            return;
        }
        Collection<DeliveryArg> args = _deliveryArgs.values();
        for (DeliveryArg arg : args) {
            sink.setArg(arg.name(), arg.value());
        }
    }

    public void setLayoutPattern(PathExpression pe) {
        setLayout(new Layout(Layout.Type.CUSTOM, pe));
    }

    public PathExpression layoutPattern() {
        if (_layout != null) {
            return _layout.pattern();
        }
        return null;
    }

    public boolean hasTranscodesToDo() {
        List<Transcode> transcodesToDo = transcodesToDo();
        return transcodesToDo != null && !transcodesToDo.isEmpty();
    }

    public List<Transcode> transcodesToDo() {
        List<Transcode> toDo = new ArrayList<Transcode>();
        if (hasTranscodesAvailable()) {
            Collection<Transcode> transcodes = transcodesAvailable();
            for (Transcode transcode : transcodes) {
                if (transcode.to() != null) {
                    toDo.add(transcode);
                }
            }
        }
        if (!toDo.isEmpty()) {
            return toDo;
        }
        return null;
    }

}
