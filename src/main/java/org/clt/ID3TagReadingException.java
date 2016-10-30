package org.clt;

import java.io.File;

/**
 * Created by clt on 10/29/16.
 */
class ID3TagReadingException extends Mp3CpOException {

    ID3TagReadingException(File song, Exception e) {
        super("Failed to read info from %s.", e, song);
    }
}
