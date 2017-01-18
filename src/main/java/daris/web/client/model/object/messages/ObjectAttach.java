package daris.web.client.model.object.messages;

import java.util.Arrays;
import java.util.List;

import arc.mf.client.file.LocalFile;
import arc.mf.client.xml.XmlElement;
import arc.mf.client.xml.XmlWriter;
import arc.mf.object.ObjectMessage;
import daris.web.client.model.object.AttachmentRef;

public class ObjectAttach extends ObjectMessage<AttachmentRef> {

	private String _cid;

	private LocalFile _input;

	public ObjectAttach(String cid, LocalFile f) {

		_cid = cid;
		_input = f;
	}

	@Override
	protected void messageServiceArgs(XmlWriter w) {

		w.add("cid", _cid);
		w.push("attachment");
		w.add("name", _input.name());
		if (_input.description() != null) {
			w.add("description", _input.description());
		}
		w.pop();

	}

	@Override
	protected String messageServiceName() {

		return "daris.object.attach";
	}

	@Override
	protected AttachmentRef instantiate(XmlElement xe) throws Throwable {

		if (xe != null) {
			XmlElement ae = xe.element("attachment");
			if (ae != null) {
				return new AttachmentRef(ae.value("@id"), ae.value("@name"));
			}
		}
		return null;
	}

	@Override
	protected String objectTypeName() {

		return "attachment";
	}

	@Override
	protected String idToString() {

		return _cid;
	}

	@Override
	protected List<LocalFile> inputs() {

		return Arrays.asList(_input);
	}

}
