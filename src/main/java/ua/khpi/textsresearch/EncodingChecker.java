package ua.khpi.textsresearch;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
* Change DIRECTORY_WITH_TEXTS constant to use this application
* */
public class EncodingChecker {

    public static final String TXT_EXTENSION = ".txt";
    public static final String DIRECTORY_WITH_TEXTS = "/path/to/your/texts";
    public static final String REPORT_FILE_NAME = "EncodingCheckerReport";
    public static final String REPORT_PATH = DIRECTORY_WITH_TEXTS + REPORT_FILE_NAME + TXT_EXTENSION;

    private static final StringBuilder reportText = new StringBuilder();

    public static void main(String[] args) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(DIRECTORY_WITH_TEXTS))) {
            List<Path> filesFound = walk
                    .filter(EncodingChecker::isTxtFile)
                    .filter(EncodingChecker::isNotReport)
                    .collect(Collectors.toList());

            reportText.append("Found ")
                    .append(filesFound.size())
                    .append(" files for processing\n");

            filesFound.forEach(EncodingChecker::checkEncoding);
        } catch (IOException e) {
            reportText.append(Arrays.toString(e.getStackTrace()));
        } finally {
            reportText.append("Finished processing");
            Files.write(Paths.get(REPORT_PATH), reportText.toString().getBytes());
        }
    }

    private static void checkEncoding(Path pathToTxtFile) {
        try {
            reportText.append("processing ")
                    .append(pathToTxtFile.toString())
                    .append(" file\n");

            CharsetMatch charSet = detectCharset(pathToTxtFile);

            reportText.append("this file have ")
                    .append(charSet.getName())
                    .append(" encoding ")
                    .append(" with confidence ")
                    .append(charSet.getConfidence())
                    .append("\n");

            List<String> fileContent = Files.readAllLines(pathToTxtFile, Charset.forName(charSet.getName()));
            Files.write(pathToTxtFile, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            reportText.append(Arrays.toString(e.getStackTrace()));
        }
    }

    private static CharsetMatch detectCharset(Path pathToTxtFile) throws IOException {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(new BufferedInputStream(Files.newInputStream(pathToTxtFile)));
        return detector.detect();
    }

    private static boolean isTxtFile(Path pathToFile) {
        return pathToFile.getFileName().toString().endsWith(TXT_EXTENSION);
    }

    private static boolean isNotReport(Path pathToFile) {
        return !pathToFile.getFileName().toString().equals(REPORT_FILE_NAME + TXT_EXTENSION);
    }
}
