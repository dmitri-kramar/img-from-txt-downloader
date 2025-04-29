import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple CLI utility for scanning text files in a directory,
 * extracting image links, and downloading the images into named folders.
 */
public class ImageDownloader {

    private static final Pattern FILE_NAME = Pattern
            .compile(".*\\.(txt|csv|log|rtf)", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMAGE_LINK = Pattern
            .compile("https?://\\S+?\\.(jpg|jpeg|png|gif|webp|bmp)", Pattern.CASE_INSENSITIVE);

    /**
     * Runs the main application workflow: validates directory,
     * processes text files, downloads images, and prints a summary.
     *
     * @param currentDirectory the directory to scan for text files
     * @throws IOException if an I/O error occurs
     */
    private static void run(Path currentDirectory) throws IOException {
        validateDirectory(currentDirectory);
        List<File> files = getFiles(currentDirectory);
        validateFiles(files, currentDirectory);

        int downloadedImages = 0;
        for (File file : files) {
            downloadedImages += downloadImagesFromFile(file);
        }

        printSummary(files.size(), downloadedImages);
    }

    /**
     * Validates that the given path exists and is a directory.
     *
     * @param directory the path to validate
     * @throws IOException if the path does not exist or is not a directory
     */
    private static void validateDirectory(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IOException("invalid directory: " + directory);
        }
    }

    /**
     * Checks if the list of files is empty and exits the program if so.
     *
     * @param files the list of text files
     * @param directory the directory where files were searched
     */
    private static void validateFiles(List<File> files, Path directory) {
        if (files.isEmpty()) {
            System.err.println("No eligible files found in " + directory);
            System.exit(0);
        }
    }

    /**
     * Finds eligible text files in the given directory.
     *
     * @param path the directory to search
     * @return a list of eligible files, or an empty list if none found
     */
    private static List<File> getFiles(Path path) {
        File[] files = path.toFile().listFiles((dir, name) -> FILE_NAME.matcher(name).matches());
        return files == null ? List.of() : Arrays.asList(files);
    }

    /**
     * Processes a text file: extracts image URIs and downloads the images.
     *
     * @param file the text file to process
     * @return the number of successfully downloaded images
     * @throws IOException if an I/O error occurs while reading the file
     */
    private static int downloadImagesFromFile(File file) throws IOException {
        List<URI> uris = extractURIs(file);
        if (uris.isEmpty()) return 0;

        String baseName = file.getName().replaceFirst("\\.[^.]+$", "");
        Path targetPath = Files.createDirectories(Paths.get(baseName));

        int successfulDownloads = 0;
        for (URI uri : uris) {
            try {
                downloadImage(uri, targetPath);
                successfulDownloads++;
            } catch (IOException e) {
                System.err.println("Failed to download " + uri);
            }
        }

        return successfulDownloads;
    }

    /**
     * Extracts image URIs from a given text file using a regex pattern.
     *
     * @param file the text file to scan
     * @return a list of extracted image URIs
     * @throws IOException if an I/O error occurs while reading the file
     */
    private static List<URI> extractURIs(File file) throws IOException {
        List<URI> uris = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = IMAGE_LINK.matcher(line);
                while (matcher.find()) {
                    uris.add(URI.create(matcher.group()));
                }
            }
        }

        return uris;
    }

    /**
     * Downloads an image from a given URI into the specified target directory.
     *
     * @param uri the URI of the image
     * @param targetPath the directory to save the image to
     * @throws IOException if an I/O error occurs during downloading or saving
     */
    private static void downloadImage(URI uri, Path targetPath) throws IOException {
        try (InputStream in = uri.toURL().openStream()) {
            String fileName = Paths.get(uri.getPath()).getFileName().toString();
            Path imagePath = targetPath.resolve(fileName);
            Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(imagePath);
        }
    }

    /**
     * Prints a summary of the number of processed files and downloaded images.
     *
     * @param filesCount the number of processed text files
     * @param imagesCount the number of downloaded images
     */
    private static void printSummary(int filesCount, int imagesCount) {
        System.out.printf("%nProcessed %d files, downloaded %d images%n", filesCount, imagesCount);
    }

    /**
     * The application entry point.
     *
     * @param args optional argument specifying the target directory
     */
    public static void main(String[] args) {
        try {
            System.out.println();
            Path targetDirectory = args.length > 0
                    ? Paths.get(args[0]).toAbsolutePath()
                    : Paths.get("").toAbsolutePath();
            run(targetDirectory);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
