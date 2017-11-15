package daris.web.client.model.user;

import arc.mf.object.ObjectResolveHandler;
import daris.web.client.model.user.messages.ActorSelfHave;

public class UserSelf {

    private static Boolean _isAdmin;

    public static void isAdministrator(ObjectResolveHandler<Boolean> rh) {
        if (_isAdmin != null) {
            rh.resolved(_isAdmin);
            return;
        }
        new ActorSelfHave("system-administrator", "role").send(r -> {
            _isAdmin = r;
            rh.resolved(_isAdmin);
        });
    }

}
