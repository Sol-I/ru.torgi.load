import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;

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

    public static int countOfTags(Document doc, String tag) {
        return doc.getElementsByTagName(tag).getLength();
    }

    public static void saveNotification(Document doc) {

        int countOfNotification = Option.countOfTags(doc, "notification");
        if (countOfNotification > 0) {
            int countOfChild;
            NodeList listOfChild;
            NodeList nodeList = doc.getElementsByTagName("notification");
            for (int i = 0; i < countOfNotification; i++) {
                System.out.println("------------ Notification " + (i + 1) + " ------------");
                listOfChild = nodeList.item(i).getChildNodes();
                countOfChild = listOfChild.getLength();
                if (countOfChild > 0) {
                    Node node;
                    for (int j = 0; j < countOfChild; j++) {
                        node = listOfChild.item(j);
                        if (node.getNodeType() != Node.TEXT_NODE) {
                            System.out.println(node.getNodeName() + ": "
                                    + node.getFirstChild().getNodeValue());
                        }
                    }
                }
            }
        }
    }

    public static long getLastNumber() {

        // JDBC URL, username and password of MySQL server
        String url = "jdbc:mysql://localhost:3306/torgi";
        String user = "root";
        String password = "admin";

        // JDBC variables for opening and managing connection
        String query = "SELECT max(ID) FROM notifications";
        int count = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();

        }
        return count;
    }
}