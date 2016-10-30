package org.clt;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by clt on 10/29/16.
 * 
 */
class FailedToCopySongException extends Mp3CpOException {

    FailedToCopySongException(Path song, IOException e) {
        super("Failed to copy %s.", e, song);
    }

    FailedToCopySongException(String album, IOException e) {
        super("Failed to copy album %s.", e, album);
    }
}
