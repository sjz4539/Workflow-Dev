package com.workflow.core.controller;

/**
 * Slightly less simple handler. Takes a single argument of an arbitrary type.
 * @author Steven Zuchowski
 *
 * @param <T>
 */
public interface ArgumentHandler<T> {

    void handle(T arg);

}
