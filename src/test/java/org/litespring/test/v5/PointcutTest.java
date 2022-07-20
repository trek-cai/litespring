package org.litespring.test.v5;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.aop.MethodMatcher;
import org.litespring.aop.aspectj.AspectJExpressionPointcut;
import org.litespring.service.v5.PetStoreService;

import java.lang.reflect.Method;

/**
 * 抽象出Pointcut用于表示切入点，通过MethodMatcher的matches方法来判断业务方法是否符合切入点匹配规则，AspectJExpressionPointcut为其具体实现。
 */
public class PointcutTest {

    @Test
    public void testPointcut() throws Exception {
        String expression = "execution(* org.litespring.service.v5.*.placeOrder(..))";
        AspectJExpressionPointcut ajp = new AspectJExpressionPointcut();
        ajp.setExpression(expression);

        MethodMatcher mm = ajp.getMethodMatcher();

        {
            Class<?> targetClass = PetStoreService.class;

            Method method1 = targetClass.getMethod("placeOrder");
            Method method2 = targetClass.getMethod("getAccountDao");
            Assert.assertTrue(mm.matches(method1));
            Assert.assertFalse(mm.matches(method2));
        }

        {
            Class<?> targetClass = org.litespring.service.v4.PetStoreService.class;
            Method method1 = targetClass.getMethod("getAccountDao");
            Assert.assertFalse(mm.matches(method1));
        }
    }
}
