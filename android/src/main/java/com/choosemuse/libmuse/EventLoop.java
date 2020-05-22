package com.choosemuse.libmuse;

public abstract class EventLoop {
    public abstract void post(Action action);

    public abstract void postDelayed(Action action, long j);
}
