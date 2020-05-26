package com.choosemuse.libmuse;

import android.os.Handler;
import android.util.Log;

public class HandlerEventLoop extends EventLoop {
    private final Handler handler;

    public HandlerEventLoop(Handler handler2) {
        this.handler = handler2;
    }

    private static Runnable toRunnable(final Action action) {
        return new Runnable() {
            public void run() {
                action.run();
            }
        };
    }

    public void post(Action action) {
        this.handler.post(toRunnable(action));
    }

    public void postDelayed(Action action, long j) {
    	//Log.w("Test1", "Data:" + action + ";" + j); // v
        this.handler.postDelayed(toRunnable(action), j);
    }
}
