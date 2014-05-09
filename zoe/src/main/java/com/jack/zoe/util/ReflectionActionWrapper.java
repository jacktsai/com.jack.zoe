package com.jack.zoe.util;

public class ReflectionActionWrapper extends ActionWrapper {

    static Class<?> classReflectionAction;

    static {
        try {
            classReflectionAction = Class.forName("android.widget.RemoteViews$ReflectionAction");
        } catch (ClassNotFoundException ignored) {
        }
    }

    protected ReflectionActionWrapper(Object action) {
        super(action);
    }

    public String getMethodName() throws NoSuchFieldException, IllegalAccessException {
        return (String)classReflectionAction.getDeclaredField("methodName").get(super.action);
    }

    public Object getValue() throws NoSuchFieldException, IllegalAccessException {
        return classReflectionAction.getDeclaredField("value").get(super.action);
    }
}
