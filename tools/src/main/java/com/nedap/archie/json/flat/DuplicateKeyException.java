package com.nedap.archie.json.flat;

/**
 * Exception to indicate a key in the flat json has been added twice, and that this RM Object can thus not be
 * properly converted to flat json. This should not happen with proper input.
 */
public class DuplicateKeyException extends Exception {

    public DuplicateKeyException(String message) {
        super(message);
    }
}
