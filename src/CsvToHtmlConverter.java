import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CsvToHtmlConverter {
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            printHelpMessage();
        }

        if (args.length != 2) {
            printWrongArgumentsMessage();
            return;
        }

        if (args[0].equals(args[1])) {
            System.out.printf("The output file must be different from the input file.%n");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        convertCsvToHtmlTable(inputFileName, outputFileName);
    }

    public static void convertCsvToHtmlTable(String inputFileName, String outputFileName) {
        try (Scanner scanner = new Scanner(new FileInputStream(inputFileName));
             PrintWriter writer = new PrintWriter(outputFileName)) {

            final char SEPARATOR = ',';
            final char QUOTES = '\"';
            final char END_OF_STRING = '\n';

            final String TABLE_OPEN_TAG = "<table>";
            final String TABLE_CLOSE_TAG = "</table>";
            final String ROW_OPEN_TAG = "<tr>";
            final String ROW_CLOSE_TAG = "</tr>";
            final String CELL_OPEN_TAG = "<td>";
            final String CELL_CLOSE_TAG = "</td>";
            final String BREAK_LINE_TAG = "<br/>";

            boolean separatorMode = true;
            boolean isNewCell = true;
            boolean isEscapeQuotes = false;

            writer.println("<!DOCTYPE html>" + System.lineSeparator() +
                    "<html>" + System.lineSeparator() +
                    "<head>" + System.lineSeparator() +
                    "<meta charset=\"utf-8\">");
            writer.println("<title>Generated from " + inputFileName + "</title>");
            writer.println("</head>" + System.lineSeparator() + "<body>");

            writer.println(TABLE_OPEN_TAG);
            String processedString;

            while (scanner.hasNextLine()) {
                processedString = scanner.nextLine();
                int currentCharIndex = 0;

                while (currentCharIndex < processedString.length()) {
                    char currentChar = processedString.charAt(currentCharIndex);
                    char nextChar = (currentCharIndex < processedString.length() - 1) ? processedString.charAt(currentCharIndex + 1) : END_OF_STRING;

                    if (currentCharIndex == 0 && isNewCell) {
                        writer.println(ROW_OPEN_TAG);

                        // Process if the first cell in a row is empty
                        if (currentChar == SEPARATOR) {
                            writer.print(CELL_OPEN_TAG);
                            writer.println(CELL_CLOSE_TAG);
                        }
                    }

                    // Process the beginning fo a new cell by flag
                    if (isNewCell) {
                        writer.print(CELL_OPEN_TAG);

                        // Checking and finalizing a cell if it is empty
                        if (currentChar == SEPARATOR) {
                            if (nextChar == SEPARATOR) {
                                writer.println(CELL_CLOSE_TAG);
                                ++currentCharIndex;
                                continue;
                            }

                            if (nextChar == END_OF_STRING) {
                                writer.println(CELL_CLOSE_TAG);
                                writer.println(ROW_CLOSE_TAG);
                                ++currentCharIndex;
                                continue;
                            }

                            if (nextChar == QUOTES) {
                                isNewCell = false;
                                separatorMode = false;
                                currentCharIndex += 2;
                                continue;
                            }

                            isNewCell = false;
                            ++currentCharIndex;
                            continue;
                        }

                        if (currentChar == QUOTES) {
                            isNewCell = false;
                            separatorMode = false;
                            ++currentCharIndex;
                            continue;
                        }

                        isNewCell = false;
                        continue;
                    }

                    // Code block for separatorMode reading mode,i.e. not in quotes
                    if (separatorMode) {
                        // If nextChar is a separator then
                        // write the char and finalizing the cell
                        if (nextChar == SEPARATOR) {
                            writer.print(getStringWithReplacements(currentChar));
                            writer.println(CELL_CLOSE_TAG);

                            isNewCell = true;
                            ++currentCharIndex;
                            continue;
                        }

                        // If nextChar is the end of line
                        // then finalizing the cell and the line
                        if (nextChar == END_OF_STRING) {
                            writer.print(getStringWithReplacements(currentChar));
                            writer.println(CELL_CLOSE_TAG);
                            writer.println(ROW_CLOSE_TAG);

                            isNewCell = true;
                            ++currentCharIndex;
                            continue;
                        }

                        writer.print(getStringWithReplacements(currentChar));
                        ++currentCharIndex;
                        continue;
                    }

                    // Code block for reading mode !separatorMode,
                    // i.e. when the cell contents are in quotes
                    if (currentChar == QUOTES) {
                        // If the previous character was an escape quote,
                        // then these quotes need to be written down and the flag turned off
                        if (isEscapeQuotes) {
                            writer.print(currentChar);
                            isEscapeQuotes = false;

                            // If followed by the end of the line, write a line break tag
                            if (nextChar == END_OF_STRING) {
                                writer.print(BREAK_LINE_TAG);
                            }

                            ++currentCharIndex;
                            continue;
                        }

                        // If nextChar is also quotes, set the flag isEscapeQuotes and move to the next char
                        if (nextChar == QUOTES) {
                            isEscapeQuotes = true;
                            ++currentCharIndex;
                            continue;
                        }

                        // If nextChar is a separator, then finalizing the cell
                        if (nextChar == SEPARATOR) {
                            writer.println(CELL_CLOSE_TAG);

                            separatorMode = true;
                            isNewCell = true;
                            ++currentCharIndex;
                            continue;
                        }

                        // If nextChar is the end of the line, then finalizing the cell and the line
                        if (nextChar == END_OF_STRING) {
                            writer.println(CELL_CLOSE_TAG);
                            writer.println(ROW_CLOSE_TAG);

                            separatorMode = true;
                            isNewCell = true;
                            ++currentCharIndex;
                            continue;
                        }
                    }

                    // If nextChar is the end of the line,
                    // then insert the char and add a line break tag.
                    // Then the line ends and the next one is read from the file.
                    if (nextChar == END_OF_STRING) {
                        writer.print(getStringWithReplacements(currentChar));
                        writer.print(BREAK_LINE_TAG);

                        ++currentCharIndex;
                        continue;
                    }

                    // If none of the conditions is met,
                    // write the current char to the file
                    writer.print(getStringWithReplacements(currentChar));
                    ++currentCharIndex;
                }
            }

            writer.println(TABLE_CLOSE_TAG);
            writer.print("</body>" + System.lineSeparator() + "</html>");

            System.out.printf("Success! See \"%s\"%n", outputFileName);
        } catch (IOException exception) {
            System.out.printf("File %s doesn't exist.", inputFileName);
        }
    }

    public static String getStringWithReplacements(char charToReplace) {
        if (charToReplace == '&') {
            return "&amp;";
        } else if (charToReplace == '<') {
            return "&lt;";
        } else if (charToReplace == '>') {
            return "&gt;";
        } else {
            return Character.toString(charToReplace);
        }
    }

    private static void printWrongArgumentsMessage() {
        System.out.printf("Wrong arguments.%n%n");

        printHelpMessage();
    }

    private static void printHelpMessage() {
        System.out.printf("Wrong arguments.%n%n");

        System.out.printf("NAME%n");
        System.out.printf("\tCsvConverter - converting csv into html-table.%n%n");

        System.out.printf("SYNOPSIS%n");
        System.out.printf("\tCsvConverter [input_filename] [output_filename]%n%n");

        System.out.printf("DESCRIPTION%n");
        System.out.printf("\tTwo arguments must be provided:%n%n");
        System.out.printf("\tinput_filename - input csv-file name (String) with delimiters%n%n");
        System.out.printf("\toutput_filename - output html-file name (String) for generated table,%n" +
                "\t\tpreferably to have .html or .htm extension.%n" +
                "\t\tIf file exists it will be overwritten, otherwise created.%n");
    }
}
