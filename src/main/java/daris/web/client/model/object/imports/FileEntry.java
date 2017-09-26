package daris.web.client.model.object.imports;

import arc.mf.client.file.LocalFile;

public class FileEntry {
    public final LocalFile file;
    public final String dstPath;

    public FileEntry(LocalFile file, String dstPath) {
        this.file = file;
        this.dstPath = dstPath;
    }
}