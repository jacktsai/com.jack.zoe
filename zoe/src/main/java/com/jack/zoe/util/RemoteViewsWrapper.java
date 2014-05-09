package com.jack.zoe.util;

import android.widget.RemoteViews;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class RemoteViewsWrapper {

    private final Iterable<ActionWrapper> actionWrappers;

    public RemoteViewsWrapper(RemoteViews target) throws NoSuchFieldException, IllegalAccessException, InvalidObjectException {
        Object mActions = target.getClass().getDeclaredField("mActions").get(target);
        Object[] array = (Object[])mActions.getClass().getDeclaredField("array").get(mActions);

        List<ActionWrapper> list = new ArrayList<ActionWrapper>(array.length);
        for (Object action : array) {
            ActionWrapper wrapper = ActionWrapper.wrap(action);
            list.add(wrapper);
        }

        this.actionWrappers = list;
    }

    public Iterable<ActionWrapper> getActionWrappers() {
        return this.actionWrappers;
    }
}
