package ua.khpi.textsresearch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
* Change TEXTS_SOURCE_DIR to use this application
* */
public class TextMover {
    public static final String TXT_EXTENSION = ".txt";
    public static final String TEXTS_SOURCE_DIR = "/path/to/texts/that/should/be/moved";

    public static void main(String[] args) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(TEXTS_SOURCE_DIR))) {
            List<Path> filesFound = walk
                    .filter(TextMover::isTxtFile)
                    .collect(Collectors.toList());

            Collections.shuffle(filesFound);

            //change this parameter if needed
            int textsPerPerson = 50;

            Map<Integer, List<Path>> splittedLists = splitIntoSmallerLists(filesFound, textsPerPerson);

            if (filesFound.size() == splittedLists.values().stream().mapToLong(Collection::size).sum()) {
                int i = 1;
                for (List<Path> list : splittedLists.values()) {
                    moveFilesFromListWithIndex(list, i++);
                }
            }
        }
    }

    private static void moveFilesFromListWithIndex(List<Path> list, int i) throws IOException {
        String dirPath = TEXTS_SOURCE_DIR + File.separator + "sorted" + File.separator + i;
        File newDir = new File(dirPath);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        for (Path path : list) {
            List<String> fileContent = Files.readAllLines(path);
            Files.write(Path.of(dirPath, path.getFileName().toString()), fileContent, StandardCharsets.UTF_8);
        }
    }

    private static Map<Integer, List<Path>> splitIntoSmallerLists(List<Path> filesFound, int textsPerPerson) {
        final AtomicInteger counter = new AtomicInteger(0);
        return filesFound.stream().collect(Collectors.groupingBy(s -> counter.getAndIncrement() / textsPerPerson));
    }

    private static boolean isTxtFile(Path pathToFile) {
        return pathToFile.getFileName().toString().endsWith(TXT_EXTENSION);
    }
}
