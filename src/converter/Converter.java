package converter;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Converter {
    private String xml;
    private static Pattern tag = Pattern.compile("<[^<]+/>");
    private String json;
    private static Pattern jsonPattern = Pattern.compile("\\{[^{]+}");

    private static Scanner scanner = new Scanner(System.in);

    public static void readData() {
        while(scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.matches(tag.pattern())) {
                parseXmlToJson();
            }
            if (input.matches(jsonPattern.pattern())) {
                parseJsonToXml();
            }
        }
    }

    private static void parseJsonToXml() {

    }

    private static void parseXmlToJson() {
    }
}
