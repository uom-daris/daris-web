package daris.web.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resource extends ClientBundle {
    Resource INSTANCE = GWT.create(Resource.class);

    public interface Style extends CssResource {
        @ClassName("my-css-class")
        String myCssClass();
    }

    @ClientBundle.Source("resource/_default.css")
    @CssResource.NotStrict
    Style css();

    @Source("resource/about-16.png")
    ImageResource about16();

    @Source("resource/add-blue-16.png")
    ImageResource addBlue16();

    @Source("resource/add-green-16.png")
    ImageResource addGreen16();

    @Source("resource/alert-16.png")
    ImageResource alert16();

    @Source("resource/arrow-down-in-circle-gold-16.png")
    ImageResource arrowDownInCircleGold16();

    @Source("resource/arrow-left-black-16.png")
    ImageResource arrowLeftBlack16();

    @Source("resource/arrow-refresh-black-16.png")
    ImageResource arrowRefreshBlack16();

    @Source("resource/arrow-right-16.png")
    ImageResource arrowRight16();

    @Source("resource/arrow-right-black-16.png")
    ImageResource arrowRightBlack16();

    @Source("resource/arrow-right-in-circle-16.png")
    ImageResource arrowRightInCircle16();

    @Source("resource/caret-down-black-16.png")
    ImageResource caretDownBlack16();

    @Source("resource/check-16.png")
    ImageResource check16();

    @Source("resource/checkbox-checked-16.png")
    ImageResource checkboxCheckedIcon();

    @Source("resource/checkbox-intermediate-16.png")
    ImageResource checkboxIntermediateIcon();

    @Source("resource/checkbox-unchecked-16.png")
    ImageResource checkboxUncheckedIcon();

    @Source("resource/clear-16.png")
    ImageResource clear16();

    @Source("resource/cross-16.png")
    ImageResource cross16();

    @Source("resource/cross-red-16.png")
    ImageResource crossRed16();

    @Source("resource/csv-16.png")
    ImageResource csv16();

    @Source("resource/daris-16.png")
    ImageResource daris16();

    @Source("resource/delete-12x16.png")
    ImageResource delete12x16();

    @Source("resource/delete-16.png")
    ImageResource delete16();

    @Source("resource/document-32.png")
    ImageResource document32();

    @Source("resource/double-left-chevron-16.png")
    ImageResource doubleLeftChevron16();

    @Source("resource/double-right-chevron-16.png")
    ImageResource doubleRightChevron16();

    @Source("resource/down-16.png")
    ImageResource down16();

    @Source("resource/down-20.png")
    ImageResource down20();

    @Source("resource/download-blue-16.png")
    ImageResource downloadBlue16();

    @Source("resource/download-gold-16.png")
    ImageResource downloadGold16();

    @Source("resource/edit-blue-16.png")
    ImageResource editBlue16();

    @Source("resource/edit-green-16.png")
    ImageResource editGreen16();

    @Source("resource/error-16.png")
    ImageResource error16();

    @Source("resource/exclamation-16.png")
    ImageResource exclamation16();

    @Source("resource/exclamation-triangle-16.png")
    ImageResource exclamationTriangle16();

    @Source("resource/exit-16.png")
    ImageResource exit16();

    @Source("resource/fa-desktop-monitor-16.png")
    ImageResource faDesktopMonitor16();

    @Source("resource/folder-16.png")
    ImageResource folder16();

    @Source("resource/folder-open-16.png")
    ImageResource folderOpen16();

    @Source("resource/folder-32.png")
    ImageResource folder32();

    @Source("resource/folder-enter-32.png")
    ImageResource folderEnter32();

    @Source("resource/group-20.png")
    ImageResource group20();

    @Source("resource/image-32.png")
    ImageResource image32();

    @Source("resource/import-16.gif")
    ImageResource import16();

    @Source("resource/launch-16.png")
    ImageResource launch16();

    @Source("resource/link-blue-16.png")
    ImageResource linkBlue16();

    @Source("resource/link-green-16.png")
    ImageResource linkGreen16();

    @Source("resource/loading-16.gif")
    ImageResource loading16();

    @Source("resource/loading-56.gif")
    ImageResource loading56();

    @Source("resource/loading-bar-128x15.gif")
    ImageResource loadingBar();

    @Source("resource/loading-bar-aborted-128x15.png")
    ImageResource loadingBarAborted();

    @Source("resource/loading-bar-completed-128x15.png")
    ImageResource loadingBarCompleted();

    @Source("resource/loading-bar-pending-128x15.png")
    ImageResource loadingBarPending();

    @Source("resource/options-16.png")
    ImageResource options16();

    @Source("resource/order-asc-16.png")
    ImageResource orderAsc16();

    @Source("resource/order-desc-16.png")
    ImageResource orderDesc16();

    @Source("resource/refresh-blue-16.png")
    ImageResource refreshBlue16();

    @Source("resource/refresh-green-16.png")
    ImageResource refreshGreen16();

    @Source("resource/remove-16.png")
    ImageResource remove16();

    @Source("resource/right-16.png")
    ImageResource right16();

    @Source("resource/search-16.png")
    ImageResource search16();

    @Source("resource/send-16.png")
    ImageResource send16();

    @Source("resource/settings-16.png")
    ImageResource settings16();

    @Source("resource/shoppingcart-black-16.png")
    ImageResource shoppingcartBlack16();

    @Source("resource/shoppingcart-blue-16.png")
    ImageResource shoppingcartBlue16();

    @Source("resource/shoppingcart-color-16.png")
    ImageResource shoppingcartColor16();

    @Source("resource/shoppingcart-green-16.png")
    ImageResource shoppingcartGreen16();

    @Source("resource/shoppingcart-purple-16.png")
    ImageResource shoppingcartPurple16();

    @Source("resource/shoppingcart-red-16.png")
    ImageResource shoppingcartRed16();

    @Source("resource/shoppingcart-yellow-16.png")
    ImageResource shoppingcartYellow16();

    @Source("resource/star-blue-16.png")
    ImageResource starBlue16();

    @Source("resource/star-border-blue-16.png")
    ImageResource starBorderBlue16();

    @Source("resource/sub-16.png")
    ImageResource sub16();

    @Source("resource/submit-16.png")
    ImageResource submit16();

    @Source("resource/suspended-16.gif")
    ImageResource suspended16();

    @Source("resource/tasks-16.png")
    ImageResource tasks16();

    @Source("resource/tick-green-16.png")
    ImageResource tickGreen16();

    @Source("resource/upload-16.png")
    ImageResource upload16();

    @Source("resource/waiting-16.gif")
    ImageResource waiting16();

    @Source("resource/warning-16.png")
    ImageResource warning16();

    @Source("resource/xml-16.png")
    ImageResource xml16();
}
