package com.darcytech.demo.common;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;

public class PropertiesBinder {

    private final Binder binder;

    public PropertiesBinder(ApplicationContext applicationContext) {
        binder = Binder.get(applicationContext.getEnvironment());
    }

    public static <T> T bind(ApplicationContext context, T object, String... configPrefixes) {
        return new PropertiesBinder(context).bind(object, configPrefixes);
    }

    public <T> T bind(T object, String... configPrefixes) {
        Bindable<T> target = Bindable.ofInstance(object);
        for (String prefix : configPrefixes) {
            binder.bind(prefix, target);
        }
        return object;
    }

}
