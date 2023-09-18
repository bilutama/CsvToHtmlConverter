import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvToHtmlConverterTests {
    @Test
    void convertCsvToHtmlTableWithValidInput() throws IOException {
        // Arrange
        String lineSeparator = System.lineSeparator();
        String inputFileName = "./tests/inputTestCsvFiles/test.csv";
        String outputFileName = "./tests/outputTestHtml/output_test.html";
        String expectedOutput = getHtmlHeader(inputFileName, lineSeparator) +
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

        // Act
        CsvToHtmlConverter.convertCsvToHtmlTable(inputFileName, outputFileName);

        // Assert
        String actualOutput = new String(Files.readAllBytes(Paths.get(outputFileName)));
        assertEquals(expectedOutput, actualOutput);
    }


    @Test
    void convertCsvToHtmlTableWithSpecialCharacters() throws IOException {
        // Arrange
        String lineSeparator = System.lineSeparator();
        String inputFileName = "./tests/inputTestCsvFiles/test-special-characters.csv";
        String outputFileName = "./tests/outputTestHtml/output_test-special-characters.html";
        String expectedOutput = getHtmlHeader(inputFileName, lineSeparator) +
                "<table>" + lineSeparator + "<tr>" + lineSeparator +
                "<td>&amp;</td>" + lineSeparator +
                "<td>&lt;</td>" + lineSeparator +
                "<td>&gt;</td>" + lineSeparator +
                "</tr>" + lineSeparator +
                "</table>" + lineSeparator +
                "</body>" + lineSeparator + "</html>";

        // Act
        CsvToHtmlConverter.convertCsvToHtmlTable(inputFileName, outputFileName);

        // Assert
        String actualOutput = new String(Files.readAllBytes(Paths.get(outputFileName)));
        assertEquals(expectedOutput, actualOutput);
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
