package org.clt;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by clt on 10/26/16.
 * 
 */

class MusicLibraryRootDirProcessingException extends Mp3CpOException {

    MusicLibraryRootDirProcessingException(final Path musicLibraryRoot, final IOException ex) {
        super("Error processing music library in %s.", ex, musicLibraryRoot);
    }
}
