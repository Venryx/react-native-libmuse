package com.choosemuse.libmuse;

public abstract class ReaderListener {
    public abstract void receiveAnnotation(AnnotationData annotationData);

    public abstract void receiveComputingDeviceConfiguration(ComputingDeviceConfiguration computingDeviceConfiguration);

    public abstract void receiveConfiguration(MuseConfiguration museConfiguration);

    public abstract void receiveVersion(MuseVersion museVersion);
}
