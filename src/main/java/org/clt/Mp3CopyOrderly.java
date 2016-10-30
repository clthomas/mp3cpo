package org.clt;

import com.mpatric.mp3agic.*;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by clt on 10/24/16.
 */
public class Mp3CopyOrderly {

    protected static final Set<String> GENRES_TO_GROUP = new HashSet<>(Arrays.asList("gospel", "christmas"));


    private Mp3CopyOrderly() {}


    public static void main(final String[] args) throws ParseException {

        Path musicLibraryRoot = Paths.get(args[0]);
        Path destinationDir = Paths.get(args[1]);

        Map<String, SortedSet<Path>> fileMap = buildAlbumToPathMap(musicLibraryRoot);

        copyLibrary(fileMap, destinationDir);

    }

    private static Map<String, SortedSet<Path>> buildAlbumToPathMap(Path musicLibraryRoot) {
        Map<String, SortedSet<Path>> albumToPathMap = new HashMap<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(musicLibraryRoot)) {
            for (Path artistDir : directoryStream) {
                try {
                    if (Files.isDirectory(artistDir)) {
                        albumToPathMap.putAll(getAllSongsByArtist(artistDir));
                    }
                } catch (Exception e) {
                    throw new DirectoryProcessingException(artistDir, "artist", e);
                }
            }
        } catch (IOException ex) {
            throw new DirectoryProcessingException(musicLibraryRoot, "library root", ex);
        }
        return albumToPathMap;
    }



    private static Map<String, SortedSet<Path>> getAllSongsByArtist(final Path artistDir) {

        Map<String, SortedSet<Path>> albumToPathMap = new HashMap<>();
        String artistName = artistDir.toFile().getName();


        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(artistDir)) {
            for (Path albumDir : directoryStream) {
                try {
                    if (Files.isDirectory(albumDir)) {
                        String albumName = albumDir.toFile().getName();
                        SortedSet<Path> allSongsInAlbum = getAllSongsInAlbum(albumDir);
                        if (allSongsInAlbum.size() > 0) {
                            Path firstSong = allSongsInAlbum.first();
                            if (firstSong != null) {
                                String albumDirectoryDestinationName = determineDestinationDirectoryName(artistName, albumName, firstSong);

                                if (!albumDirectoryDestinationName.isEmpty()) {
                                    albumToPathMap.put(albumDirectoryDestinationName, allSongsInAlbum);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new DirectoryProcessingException(albumDir, "album", e);
                }
            }
        } catch (IOException ex) {
            throw new DirectoryProcessingException(artistDir, "artist", ex);
        }
        return albumToPathMap;
    }

    private static String determineDestinationDirectoryName(String artistName, String albumName, Path firstSong) {
        String genre = readGenre(firstSong.toFile());
        String albumDirectoryDestinationName = "";
        try {
            if (genre != null && !genre.isEmpty() && GENRES_TO_GROUP.contains(genre.toLowerCase())) {
                albumDirectoryDestinationName = String.format("%s_%s_%s", genre, artistName, albumName);
            } else {
                albumDirectoryDestinationName = String.format("%s_%s", artistName, albumName);
            }
            if (albumDirectoryDestinationName.isEmpty()) {
                System.err.println(String.format("Could not determine destination name for %s by %s.", albumName, artistName));
            }

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        return albumDirectoryDestinationName;
    }

    private static String readGenre(File song) {
        try {
            Mp3File mp3File = new Mp3File(song);
            if (mp3File.hasId3v2Tag())
            {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                return id3v2Tag.getGenreDescription();
            }
            else if (mp3File.hasId3v1Tag())
            {
                ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                return id3v1Tag.getGenreDescription();
            }
            else
            {
                return "";
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new ID3TagReadingException(song, e);
        }
    }

    private static SortedSet<Path> getAllSongsInAlbum(Path albumDir) {
        SortedSet<Path> songPaths = new TreeSet<>();
        try (Stream<Path> pathStream = Files.list(albumDir)) {
            pathStream.filter(fileName -> fileName.toString().endsWith(".mp3"))
                    .forEach(song -> {
                try {
                    songPaths.add(song);
                } catch (Exception e) {
                    throw new FileProcessingException(song, e);
                }
            });

        } catch (Exception ex) {
            throw new DirectoryProcessingException(albumDir, "album", ex);
        }
        return songPaths;
    }


    private static void copyLibrary(Map<String, SortedSet<Path>> musicLibraryAlbumMap, Path destinationRoot) {
        SortedSet<String> albums = new TreeSet<>();
        albums.addAll(musicLibraryAlbumMap.keySet());
        for (String album : albums)
        {
            try {
                Path albumDir = destinationRoot.resolve(album);
                Files.createDirectory(albumDir);
                copyAlbum(musicLibraryAlbumMap.get(album), albumDir);
            } catch (IOException e) {
                throw new FailedToCopySongException(album, e);
            }

        }

    }

    private static void copyAlbum(SortedSet<Path> songs, Path destinationDir) {
        System.out.println(destinationDir);
        for (Path song : songs)
        {
            try {
                Files.copy(song, destinationDir.resolve(song.getFileName()));
            } catch (IOException e) {
                throw new FailedToCopySongException(song, e);
            }

        }
    }

    private static Options buildOptions() {
        return null;
    }


}
