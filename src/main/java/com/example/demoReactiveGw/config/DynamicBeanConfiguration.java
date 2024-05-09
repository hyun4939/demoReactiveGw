package com.example.demoReactiveGw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicBeanConfiguration implements BeanPostProcessor {

    private final ConfigurableBeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (!(bean instanceof ExampleBean)) {
            return bean;
        }

        ((ExampleBean) bean).getList().forEach(propertyData -> {
            DynamicBean dynamicBean = new DynamicBean();
            dynamicBean.setName(propertyData.getName());
            dynamicBean.setValue(propertyData.getValue());

            if (InitializingBean.class.isAssignableFrom(dynamicBean.getClass())) {
                try {
                    ((InitializingBean) dynamicBean).afterPropertiesSet();
                } catch (Exception e) {
                    log.error("afterPropertiesSet error", e);
                }
            }

            beanFactory.registerSingleton(propertyData.getName(), dynamicBean);
        });


        Object x = beanFactory.getBean("xbean");
        Object y = beanFactory.getBean("ybean");
        Object z = beanFactory.getBean("zbean");

        log.info("ttttt=x=>>"+((DynamicBean) x).getId());
        log.info("ttttt=y=>>"+((DynamicBean) y).getId());
        log.info("ttttt=z=>>"+((DynamicBean) z).getId());

        return bean;
    }
}