package com.nedap.archie.text;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.lang.reflect.InvocationTargetException;

public class ReflectionRmSerializer {

    private final ArchieAOMInfoLookup modelLookup;

    public ReflectionRmSerializer() {
        this.modelLookup = ArchieAOMInfoLookup.getInstance();
    }
    public void serialize(RMObject object, RmToTextSerializer serializer) {
        if(object == null) {
            return;
        }
        RMTypeInfo typeInfo = modelLookup.getTypeInfo(object.getClass());
        if(typeInfo == null) {
            serializer.append(object.toString());
            return;
        }
        for(RMAttributeInfo attribute: typeInfo.getAttributes().values()) {
            serializer.append(attribute.getRmName());
            serializer.append(": ");
            try {
                Object result = attribute.getGetMethod().invoke(object);
                if(result == null) {
                    serializer.append(" - ");
                }
                if(result instanceof RMObject) {
                    serializer.writeToText((RMObject) result);
                } else {
                    serializer.append(result.toString());
                }
            } catch (IllegalAccessException e) {
                //...
            } catch (InvocationTargetException e) {
                //...
            }
            serializer.append("\n");

        }
    }
}
