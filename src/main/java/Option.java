import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.*;

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
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc;
            if (url.contains("http")) {
                InputStream inputStream = new URL(url).openStream();
                doc = b.parse(inputStream, "UTF-8");
                inputStream.close();
            } else {
                doc = b.parse(new File(url));
            }
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("*");
//            System.out.println(nodes.getLength() + " nodes found");
            return doc;
        } catch (SAXException | ParserConfigurationException | IOException e) {
            return null;
        }
    }

    public static void loadNotification(Document doc) throws SQLException {

        NodeList nodeList = doc.getElementsByTagName("notification");
        int countOfNotification = nodeList.getLength();

        if (countOfNotification > 0) {
            long currentID = Option.getLastNumber();
            for (int i = 0; i < countOfNotification; i++) {

                System.out.println("------------ Notification " + (i + 1) + " ------------");

                NodeList listOfChild = nodeList.item(i).getChildNodes();
                int countOfChild = listOfChild.getLength();

                if (countOfChild > 0) {
                    Map<String, String> map = new HashMap<>();

                    StringBuilder nameKey = new StringBuilder();

                    for (int j = 0; j < countOfChild; j++) {
                        Node node = listOfChild.item(j);

                        System.out.println("!!! Number " + j + " ----" + node.getNodeType() + ": "
                                + node.getNodeName() + ": " + node.getTextContent());
                        String ss = node.hasChildNodes() ? node.getFirstChild().getNodeValue() : "";
                        System.out.println("!!! Number " + j + " ----"
                                + node.getNodeType() + ": "
                                + node.getNodeName() + ": "
                                + ss);

                        if (node.getNodeType() != Node.TEXT_NODE) {
                            nameKey.append("/").append(node.getNodeName());
                        } else if (!nameKey.toString().equals("")) {
                            map.put(nameKey.toString(), node.getNodeValue());
                            nameKey = new StringBuilder();
                        }
                    }

                    if (map.size() > 0) {
                        StringBuilder query = new StringBuilder("INSERT INTO notifications (id, bidNumber, `key`, value) VALUES ");
                        String bidNumber = map.getOrDefault("bidNumber", "");
                        currentID++;
                        for (Map.Entry<String, String> e : map.entrySet()) {
                            query.append("(")
                                    .append(currentID).append(", '")
                                    .append(bidNumber).append("', '")
                                    .append(e.getKey()).append("', '")
                                    .append(e.getValue()).append("'")
                                    .append("), ");
                        }
                        query.setLength(query.length() - 2);
                        //Option.saveToBase(query.toString());
                        System.out.println(query.toString());
                    }
                }
            }
        }
    }

    public static long getLastNumber() throws SQLException {
        Statement stmt = Option.getStatement();

        assert stmt != null;
        ResultSet rs = stmt.executeQuery("SELECT max(ID) FROM notifications");

        assert rs != null;
        rs.next();
        return rs.getInt(1);
    }

    public static void saveToBase(String query) throws SQLException {
        Statement stmt = Option.getStatement();
        assert stmt != null;
        stmt.executeUpdate(query);
    }

    public static Statement getStatement() {

        try {
            FileReader reader = new FileReader("src/main/resources/META-INF/db.properties");
            Properties p = new Properties();
            p.load(reader);
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            String driver = p.getProperty("driver");

            Class.forName(driver);

            Connection con = DriverManager.getConnection(url, user, password);
            return con.createStatement();

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}