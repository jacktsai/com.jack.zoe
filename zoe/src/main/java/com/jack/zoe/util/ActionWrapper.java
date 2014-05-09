package com.jack.zoe.util;

import java.io.InvalidObjectException;

public class ActionWrapper {

    static Class<?> classAction;

    static {
        try {
            classAction = Class.forName("android.widget.RemoteViews$Action");
        } catch (ClassNotFoundException ignored) {
        }
    }

    static ActionWrapper wrap(Object action) throws InvalidObjectException {

        if (ReflectionActionWrapper.classReflectionAction.isAssignableFrom(action.getClass())) {
            return new ReflectionActionWrapper(action);
        } else if (ActionWrapper.classAction.isAssignableFrom(action.getClass())) {
            return new ActionWrapper(action);
        }

        throw new InvalidObjectException("action 的型別錯誤！");
    }

    protected final Object action;

    protected ActionWrapper(Object action) {
        this.action = action;
    }

    public String getClassName() {
        return this.action.getClass().getName();
    }

    public int getViewId() throws NoSuchFieldException, IllegalAccessException {
        return classAction.getDeclaredField("viewId").getInt(this.action);
    }
}
