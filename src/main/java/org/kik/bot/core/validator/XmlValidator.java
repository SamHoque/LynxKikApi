package org.kik.bot.core.validator;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class XmlValidator {
    private String partialData;

    public String getAsCompleteValidXml(String data) {
        String completeData = completeData(data);
        return removeTagWithPossibleInvalidValue(completeData);
    }

    public boolean isStringValidXml(String data) {
        if(!isCompleteData(data) || !parseDataAsXml(data)) {
            return false;
        } else {
            return true;
        }
    }

    private String removeTagWithPossibleInvalidValue(String completeData) {
        return completeData.replaceAll("([<]images[>])([\\s\\S]*)(</images>)", "");
    }

    private String completeData(String data) {
        //todo: improve with regex
        boolean validStart = data.startsWith("<");
        boolean validEnd = data.endsWith(">");

        String completeData = data;
        if (validStart && !validEnd) {
            partialData = data;
        } else if (!validStart && !validEnd) {
            partialData += data;
        } else if (!validStart && validEnd) {
            completeData = partialData + data;
        }

        return completeData;
    }

    private boolean isCompleteData(String data) {
        return data.startsWith("<") && data.endsWith(">");
    }

    private boolean parseDataAsXml(String data) {
        boolean validXml = false;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.parse(input);
            validXml = true;
        } catch (UnsupportedEncodingException | ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return validXml;
    }
}
