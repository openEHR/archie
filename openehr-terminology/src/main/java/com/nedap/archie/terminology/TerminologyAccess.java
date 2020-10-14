package com.nedap.archie.terminology;

import java.util.List;

public interface TerminologyAccess {

    /** Get a single term by its external Terminology Id, for example "ISO_639-1", "nl" for the given language*/
    TermCode getTerm(String terminologyId, String code, String language);
    /** Get all terms by its external Terminology Id, for example ISO_639-1*/
    List<TermCode> getTerms(String terminologyId, String language);

    /** Get all terms by its terminology URI, for example http://openehr.org/id/124, for the given language */
    TermCode getTermByTerminologyURI(String uri, String language);

    /** Get all terms by its openEHR id, for example "countries", "GB" for the given language */
    TermCode getTermByOpenEhrId(String terminologyId, String code, String language);
    /** Get all terms by its openEHR id, for example "countries", for the given language */
    List<TermCode> getTermsByOpenEhrId(String terminologyId, String language);


    /** Get a list of all term codes for a given OpenEHR group */
    List<TermCode> getTermsByOpenEHRGroup(String groupId, String language);

}
