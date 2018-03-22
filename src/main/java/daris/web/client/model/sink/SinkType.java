package daris.web.client.model.sink;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arc.mf.client.util.DateTime;
import arc.mf.client.xml.XmlElement;
import arc.mf.dtype.AssetType;
import arc.mf.dtype.BooleanType;
import arc.mf.dtype.CiteableIdType;
import arc.mf.dtype.DataType;
import arc.mf.dtype.DateType;
import arc.mf.dtype.DocType;
import arc.mf.dtype.DoubleType;
import arc.mf.dtype.EmailAddressType;
import arc.mf.dtype.EnumerationType;
import arc.mf.dtype.FloatType;
import arc.mf.dtype.IdentifierType;
import arc.mf.dtype.IntegerType;
import arc.mf.dtype.LongType;
import arc.mf.dtype.NullType;
import arc.mf.dtype.PasswordType;
import arc.mf.dtype.StringType;
import arc.mf.dtype.TextType;
import arc.mf.dtype.UrlType;
import arc.mf.dtype.UserType;
import arc.mf.session.Session;

public class SinkType {

    public static class ArgumentDefinition {

        private String _name;
        private String _description;
        private DataType _type;

        private boolean _admin;
        private boolean _text;
        private boolean _time;
        private boolean _optional;
        private boolean _mutable;
        private String _pattern;
        private Object _defaultValue;
        private String[] _enumValues;
        private String[] _xorArgs;
        private boolean _secure;

        ArgumentDefinition(XmlElement ae) {
            _name = ae.value();
            _admin = false;
            _text = false;
            _time = false;
            _optional = true;
            _mutable = false;
            _pattern = null;
            _defaultValue = null;
            _enumValues = null;
            _xorArgs = null;
            _secure = false;
            String type = ae.value("@type");
            String description = ae.value("@description");
            String defaultValue = null;
            if (description == null || description.trim().isEmpty()) {
                _description = null;
            } else {
                description = description.trim();
                if (!description.matches(".*\\{\\{.*\\}\\}$")) {
                    _description = description;
                } else {
                    int idx1 = description.indexOf("{{");
                    int idx2 = description.lastIndexOf("}}");
                    if (idx1 >= 0 && idx2 > idx1) {
                        _description = description.substring(0, idx1);
                        String[] ps = description.substring(idx1 + 2, idx2).split(",");
                        for (String p : ps) {
                            p = p.trim();
                            if ("admin".equals(p)) {
                                _admin = true;
                            } else if ("text".equals(p)) {
                                _text = true;
                            } else if ("time".equals(p)) {
                                _time = true;
                            } else if ("optional".equals(p)) {
                                _optional = true;
                            } else if ("mutable".equals(p)) {
                                _mutable = true;
                            } else if (p != null && p.startsWith("pattern=")) {
                                _pattern = p.substring(8);
                            } else if (p != null && p.startsWith("default=")) {
                                defaultValue = p.substring(8);
                            } else if (p != null && p.startsWith("enum=")) {
                                _enumValues = p.substring(5).split("\\|");
                                type = EnumerationType.TYPE_NAME;
                            } else if (p != null && p.startsWith("xor")) {
                                _xorArgs = p.substring(4).split("\\|");
                            }
                        }
                    }
                }
            }

            if (StringType.TYPE_NAME.equals(type)) {
                if (_text) {
                    _type = TextType.DEFAULT;
                } else {
                    _type = new StringType();
                    if (_pattern != null) {
                        ((StringType) _type).setPattern(_pattern);
                    }
                }
                if (defaultValue != null) {
                    _defaultValue = defaultValue;
                }
            } else if (PasswordType.TYPE_NAME.equals(type)) {
                _secure = true;
                _type = PasswordType.DEFAULT;
                if (_text) {
                    _type = TextType.DEFAULT;
                } else {
                    _type = new PasswordType();
                }
                if (defaultValue != null) {
                    _defaultValue = defaultValue;
                }
            } else if (BooleanType.TYPE_NAME.equals(type)) {
                _type = BooleanType.DEFAULT_TRUE_FALSE;
                if (defaultValue != null) {
                    _defaultValue = Boolean.parseBoolean(defaultValue);
                }
            } else if (AssetType.TYPE_NAME.equals(type)) {
                _type = AssetType.DEFAULT;
                if (defaultValue != null) {
                    // TODO validate
                    _defaultValue = defaultValue;
                }
            } else if (CiteableIdType.TYPE_NAME.equals(type)) {
                _type = CiteableIdType.DEFAULT;
                if (defaultValue != null) {
                    // TODO validate
                    _defaultValue = defaultValue;
                }
            } else if (DateType.TYPE_NAME.equals(type)) {
                _type = _time ? DateType.DATE_AND_TIME : DateType.DATE_ONLY;
                if (defaultValue != null) {
                    try {
                        _defaultValue = DateTime.parseServerDate(defaultValue);
                    } catch (Throwable e) {
                        Session.displayError("Parsing sink argument defintion...", e);
                    }
                }
            } else if (DoubleType.TYPE_NAME.equals(type)) {
                _type = DoubleType.DEFAULT;
                if (defaultValue != null) {
                    _defaultValue = Double.parseDouble(defaultValue);
                }
            } else if (EmailAddressType.TYPE_NAME.equals(type)) {
                _type = EmailAddressType.DEFAULT;
                if (defaultValue != null) {
                    // TODO: validate
                    _defaultValue = defaultValue;
                }
            } else if (EnumerationType.TYPE_NAME.equals(type)) {
                _type = new EnumerationType<String>(_enumValues);
                if (defaultValue != null) {
                    if (_enumValues != null) {
                        boolean exists = false;
                        for (String v : _enumValues) {
                            if (defaultValue.equals(v)) {
                                exists = true;
                                break;
                            }
                        }
                        if (exists) {
                            _defaultValue = defaultValue;
                        }
                    }
                }
            } else if (FloatType.TYPE_NAME.equals(type)) {
                _type = FloatType.DEFAULT;
                if (defaultValue != null) {
                    _defaultValue = Float.parseFloat(defaultValue);
                }
            } else if (IdentifierType.TYPE_NAME.equals(type)) {
                _type = IdentifierType.DEFAULT;
                if (defaultValue != null) {
                    // TODO validate
                    _defaultValue = defaultValue;
                }
            } else if (IntegerType.TYPE_NAME.equals(type)) {
                _type = IntegerType.DEFAULT;
                if (defaultValue != null) {
                    _defaultValue = Integer.parseInt(defaultValue);
                }
            } else if (LongType.TYPE_NAME.equals(type)) {
                _type = LongType.DEFAULT;
                if (defaultValue != null) {
                    _defaultValue = Long.parseLong(defaultValue);
                }
            } else if (NullType.TYPE_NAME.equals(type)) {
                _type = NullType.DEFAULT;
            } else if (UrlType.TYPE_NAME.equals(type)) {
                _type = UrlType.DEFAULT;
                if (defaultValue != null) {
                    // TODO validate
                    _defaultValue = defaultValue;
                }
            } else if (UserType.TYPE_NAME.equals(type)) {
                _type = UserType.DEFAULT;
                if (defaultValue != null) {
                    // TODO validate
                    _defaultValue = defaultValue;
                }
            } else if (DocType.TYPE_NAME.equals(type)) {
                _type = DocType.DEFAULT;
            } else {
                throw new IllegalArgumentException("Unknown data type: " + type);
            }
        }

