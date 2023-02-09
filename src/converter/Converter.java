package converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    private static  String xmlSource;
    private static final Pattern xmlString = Pattern.compile("<.+");
    private static final Pattern openingTag = Pattern.compile("<.+>");
    private static final Pattern closingTag = Pattern.compile("</[^<]+>");
    private static final Pattern pElement = Pattern.compile(">.+<");
    private static final Pattern pTagName = Pattern.compile("<\\w+");
    private static final Pattern pAttribute = Pattern.compile("[@#\"\\w]+\\s*?(=|:)\\s*?\"[\\d\\w\\s]+\"?");
    private static String jsonSource;
    private static final Pattern jsonString = Pattern.compile("\\s*?\\{.+");
    private static final Pattern pObjectKey = Pattern.compile("\\{\\s*?\"\\w+\"\\s*?:");

    private static final Scanner scanner = new Scanner(System.in);

    public static void readData() {
        StringBuilder source = new StringBuilder();
        while (scanner.hasNextLine()) {
            source.append(scanner.nextLine().replaceAll("\\s+", " "));
        }

        if (source.toString().matches(xmlString.pattern())) {
            xmlSource = source.toString();
            parseXmlToJson();
        }
        if (source.toString().matches(jsonString.pattern())) {
            jsonSource = source.toString();
            parseJsonToXml();
        }
    }

    private static void parseJsonToXml() {
        Matcher objectKey = pObjectKey.matcher(jsonSource);
        Matcher objectValue = pAttribute.matcher(jsonSource);
        String element = null;
        String content = null;
        List<String[]> attributes = new ArrayList<>();

        if (objectKey.find()) {
            element = objectKey.group().replaceAll("\\{|\"|:|\\s+", "");
        }
        while (objectValue.find()) {
            String attributeName = objectValue.group().substring(0, objectValue.group().indexOf(":")).replaceAll("[\"\\s+]", "");
            String attributeValue = objectValue.group().substring(objectValue.group().indexOf(":") + 1).replaceAll("\"", "");
            if (attributeName.startsWith("@")) {
                attributes.add(new String[] {attributeName.replaceAll("@",""), attributeValue.trim()});
            }
            if (attributeName.startsWith("#")) {
                content = attributeValue.trim();
            }
        }

        System.out.printf("<%s", element);
        attributes.forEach(x-> System.out.printf(" %s = \"%s\"", x[0], x[1]));
        System.out.printf(content == null ? "/>%n" : ">%s</%s>", content, element);


    }

    private static void parseXmlToJson() {
        Matcher mTagName = pTagName.matcher(xmlSource);
        Matcher mAttribute = pAttribute.matcher(xmlSource);
        Matcher mElement = pElement.matcher(xmlSource);

        String tagName = null;
        List<String[]> attributes = new ArrayList<>();
        String element = null;

        if (mTagName.find()) {
            tagName = mTagName.group().replaceAll("<", "");
        }

        while (mAttribute.find()) {
            String attributeName = mAttribute.group().substring(0, mAttribute.group().indexOf("=")).trim();
            String attributeValue = mAttribute.group().substring(mAttribute.group().indexOf("=") + 1).trim();
            attributes.add(new String[] {"\"@" + attributeName + "\"" , attributeValue});
        }
        if (mElement.find()) {
            element = mElement.group().replaceAll("[<>]", "\"");
        }
        if (attributes.size() != 0) {
            attributes.add(new String[] {"\"#" + tagName + "\"", element});
        }

        //Print JSON object
        System.out.printf("{\"%s\" : ", tagName);
        if (attributes.size() != 0) {
            System.out.print("{");
            attributes.forEach(x-> System.out.print("\n\t" + x[0] + " : " +  x[1]));
            System.out.println("\n\t}");
        } else {
            System.out.print(element);
        }
        System.out.println("}");
    }
}
