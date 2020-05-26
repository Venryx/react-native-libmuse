package com.choosemuse.libmuse;

public final class ComputingDeviceConfigurationFactory {
    private static ComputingDeviceConfigurationFactory instance;

    public static ComputingDeviceConfigurationFactory getInstance() {
        if (instance == null) {
            synchronized (ComputingDeviceConfigurationFactory.class) {
                if (instance == null) {
                    instance = new ComputingDeviceConfigurationFactory();
                }
            }
        }
        return instance;
    }

    public ComputingDeviceConfiguration getComputingDeviceConfiguration() {
        return ComputingDeviceAndroid.getInstance().getComputingDeviceConfig();
    }
}
