package com.example.springboot_demo.common.utils;

public interface Ilock {
    boolean tryLock (long timeoutSec);

    void unlock();
}
