import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageDownloader {

    public static void main(String[] args) {
        Path currentDirectory = Paths.get("").toAbsolutePath();
        Pattern jpgPattern = Pattern.compile("https?://\\S+?\\.(jpg|jpeg|png|gif|webp|bmp)", Pattern.CASE_INSENSITIVE);

        File[] files = currentDirectory.toFile().listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                List<URI> uris = new ArrayList<>();

                try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = jpgPattern.matcher(line);
                        while (matcher.find()) {
                            uris.add(URI.create(matcher.group()));
                        }
                    }
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                if (!uris.isEmpty()) {
                    try {
                        Path targetPath = Files.createDirectories(Paths.get(file.getName().replaceFirst("\\.txt$", "")));

                        for (URI uri : uris) {
                            try (InputStream in = uri.toURL().openStream()) {
                                String fileName = Paths.get(uri.getPath()).getFileName().toString();
                                Path imagePath = targetPath.resolve(fileName);
                                Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);

                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                }
            }
        }

    }
}
