package com.nedap.archie.aom;

import com.nedap.archie.xml.adapters.ArchetypeAdapter;
import com.nedap.archie.xml.adapters.OPTAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * See @Archetype for the fields you might expect here, along with an explanation
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="archetype")
public class AuthoredArchetype extends Archetype {


}
