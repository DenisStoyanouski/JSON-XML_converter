package converter;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    private static  String xml;

    private static final Pattern xmlString = Pattern.compile("<.+");
    private static final Pattern openingTag = Pattern.compile("<[^<]+>");
    private static final Pattern closingTag = Pattern.compile("</[^<]+>");
    private static final Pattern attribute = Pattern.compile(">.+<");
    private static final Pattern jsonString = Pattern.compile("\\s*?\\{.+");
    private static final Pattern pObjectKey = Pattern.compile("\\{\"\\w+\"\\s*?:");
    private static final Pattern pObjectValue = Pattern.compile(":.+}");
    private static String json;

    private static Scanner scanner = new Scanner(System.in);

    public static void readData() {
        while(scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.matches(xmlString.pattern())) {
                xml = input;
                parseXmlToJson();
            }
            if (input.matches(jsonString.pattern())) {
                json = input;
                parseJsonToXml();
            }
        }
    }

    private static void parseJsonToXml() {
        Matcher objectKey = pObjectKey.matcher(json);
        Matcher objectValue = pObjectValue.matcher(json);
        String key = null;
        String value = null;

        if (objectKey.find()) {
            key = objectKey.group().replaceAll("\\{|\"|:|\\s+", "");
        }
        if (objectValue.find()) {
            value = objectValue.group().replaceAll("}|\"|:|\\s+","");
        }

        if ("null".equals(value)) {
            System.out.printf("<%s/>%n", key);
        } else {
            System.out.printf("<%1$s>%2$s</%1$s>", key, value);
        }


    }

    private static void parseXmlToJson() {
        Matcher open = openingTag.matcher(xml);
        Matcher att = attribute.matcher(xml);
        String tagName = null;
        String attributeText = null;

        if (open.find()) {
            tagName = open.group().replaceAll("(<|>|/>)", "\"");
        }
        if (att.find()) {
            attributeText = att.group().replaceAll("(<|>)", "\"");
        }
        System.out.printf("{%s:%s}", tagName, attributeText);
    }
}
