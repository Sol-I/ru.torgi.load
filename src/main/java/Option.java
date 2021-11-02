import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Option {

    public static String getURL(byte bidKind, String publishDateFrom, String publishDateTo,
                                String lastChangeFrom, String lastChangeTo) {

        if (bidKind == 0) return "";

        StringBuilder url =
                new StringBuilder("https://torgi.gov.ru/opendata/7710349494-torgi/data.xml?")
                        .append("bidKind=").append(String.valueOf(bidKind));

        if (publishDateFrom != null) url.append("&publishDateFrom=").append(publishDateFrom).append("T0000");
        if (publishDateTo != null) url.append("&publishDateTo=").append(publishDateTo).append("T0000");
        if (lastChangeFrom != null) url.append("&lastChangeFrom=").append(lastChangeFrom).append("T0000");
        if (lastChangeTo != null) url.append("&lastChangeTo=").append(lastChangeTo).append("T0000");

        return url.toString();

    }

    public static Document getDoc(String url) {

        //Get xml document from URL address
        try {

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(false);
            f.setValidating(false);
            DocumentBuilder b = f.newDocumentBuilder();

            URLConnection urlConnection = new URL(url).openConnection();
            Document doc = b.parse(urlConnection.getInputStream(), "UTF-8");
            doc.getDocumentElement().normalize();

            return doc;

        } catch (SAXException | ParserConfigurationException | IOException e) {
            return null;
        }

    }

    public static void listOfTags(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            // Get element
            Element element = (Element) nodeList.item(i);
//            System.out.println(element.getNodeName() + ": " + element.getTextContent());
            if (element.getNodeType() == 1)
                System.out.println(element.getNodeName() + ": " + element.getFirstChild().getNodeValue());
        }

    }

}