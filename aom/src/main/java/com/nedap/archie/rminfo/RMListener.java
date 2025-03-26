package com.nedap.archie.rminfo;

/**
 * A very generic Listener class to walk the tree of RM Objects.
 * Can be used directly, to implement very generic RM listeners, or can be used to create a more specific
 * listener class for a specific RM
 */
public interface RMListener {

    void enterObject(Object object);

    void exitObject(Object object);

}
