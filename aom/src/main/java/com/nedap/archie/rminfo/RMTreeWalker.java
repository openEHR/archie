package com.nedap.archie.rminfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 *
 */
public class RMTreeWalker {

    private final ModelInfoLookup lookup;

    public RMTreeWalker(ModelInfoLookup lookup) {
        this.lookup = lookup;
    }

    public void walk(Object rmObject, RMListener listener){
        if(rmObject == null) {
            return;
        }
        listener.enterObject(rmObject);

        RMTypeInfo typeInfo = lookup.getTypeInfo(rmObject.getClass());
        if(typeInfo != null) {
            for(RMAttributeInfo attributeInfo:typeInfo.getAttributes().values()) {
                if(attributeInfo.isComputed()) {
                    continue;
                }
                Method getMethod = attributeInfo.getGetMethod();
                if(getMethod != null) {
                    Object result = null;
                    try {
                        result = getMethod.invoke(rmObject);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    if(result != null) {
                        if(result instanceof Collection) {
                            for(Object c: (Collection) result) {
                                walk(c, listener);
                            }
                        } else {
                            walk(result, listener);
                        }
                    }
                }
            }
        }
        listener.exitObject(rmObject);

    }
}
