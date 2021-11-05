import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Temp {

    public static void showXMLtree(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            // Get element
            Element element = (Element) nodeList.item(i);
            System.out.println("<" + element.getNodeName());
        }

    }

    public static String space(byte countSpace) {
        StringBuilder sp = new StringBuilder();
        for (byte i = 0; i < countSpace; i++) sp.append("\t");
        return sp.toString();
    }


    public static void main(String[] args) {
        Statement st = getStatementMSSQL();
//        exampleTest();
    }

    public static void exampleTest() {
//        Map<String, Map<String, String>> gMap = getNotification("src/main/resources/example.xml");
//        Map<String, Map<String, String>> gMap = getNotification("https://torgi.gov.ru/opendata/notification/55829150");

    }

    public static void getTagDetail(Node node) {
        getTagDetail(node, "");
    }

    public static void getTagDetail(Node node, String tag) {
        if ((node.getNodeType() == Node.TEXT_NODE) && (LoadData.hasText(node.getTextContent()))) {
//            System.out.println("Node '" + node.getNodeName() + "' (type " + node.getNodeType() + ") has " +
//                    node.getChildNodes().getLength() + " child");
//        if (node.getNodeType() == Node.ELEMENT_NODE) {
//            System.out.println("Node '" + node.getNodeName() + "' is ELEMENT with value: " +
//                    ((Element) node).getNodeValue());
//        }
//            System.out.println("As node has no child RESULT is: ");
//            System.out.println("\t - lenght text content " + node.getTextContent().length());
//            System.out.println("\t - lenght text content without '\\n' " +
//                    node.getTextContent().replace("\n", "").length());
//            System.out.println("\t - lenght node value " + node.getNodeValue().length());
            System.out.println(tag.substring(1) + ": ("
                    + node.getTextContent().length() + ") (type: "
                    + node.getNodeType() + ") (child: "
                    + node.getChildNodes().getLength() + ") (nodeName: "
                    + node.getNodeName() + ") "
                    + LoadData.clearText(node.getTextContent()));
            System.out.println("--------------------------------------------------");
        } else {
            ArrayList<Node> nodeList = LoadData.clearNodeList(node.getChildNodes());
            System.out.println("Size of Array: " + nodeList.size());
            for (int i = 0; i < nodeList.size(); i++) {
                System.out.println("Go from node '" + node.getParentNode().getNodeName() + "' to node №" + (i + 1)
                        + "' " + nodeList.get(i).getNodeName() + "' with tag: " +
                        tag + "/" + nodeList.get(i).getParentNode().getNodeName());
                getTagDetail(nodeList.get(i), tag + "/" + nodeList.get(i).getParentNode().getNodeName());
            }
        }
    }

    public static void listOfTags(Document doc, String tag) {

        NodeList nodeList = doc.getElementsByTagName(tag);
        System.out.println("Count of nodes: " + nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            System.out.println("------------- " + node.getNodeName() + " --------------------");
            System.out.println(node.getNodeName() + ": (parent: "
                    + node.getParentNode().getNodeName() + ") ("
                    + node.getTextContent().length() + ") (type: "
                    + node.getNodeType() + ") (child: "
                    + node.getChildNodes().getLength() + ") ");
            //+ node.getTextContent());
            System.out.println("------------------------------------------");
        }

    }

    public static void newListOfTags(Document doc) {

        NodeList nodeList = doc.getElementsByTagName("notification");
        //System.out.println(nodeList.getLength());
        NodeList firstNode = nodeList.item(0).getChildNodes();

        for (int i = 0; i < firstNode.getLength(); i++) {
            Node node = firstNode.item(i);
            System.out.println(node.getNodeName() + ": (parent: "
                    + node.getParentNode().getNodeName() + ") ("
                    + node.getTextContent().length() + ") (type: "
                    + node.getNodeType() + ") (child: "
                    + node.getChildNodes().getLength() + ") "
                    + node.getTextContent());
            System.out.println("-------------next--------------------");
            for (int j = 1; j < node.getTextContent().length(); j++) {
                System.out.println(node.getTextContent().indexOf(j));
                System.out.println("!!!" + (char) -1);
            }

//             Get element
//            Node node = nodeList.item(i);
//            getTag(node, "");
//            System.out.println("!!! Number " + i + " ----" +node.getNodeType() + ": "
//                    + node.getNodeName());// + ": " + node.getTextContent());
//            if (node.getNodeType() == 1)
//                System.out.println(node.getNodeType() + ": " + node.getNodeName() + ": " + node.getTextContent());
//                System.out.println(node.getNodeName() + ": " + node.getFirstChild().getTextContent());
        }

    }

    public static int countOfTags(Document doc, String tag) {
        return doc.getElementsByTagName(tag).getLength();
    }

    public static void URLConnectionReader(String urls) throws IOException, ParserConfigurationException, SAXException {

        URL url = new URL(urls);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(url.openStream());
        NodeList nodes = doc.getElementsByTagName("*");
        System.out.println(nodes.getLength() + " nodes found");

    }

    public static Statement getStatementMSSQL() {

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

            // Create and execute a SELECT SQL statement
            Statement statement = con.createStatement();
            ResultSet resultSet;
            String selectSql = "SELECT * from notifications";
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }

            return statement;

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }

    }
}
