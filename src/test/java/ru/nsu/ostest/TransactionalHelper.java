package ru.nsu.ostest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Component
public class TransactionalHelper {
    @Transactional
    public void runInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    public <T> T getInTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
