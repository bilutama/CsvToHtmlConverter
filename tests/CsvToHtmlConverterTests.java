import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvToHtmlConverterTests {
    final static String inputPath = "./tests/inputCsv/";
    final static String outputPath = "./tests/outputHtml/";

    final static String inputCsv = inputPath + "input.csv";
    final static String outputHtml = outputPath + "output.html";
    final static String inputEmptyCsv = inputPath + "inputEmpty.csv";
    final static String outputEmptyHtml = outputPath + "outputEmpty.html";
    final static String inputCsvWithSpecialChars = inputPath + "inputWithSpecialChars.csv";
    final static String outputHtmlWithSpecialChars = outputPath + "outputWithSpecialChars.html";

    final static boolean RUN_CLEANUP_FOR_OUTPUT_HTMLS = false;

    @Test
    void convertCsvToHtmlTableWithEmptyInput() throws IOException {
        // Given
        String lineSeparator = System.lineSeparator();
        String expectedOutput = getHtmlHeader(inputEmptyCsv, lineSeparator) +
                "<table>" + lineSeparator +
                "</table>" + lineSeparator +
                "</body>" + lineSeparator + "</html>";

        // Perform
        CsvToHtmlConverter.convertCsvToHtmlTable(inputEmptyCsv, outputEmptyHtml);

        // Assert
        String actualOutput = new String(Files.readAllBytes(Paths.get(outputEmptyHtml)));
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void convertCsvToHtmlTableWithValidInput() throws IOException {
        // Given
        String lineSeparator = System.lineSeparator();
        String expectedOutput = getHtmlHeader(inputCsv, lineSeparator) +
                "<table>" + lineSeparator +
                "<tr>" + lineSeparator +
                "<td>1</td>" + lineSeparator +
                "<td>2</td>" + lineSeparator +
                "</tr>" + lineSeparator +
                "<tr>" + lineSeparator +
                "<td>3</td>" + lineSeparator +
                "<td>4</td>" + lineSeparator +
                "</tr>" + lineSeparator +
                "</table>" + lineSeparator +
                "</body>" + lineSeparator + "</html>";

        // Perform
        CsvToHtmlConverter.convertCsvToHtmlTable(inputCsv, outputHtml);

        // Assert
        String actualOutput = new String(Files.readAllBytes(Paths.get(outputHtml)));
        assertEquals(expectedOutput, actualOutput);
    }


    @Test
    void convertCsvToHtmlTableWithSpecialCharacters() throws IOException {
        // Given
        String lineSeparator = System.lineSeparator();
        String expectedOutput = getHtmlHeader(inputCsvWithSpecialChars, lineSeparator) +
                "<table>" + lineSeparator + "<tr>" + lineSeparator +
                "<td>&amp;</td>" + lineSeparator +
                "<td>&lt;</td>" + lineSeparator +
                "<td>&gt;</td>" + lineSeparator +
                "</tr>" + lineSeparator +
                "</table>" + lineSeparator +
                "</body>" + lineSeparator + "</html>";

        // Perform
        CsvToHtmlConverter.convertCsvToHtmlTable(inputCsvWithSpecialChars, outputHtmlWithSpecialChars);

        // Assert
        String actualOutput = new String(Files.readAllBytes(Paths.get(outputHtmlWithSpecialChars)));
        assertEquals(expectedOutput, actualOutput);
    }

    @AfterAll
    static void cleanup() {
        // Delete the files or perform any other cleanup actions
        if (RUN_CLEANUP_FOR_OUTPUT_HTMLS) {
            File inputFile = new File(outputHtml);
            File outputFile = new File(outputHtmlWithSpecialChars);

            if (inputFile.exists()) {
                inputFile.delete();
            }

            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    String getHtmlHeader(String inputFileName, String lineSeparator) {
        return "<!DOCTYPE html>" + lineSeparator +
                "<html>" + lineSeparator +
                "<head>" + lineSeparator +
                "<meta charset=\"utf-8\">" + lineSeparator +
                "<title>Generated from " + inputFileName + "</title>" + lineSeparator +
                "</head>" + lineSeparator +
                "<body>" + lineSeparator;
    }
}
