/*@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(value= ArchetypeAdapter.class, type=AuthoredArchetype.class),
        @XmlJavaTypeAdapter(value= OPTAdapter.class, type=OperationalTemplate.class),
        @XmlJavaTypeAdapter(value= TemplateAdapter.class, type=Template.class),
})*/
package com.nedap.archie.aom;

import com.nedap.archie.xml.adapters.ArchetypeAdapter;
import com.nedap.archie.xml.adapters.OPTAdapter;
import com.nedap.archie.xml.adapters.TemplateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;