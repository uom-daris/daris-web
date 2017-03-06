package daris.web.client.model.dataset;

import java.util.Collection;
import java.util.List;

import arc.mf.client.Output;
import arc.mf.client.RemoteServer;
import arc.mf.client.xml.XmlElement;
import arc.mf.object.ObjectMessageResponse;
import arc.mf.session.ServiceResponseHandler;
import arc.mf.session.Session;
import daris.web.client.model.object.ContentInfo;

public class NiftiDataset extends DerivedDataset {

    public static final String ASSET_MIME_TYPE = "nifti/series";

    public NiftiDataset(XmlElement oe) {
        super(oe);
    }

    public void numberOfNiftiFiles(ObjectMessageResponse<Integer> rh) {
        ContentInfo content = content();
        if (content == null) {
            rh.responded(0);
            return;
        }
        if ("gz".equalsIgnoreCase(content.ext()) || "nii".equalsIgnoreCase(content.ext())) {
            rh.responded(1);
            return;
        }
        if ("zip".equalsIgnoreCase(content.ext()) || "aar".equalsIgnoreCase(content.ext())) {
            Session.execute("asset.archive.content.list", "<id>" + assetId() + "</id>", new ServiceResponseHandler() {

                @Override
                public void processResponse(XmlElement xe, List<Output> outputs) throws Throwable {
                    Collection<String> entries = xe.values("entry");
                    int nbNiftiFiles = 0;
                    if (entries != null) {
                        for (String entry : entries) {
                            if (entry != null && (entry.endsWith(".nii") || entry.endsWith(".nii.gz")
                                    || entry.endsWith(".NII") || entry.endsWith(".NII.GZ"))) {
                                nbNiftiFiles++;
                            }
                        }
                    }
                    rh.responded(nbNiftiFiles);
                }
            });
        } else {
            rh.responded(0);
        }
    }

    public String niftiViewerUrl() {
        if (content() == null && !RemoteServer.haveSession()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(com.google.gwt.user.client.Window.Location.getProtocol());
        sb.append("//");
        sb.append(com.google.gwt.user.client.Window.Location.getHost());
        sb.append("/daris/nifti.mfjp?_skey=");
        sb.append(RemoteServer.sessionId());
        sb.append("&module=view&id=");
        sb.append(assetId());
        return sb.toString();
    }

}
