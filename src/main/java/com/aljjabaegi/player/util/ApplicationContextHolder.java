package com.aljjabaegi.player.util;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * autowired annotation, this.CTX.getBean을 대신할 수 있는 클래스
 * UserService userService = ApplicationContextHolder.getContext().getBean(UserService.class);
 *
 * @author GEON LEE
 * @version 1.0.0
 * @apiNote
 * @since 2018-06-04
 **/
@Configuration
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}