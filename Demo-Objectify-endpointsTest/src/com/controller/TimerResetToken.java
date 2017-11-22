package com.controller;

import com.entity.Credential;
import com.googlecode.objectify.ObjectifyService;

import java.util.Timer;
import java.util.TimerTask;
import static com.googlecode.objectify.ObjectifyService.ofy;
public class TimerResetToken extends Timer {
    static {
        ObjectifyService.register(Credential.class);
    }
    TimerTask task;
    public TimerResetToken(Credential credential){
        task = new TimerTask() {
            @Override
            public void run() {
                ofy().delete().entity(credential).now();

            }
        };
        this.schedule(task,20000);
    }
}
