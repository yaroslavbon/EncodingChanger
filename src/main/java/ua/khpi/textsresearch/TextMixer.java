package ua.khpi.textsresearch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Change SOURCE_TEXTS_DIR to use this application
 * */
public class TextMixer {
    public static final String TXT_EXTENSION = ".txt";
    public static final String REPORT_FILE_NAME = "TextMixerReport.csv";
    public static final String SOURCE_TEXTS_DIR = "path/to/texts";
    public static final String PROCESSED_FILES_PATH = SOURCE_TEXTS_DIR + File.separator + "processed_texts";
    public static final String REPORT_PATH = SOURCE_TEXTS_DIR + File.separator + REPORT_FILE_NAME;

    private static StringBuilder reportText = new StringBuilder();

    public static void main(String[] args) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(SOURCE_TEXTS_DIR))) {
            List<Path> filesFound = walk
                    .filter(TextMixer::isTxtFile)
                    .collect(Collectors.toList());

            reportText.append("Found ")
                    .append(filesFound.size())
                    .append(" files for processing\n");

            if (!filesFound.isEmpty()){
                File newDir = new File(PROCESSED_FILES_PATH);
                if (!newDir.exists()) newDir.mkdir();

                filesFound.forEach(TextMixer::mixFile);
            }
        } catch (IOException e) {
            reportText.append(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        } finally {
            reportText.append("Finished processing");
            Files.write(Paths.get(REPORT_PATH), reportText.toString().getBytes());
        }
    }

    private static void mixFile(Path pathToTxtFile) {
        try {
            String uuid = UUID.randomUUID().toString();
            List<String> fileContent = Files.readAllLines(pathToTxtFile);
            Files.write(Path.of(PROCESSED_FILES_PATH, uuid + TXT_EXTENSION), fileContent, StandardCharsets.UTF_8);

            reportText.append(pathToTxtFile.getFileName())
                    .append(";")
                    .append(uuid)
                    .append("\n");
        } catch (IOException e) {
            reportText.append(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    private static boolean isTxtFile(Path pathToFile) {
        return pathToFile.getFileName().toString().endsWith(TXT_EXTENSION);
    }
}
