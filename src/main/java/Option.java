import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Option {

    public static void listOfTags(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            // Get element
            Element element = (Element) nodeList.item(i);
            System.out.println(element.getNodeName());
        }

    }

    public static void showXMLtree(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            // Get element
            Element element = (Element) nodeList.item(i);
            System.out.println("<" + element.getNodeName());
        }

    }

    public static Document getDoc(String url) {
        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(false);
            f.setValidating(false);
            DocumentBuilder b = f.newDocumentBuilder();

            URLConnection urlConnection = new URL(url).openConnection();
            //urlConnection.addRequestProperty("Accept", "application/xml");
            Document doc = b.parse(urlConnection.getInputStream());
            doc.getDocumentElement().normalize();

            return doc;

        } catch (SAXException | ParserConfigurationException | IOException e) {
            return null;
        }

    }

    public static String space (byte countSpace) {
        StringBuilder sp = new StringBuilder();
        for (byte i = 0; i < countSpace; i++) sp.append("\t");
        return sp.toString();
    }
}