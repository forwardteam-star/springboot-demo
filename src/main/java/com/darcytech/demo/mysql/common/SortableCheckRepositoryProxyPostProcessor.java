package com.darcytech.demo.mysql.common;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.stereotype.Component;

import com.darcytech.demo.common.Sortable;

@Component
public class SortableCheckRepositoryProxyPostProcessor implements RepositoryProxyPostProcessor {

    private final SortableMethodInterceptor interceptor = new SortableMethodInterceptor();

    private Map<Method, List<String[]>> sortableFields = new ConcurrentHashMap<>();

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        if (repositoryInformation.getQueryMethods().stream().anyMatch(it->getAnnotation(it) != null)) {
            factory.addAdvice(interceptor);
        }
    }

    @Nullable
    private Sortable getAnnotation(Method method) {
        return method.getDeclaredAnnotation(Sortable.class);
    }

    class SortableMethodInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return null;
        }
    }

}
