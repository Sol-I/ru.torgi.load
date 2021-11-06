import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class LoadData {

    public static void load(String url) throws SQLException {

        Map<Long, Map<String, String>> gMap = LoadData.getNotification(url);
        LoadData.addDetailInfo(gMap);

        for (Map.Entry<Long, Map<String, String>> gm : gMap.entrySet()) {
            String bidNumber = gm.getValue().getOrDefault("bidNumber", "");
            for (Map.Entry<String, String> map : gm.getValue().entrySet()) {
                if (!map.getKey().equals("bidNumber")) {
                    StringBuilder query = new StringBuilder("INSERT INTO notifications (bidNumber, key_nm, val) VALUES ");
                    query.append("('")
                            .append(bidNumber).append("', '")
                            .append(map.getKey()).append("', '")
                            .append(map.getValue()).append("'")
                            .append("), ");
                    query.setLength(query.length() - 2);
                    LoadData.saveToBase(query.toString());
                }
            }
            System.out.println("Notification №" + gm.getKey() + " ("
                    + (int) (1.0 * gm.getKey() / gMap.size() * 100) + "%)");
//            if (gm.getKey() == 12) break;
        }
    }

    public static void saveToBase(String query) throws SQLException {
        Statement stmt = LoadData.getStatement();
        if (stmt != null) stmt.executeUpdate(query);
    }

    public static Map<Long, Map<String, String>> getNotification(String url) {

        //Global map of notifications
        Map<Long, Map<String, String>> gMap = new TreeMap<>();

        Document doc = LoadData.getDoc(url);
        if (doc != null) {
            //Get main list of notification
            NodeList mainNodeList = doc.getElementsByTagName("notification");
            for (int i = 0; i < mainNodeList.getLength(); i++) {
                Node node = mainNodeList.item(i);
                //Get all tag (key, values) in node i
                Map<String, String> map = getTag(node);
                gMap.put((long) i, map);
            }
        }
        return gMap;
    }

    public static void addDetailInfo(Map<Long, Map<String, String>> gMap) {

        //list of odDetailedHref
        Map<Long, String> temp = new TreeMap<>();
        for (Map.Entry<Long, Map<String, String>> gm : gMap.entrySet()) {
            if (gm.getValue().containsKey("odDetailedHref")) {
                String url = gm.getValue().get("odDetailedHref").replace("http://", "https://");
                temp.put(gm.getKey(), url);
            }
        }

        for (Map.Entry<Long, String> e : temp.entrySet()) {
            Document detailDoc = LoadData.getDoc(e.getValue());
            if (detailDoc != null) {
                NodeList detailNodeList = detailDoc.getElementsByTagName("notification");
                for (int i = 0; i < detailNodeList.getLength(); i++) {
                    Node detailNode = detailNodeList.item(i);
                    Map<String, String> detailMap = getTag(detailNode);
                    gMap.get(e.getKey()).putAll(detailMap);
                }
            }
        }
    }

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

    public static Statement getStatement() {

        try {
            FileReader reader = new FileReader("src/main/resources/META-INF/db.properties");
            Properties p = new Properties();
            p.load(reader);
            String url = p.getProperty("urlMSSQL");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            String driver = p.getProperty("driverMSSQL");

            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, password);
            return con.createStatement();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> getTag(Node node) {
        return getTag(node, "", new TreeMap<>());
    }

    public static Map<String, String> getTag(Node node, String tag, Map<String, String> map) {
        if ((node.getNodeType() == Node.TEXT_NODE) && (hasText(node.getTextContent()))) {
            //System.out.println(tag.substring(14) + ": " + clearText(node.getTextContent()));
            map.put(tag.substring(14), clearText(node.getTextContent()));
        } else {
            ArrayList<Node> nodeList = clearNodeList(node.getChildNodes());
            for (Node value : nodeList) {
                getTag(value, tag + "/" + value.getParentNode().getNodeName(), map);
            }
        }
        return map;
    }

    public static boolean hasText(String str) {
        return str.replaceAll("\n", "").replaceAll("\t", "")
                .replaceAll(" ", "").length() != 0;
    }

    public static String clearText(String str) {
        str = str.replaceAll("\n", "").replaceAll("\t", "");
        while (str.contains("  ")) str = str.replaceAll(" {2}", " ");
        return str;
    }

    public static ArrayList<Node> clearNodeList(NodeList nodeList) {
        ArrayList<Node> newNL = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ((node.getNodeType() != Node.TEXT_NODE) || (hasText(node.getTextContent()))) newNL.add(node);
        }
        return newNL;
    }

}