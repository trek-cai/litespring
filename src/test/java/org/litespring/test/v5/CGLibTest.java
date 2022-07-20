package org.litespring.test.v5;

import org.junit.Test;
import org.litespring.service.v5.PetStoreService;
import org.litespring.tx.TransactionManager;
import org.springframework.cglib.proxy.*;

import java.lang.reflect.Method;

public class CGLibTest {

    @Test
    public void testCallback() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        enhancer.setCallback(new TransactionInterceptor());
        PetStoreService petStore = (PetStoreService) enhancer.create();
        petStore.placeOrder();
//        System.out.println(petStore); // 调用任意方法都会去执行Interceptor的逻辑，可用CallbackFilter对需要执行的方法进行过滤
    }

    public static class TransactionInterceptor implements MethodInterceptor {
        TransactionManager tx = new TransactionManager();

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            tx.start();
            Object result = methodProxy.invokeSuper(o, objects);
            tx.commit();
            return result;
        }
    }

    @Test
    public void testFilter() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        enhancer.setInterceptDuringConstruction(false);

        Callback[] callbacks = new Callback[] {new TransactionInterceptor(), NoOp.INSTANCE};
        Class<?>[] callbackTypes = new Class<?>[callbacks.length];

        for (int i = 0; i< callbacks.length; i++) {
            callbackTypes[i] = callbacks[i].getClass();
        }

        enhancer.setCallbackFilter(new ProxyCallbackFilter());
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackTypes(callbackTypes);

        PetStoreService petStore = (PetStoreService) enhancer.create();
        petStore.placeOrder();
        System.out.println(petStore);
    }

    private static class ProxyCallbackFilter implements CallbackFilter {

        @Override
        public int accept(Method method) {
            if (method.getName().contains("place")) {
                return 0;
            }
            return 1;
        }
    }
}
