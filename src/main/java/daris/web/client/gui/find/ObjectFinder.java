package daris.web.client.gui.find;

import java.sql.Date;

import daris.web.client.model.object.DObject;

public interface ObjectFinder {

    DObject.Type objectType();

    Date modifiedAfter();

    Date modifiedBefore();

    Date createdAfter();

    Date createdBefore();

    String buildQuery();

}
