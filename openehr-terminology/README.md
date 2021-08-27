## OpenEHR Terminology access

A simple implementation of the OpenEHR Terminology server.

## Javascript instead of JAXB

This implementation by default uses a Jackson terminology file instead of directly using the XML ones. This is to ensure
that it works on Android, where JAXB is not available. To regenerate the JSON file, for example if the terminology file has
changed:

 - run `OpenEHRTerminologyAccessTest.writeToJson()`
 - copy/paste the output in `src/main/resources/openEHR_RM/fullTermFile.json`