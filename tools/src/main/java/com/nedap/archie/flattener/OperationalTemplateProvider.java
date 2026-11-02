package com.nedap.archie.flattener;

import com.nedap.archie.aom.OperationalTemplate;

/**
 * Interface to implement a custom OperationalTemplteProvider. Used for when OPTs are needed.
 */
public interface OperationalTemplateProvider {

    OperationalTemplate getOperationalTemplate(String archetypeId);
}
