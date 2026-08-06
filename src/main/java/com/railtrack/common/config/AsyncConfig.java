package com.railtrack.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Enables Spring's @Async support and provides a small, dedicated thread
 * pool for background tasks (currently: OTP email delivery).
 *
 * Why this exists: sending the OTP email over SMTP was previously done
 * synchronously inside the HTTP request thread for /register/send-otp and
 * /password/forgot. If Gmail's SMTP is ever slow (common on shared/free
 * hosting egress), that blocks the whole request and the frontend times
 * out even though the OTP itself was already generated and saved.
 * Running it on "otpMailExecutor" lets the controller respond immediately
 * after the OTP is persisted, while the email goes out in the background.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean(name = "otpMailExecutor")
    public Executor otpMailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("otp-mail-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.initialize();
        return executor;
    }

    /**
     * Default executor for any other @Async methods that don't name a
     * specific bean (falls back to this instead of Spring's SimpleAsyncTaskExecutor,
     * which spins up an unbounded number of threads).
     */
    @Override
    public Executor getAsyncExecutor() {
        return otpMailExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.error("Unhandled exception in async method '{}': {}",
                        method.getName(), ex.getMessage(), ex);
            }
        };
    }
}