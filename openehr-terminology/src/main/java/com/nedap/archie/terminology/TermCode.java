package com.nedap.archie.terminology;

public interface TermCode {

    /** get the id of the terminology of this code*/
    String getTerminologyId();
    /** get the language of the description of this code */
    String getLanguage();
    /** get the code string of this code, the unique machine readable code that is independent from the language */
    String getCodeString();
    /** get the language specific description of this code */
    String getDescription();
    /** Get the language specific OpenEHR Group name. Often null */
    String getGroupName();
    /** get the id of the openEHR group, which is independent of the language as its the English group name. often null */
    String getGroupId();
}
