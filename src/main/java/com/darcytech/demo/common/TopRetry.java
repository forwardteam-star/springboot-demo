package com.darcytech.demo.common;

import org.springframework.retry.annotation.Retryable;

@Retryable(
        include = {DxRetryableException.class}
//        exclude = {
//                Error.class,
//                NullPointerException.class,
//                IndexOutOfBoundsException.class,
//                IllegalArgumentException.class,
//                IllegalStateException.class,
//                UnsupportedOperationException.class,
//                ClassCastException.class,
//                ArrayStoreException.class,
//                ArithmeticException.class,
//                NoSuchElementException.class
//        }
)
public @interface TopRetry {
}
