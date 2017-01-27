package daris.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);

    @Source("resource/add_16.png")
    ImageResource add_16();

    @Source("resource/cross_16.png")
    ImageResource cross_16();

    @Source("resource/daris_16.png")
    ImageResource daris_16();

    @Source("resource/delete_12x16.png")
    ImageResource delete_12x16();

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

    @Source("resource/group_20.png")
    ImageResource group_20();

    @Source("resource/image_32.png")
    ImageResource image_32();

    @Source("resource/launch_16.png")
    ImageResource launch_16();

    @Source("resource/sub_16.png")
    ImageResource sub_16();

}
