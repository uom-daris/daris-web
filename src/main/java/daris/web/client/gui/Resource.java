package daris.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);

    @Source("resource/about_16.png")
    ImageResource about16();

    @Source("resource/add_16.png")
    ImageResource add16();

    @Source("resource/alert_16.png")
    ImageResource alert16();

    @Source("resource/check_16.png")
    ImageResource check16();

    @Source("resource/cross_16.png")
    ImageResource cross16();

    @Source("resource/daris_16.png")
    ImageResource daris16();

    @Source("resource/delete_12x16.png")
    ImageResource delete12x16();

    @Source("resource/document_32.png")
    ImageResource document32();

    @Source("resource/down_16.png")
    ImageResource down16();

    @Source("resource/down_20.png")
    ImageResource down20();

    @Source("resource/download_16.png")
    ImageResource download16();

    @Source("resource/download_dark_16.png")
    ImageResource downloadDark16();

    @Source("resource/error_16.png")
    ImageResource error16();

    @Source("resource/exclamation_16.png")
    ImageResource exclamation16();

    @Source("resource/exit_16.png")
    ImageResource exit16();

    @Source("resource/folder_16.png")
    ImageResource folder16();

    @Source("resource/folder_open_16.png")
    ImageResource folderOpen16();

    @Source("resource/folder_32.png")
    ImageResource folder32();

    @Source("resource/folder_enter_32.png")
    ImageResource folderEnter32();

    @Source("resource/group_20.png")
    ImageResource group20();

    @Source("resource/image_32.png")
    ImageResource image32();

    @Source("resource/launch_16.png")
    ImageResource launch16();

    @Source("resource/loading_16.gif")
    ImageResource loading16();

    @Source("resource/loading_56.gif")
    ImageResource loading56();

    @Source("resource/right_16.png")
    ImageResource right16();

    @Source("resource/share_16.png")
    ImageResource share16();

    @Source("resource/settings_16.png")
    ImageResource settings16();

    @Source("resource/sub_16.png")
    ImageResource sub16();

    @Source("resource/suspended_16.gif")
    ImageResource suspended16();

    @Source("resource/waiting_16.gif")
    ImageResource waiting16();

}
