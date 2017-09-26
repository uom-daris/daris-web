package daris.web.client.model.object.exports;

import java.util.Date;

public class ShareOptions extends DownloadOptions {

    private Integer _maxUses;
    private Date _expiryDate;

    public ShareOptions() {
        _maxUses = null;
        _expiryDate = null;
    }

    public Integer maxUses() {
        return _maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        _maxUses = maxUses;
    }

    public Date expiryDate() {
        return _expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        _expiryDate = expiryDate;
    }

}
