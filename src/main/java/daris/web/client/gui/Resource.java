package daris.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);
    

    @Source("resource/daris_16.png")
    ImageResource daris_16();

    @Source("resource/document_32.png")
    ImageResource document_32();

    @Source("resource/down_16.png")
    ImageResource down_16();

    @Source("resource/down_20.png")
    ImageResource down_20();

    @Source("resource/folder_32.png")
    ImageResource folder_32();

    @Source("resource/folder_enter_32.png")
    ImageResource folder_enter_32();

    @Source("resource/image_32.png")
    ImageResource image_32();

    @Source("resource/launch_16.png")
    ImageResource launch_16();

}
