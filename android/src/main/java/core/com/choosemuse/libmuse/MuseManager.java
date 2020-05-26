package com.choosemuse.libmuse;

import java.util.ArrayList;

public abstract class MuseManager {
    public static final long DEFAULT_REMOVE_FROM_LIST_AFTER = 30;

    public abstract AdvertisingStats getAdvertisingStats(Muse muse);

    public abstract ArrayList getMuses();

    public abstract void removeFromListAfter(long j);

    public abstract void resetAdvertisingStats();

    public abstract void setMuseListener(MuseListener museListener);

    public abstract void startListening();

    public abstract void stopListening();
}
