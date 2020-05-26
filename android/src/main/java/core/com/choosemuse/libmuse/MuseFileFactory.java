package com.choosemuse.libmuse;

import java.io.File;

public final class MuseFileFactory {
    public static MuseFileWriter getMuseFileWriter(File file) {
        return MuseFileWriter.getFileWriter(new MuseFileAndroid(file));
    }

    public static MuseFileReader getMuseFileReader(File file) {
        return MuseFileReader.getFileReader(new MuseFileAndroid(file));
    }

    public static MuseFile getMuseFile(File file) {
        return new MuseFileAndroid(file);
    }
}
