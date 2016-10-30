package org.clt;

import java.nio.file.Path;

/**
 * Created by clt on 10/26/16.
 */
class FileProcessingException extends Mp3CpOException {


    FileProcessingException(Path song, Exception e) {
        super("Failed to proccess song in file %s.", e, song);
    }
}
