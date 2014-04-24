package com.opentable.lifecycle;

import com.google.common.base.Throwables;

/**
 * A class like {@code Runnable} but can throw an exception.
 */
@FunctionalInterface
public interface ExceptionRunnable
{
    void run() throws Exception;

    /**
     * Run with any checked exceptions rethrown as unchecked.
     */
    default void runSafely()
    {
        try {
            run();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