        public Object defaultValue() {
            return _defaultValue;
        }

        public String[] enumeratedValues() {
            return _enumValues;
        }

        public String[] xorArgs() {
            return _xorArgs;
        }

        public boolean admin() {
            return _admin;
        }

        public boolean mandatory() {
            return !_optional;
        }

        public boolean mutable() {
            return _mutable;
        }

        public boolean secure() {
            return _secure;
        }

        public String name() {
            return _name;
        }

        public String description() {
            return _description;
        }

        public DataType type() {
            return _type;
        }

        public boolean isAssetSpecific() {
            return _name.startsWith(SinkConstants.SINK_ARG_ASSET_SPECIFIC_PREFIX);
        }

        public boolean isAssetSpecificOutput() {
            return _name.equals(SinkConstants.SINK_ARG_ASSET_SPECIFIC_OUTPUT);
        }

        public boolean optional() {
            return _optional;
        }
    }

    private String _name;
    private String _description;
    private Map<String, ArgumentDefinition> _args;

    SinkType(XmlElement se) {

        _name = se.value("@type");
        _description = se.value("description");
        List<XmlElement> aes = se.elements("arg");
        if (aes != null && !aes.isEmpty()) {
            _args = new LinkedHashMap<String, ArgumentDefinition>(aes.size());
            for (XmlElement ae : aes) {
                ArgumentDefinition arg = new ArgumentDefinition(ae);
                _args.put(arg.name(), arg);
            }
        }
    }

    public String name() {
        return _name;
    }

    public String description() {
        return _description;
    }

    public Set<String> argNames() {
        if (_args != null) {
            return _args.keySet();
        }
        return null;
    }

    public boolean argExists(String argName) {
        return _args != null && _args.containsKey(argName);
    }

    public static boolean isLayoutPatternArg(String argName) {
        return SinkConstants.SINK_ARG_LAYOUT_PATTERN.equals(argName);
    }

    public boolean isSecureArg(String argName) {
        if (argExists(argName)) {
            ArgumentDefinition argDefn = _args.get(argName);
            return argDefn.secure();
        }
        return false;
    }

    public boolean isAssetSpecific(String argName) {
        if (argExists(argName)) {
            ArgumentDefinition argDefn = _args.get(argName);
            return argDefn.isAssetSpecific();
        }
        return false;
    }

    public boolean isAssetSpecificOutput(String argName) {
        if (argExists(argName)) {
            ArgumentDefinition argDefn = _args.get(argName);
            return argDefn.isAssetSpecificOutput();
        }
        return false;
    }

    public Map<String, ArgumentDefinition> argDefns() {
        return _args;
    }

    public ArgumentDefinition argDefn(String argName) {
        if (_args != null) {
            return _args.get(argName);
        }
        return null;
    }

    public DataType argType(String argName) {
        ArgumentDefinition arg = argDefn(argName);
        if (arg != null) {
            return arg.type();
        }
        return null;
    }

}
