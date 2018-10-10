package com.mycompany.portfoliosimulator.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.mycompany.portfoliosimulator.utils.Params;
import com.mycompany.portfoliosimulator.utils.Utils;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLParser {
    private static Element root;

    public XMLParser(String fileName) {
        try {
            org.jdom2.Document parser = useDOMParser(fileName);
            root = parser.getRootElement();

        } catch (Exception e) {
            Utils.exception(e);
            System.exit(1);
        }

    }

    public Params parse(){
        return new Params(
                getListContent(root.getChild("timeframes").getChildren("timeframe")),
                root.getChildText("timeframe"),
                Integer.parseInt(root.getChildText("day")),
                Integer.parseInt(root.getChildText("portfolio")),
                Integer.parseInt(root.getChildText("cutoff")),
                root.getChildText("selector"));
    }

    //Get JDOM document from DOM Parser
    private static org.jdom2.Document useDOMParser(String fileName)
            throws ParserConfigurationException, SAXException, IOException {
        //creating DOM Document
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(fileName));
        DOMBuilder domBuilder = new DOMBuilder();
        return domBuilder.build(doc);
    }

    private static ArrayList<String> getListContent(List<org.jdom2.Element> list) {

        ArrayList<String> aList = new ArrayList<>();
        list.forEach(el -> aList.add(el.getText()));

        return aList;
    }
}
