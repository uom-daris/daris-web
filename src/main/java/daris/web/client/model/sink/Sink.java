package daris.web.client.model.sink;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import arc.mf.client.Output;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlStringWriter;
import arc.mf.object.Null;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.sink.SinkType.ArgumentDefinition;

public class Sink {

    private String _name;

    private String _description;

    private SinkType _type;

    private Map<String, String> _args;

    private Map<String, Boolean> _mutable;

    Sink(XmlElement se, XmlElement ste) {
        _name = se.value("@name");
        _description = se.value("@description");
        _type = new SinkType(ste);
        _args = new LinkedHashMap<String, String>();
        _mutable = new HashMap<String, Boolean>();
        Map<String, SinkType.ArgumentDefinition> argDefns = _type.argDefns();
        List<XmlElement> aes = se.elements("destination/arg");
        if (aes != null && !aes.isEmpty()) {
            for (XmlElement ae : aes) {
                String argName = ae.value("@name");
                String argValue = ae.value();
                _args.put(argName, argValue);
                ArgumentDefinition argDefn = argDefns.get(argName);
                _mutable.put(argName, argDefn.mutable() || false);
            }
        }

        Set<String> argNames = argDefns.keySet();
        for (String argName : argNames) {
            if (!_mutable.containsKey(argName)) {
                _mutable.put(argName, true);
            }
        }
    }

    private Sink(String name, SinkType type, Map<String, String> args, Map<String, Boolean> mutable) {
        _name = name;
        _type = type;
        _args = new LinkedHashMap<String, String>(args);
        _mutable = new HashMap<String, Boolean>(mutable);
    }

    public void setArg(String name, String value) {
        if (!_mutable.get(name)) {
            System.err.println("Warning: Argument: " + name + " for sink " + _name + " is imutable. No change.");
            return;
        }
        if (!_type.argExists(name)) {
            System.err.println("Warning: Argument: " + name + " is not defined for sink " + _name + ". No change.");
            return;
        }
        if (_args == null) {
            _args = new LinkedHashMap<String, String>();
        }
        _args.put(name, value);
    }

    public Map<String, String> args() {
        return _args;
    }

    public Map<String, String> args(Boolean mutable, Boolean secure) {
        Map<String, ArgumentDefinition> argDefns = _type.argDefns();
        if (argDefns == null || argDefns.isEmpty()) {
            return null;
        }
        Set<String> argNames = argDefns.keySet();
        Map<String, String> args = new LinkedHashMap<String, String>();
        for (String argName : argNames) {
            String argValue = argValue(argName);
            boolean isMutableArg = isMutableArg(argName);
            boolean isSecureArg = isSecureArg(argName);
            if (mutable != null && isMutableArg != mutable) {
                continue;
            }
            if (secure != null && isSecureArg != secure) {
                continue;
            }
            if (argValue != null) {
                args.put(argName, argValue);
            }
        }
        if (!args.isEmpty()) {
            return args;
        }
        return null;
    }

    public String argValue(String argName) {
        return _args.get(argName);
    }

    public boolean isMutableArg(String argName) {
        return _mutable.get(argName);
    }

    public boolean isSecureArg(String argName) {
        return _type.isSecureArg(argName);
    }

    public boolean isAssetSpecific(String argName) {
        return _type.isAssetSpecific(argName);
    }

    public boolean isAssetSpecificOutput(String argName) {
        return _type.isAssetSpecificOutput(argName);
    }

    public boolean isAdminArg(String argName) {
        ArgumentDefinition argDefn = _type.argDefn(argName);
        if (argDefn != null) {
            return argDefn.admin();
        } else {
            return false;
        }
    }

    public String name() {
        return _name;
    }

    public SinkType type() {
        return _type;
    }

    public Sink copy() {
        return new Sink(_name, _type, _args, _mutable);
    }

    public void saveToUserSettings(final ObjectMessageResponse<Null> rh) {
        if (_args == null || _args.isEmpty()) {
            if (rh != null) {
                rh.responded(new Null());
            }
            return;
        }
        XmlStringWriter w = new XmlStringWriter();
        w.add("app", "sink." + _name);
        w.push("settings");
        Set<String> argNames = _args.keySet();
        for (String argName : argNames) {
            String argValue = argValue(argName);
            boolean isMutableArg = isMutableArg(argName);
            boolean isSecureArg = isSecureArg(argName);
            if (argValue != null && isMutableArg && !isSecureArg) {
                w.add("arg", new String[] { "name", argName }, argValue);
            }
        }
        w.pop();
        Session.execute("user.self.settings.set", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                if (rh != null) {
                    rh.responded(new Null());
                }
            }
        });

    }

    public void loadFromUserSettings(final ObjectMessageResponse<Null> rh) {
        XmlStringWriter w = new XmlStringWriter();
        w.add("app", "sink." + _name);
        Session.execute("user.self.settings.get", w.document(), new ServiceResponseHandler() {

            @Override
            public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                List<XmlElement> aes = xe.elements("settings/arg");
                if (aes != null) {
                    for (XmlElement ae : aes) {
                        String argName = ae.value("@name");
                        String argValue = ae.value();
                        if (argName != null && argValue != null) {
                            Sink.this.setArg(argName, argValue);
                        }
                    }
                }
                if (rh != null) {
                    rh.responded(new Null());
                }
            }
        });
    }

    public String description() {
        return _description;
    }
    
    @Override
    public String toString(){
        return name();
    }

}
