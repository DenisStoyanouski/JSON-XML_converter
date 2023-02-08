package converter;

import java.lang.reflect.GenericArrayType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    private static  String xml;

    private static final Pattern xmlString = Pattern.compile("<.+");
    private static final Pattern openingTag = Pattern.compile("<.+>");
    private static final Pattern closingTag = Pattern.compile("</[^<]+>");
    private static final Pattern pElement = Pattern.compile(">.+<");
    private static final Pattern pTagName = Pattern.compile("<\\w+");
    private static final Pattern pAttribute = Pattern.compile("\\w+\\s+=\\s+\"\\w+\"");
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
        Matcher mTagName = pTagName.matcher(xml);
        Matcher mAttribute = pAttribute.matcher(xml);
        Matcher mElement = pElement.matcher(xml);

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
            element = mElement.group().replaceAll("<|>", "\"");
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
