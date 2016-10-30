package org.clt;

import java.nio.file.Path;

/**
 * Created by clt on 10/26/16.
 *
 */
class DirectoryProcessingException extends Mp3CpOException {

    DirectoryProcessingException(final Path artistDir, final String dirType, final Exception e) {
        super("Failed to process %s directory %s.", e, dirType, artistDir);
    }
}
