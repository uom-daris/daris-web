package daris.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);

    @Source("resource/checkbox_checked_24.png")
    ImageResource checkbox_checked_24();

    @Source("resource/checkbox_indeterminate_24.png")
    ImageResource checkbox_indeterminate_24();

    @Source("resource/checkbox_unchecked_24.png")
    ImageResource checkbox_unchecked_24();

    @Source("resource/circle_12.png")
    ImageResource circle12();

    @Source("resource/circle_16.png")
    ImageResource circle16();

    @Source("resource/sub_12.png")
    ImageResource sub12();

    @Source("resource/sub_16.png")
    ImageResource sub16();

    @Source("resource/tick_12.png")
    ImageResource tick12();

    @Source("resource/tick_16.png")
    ImageResource tick16();

}
