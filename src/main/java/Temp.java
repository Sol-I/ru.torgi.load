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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        exampleTest();
    }

    public static void exampleTest() {
//        Map<String, Map<String, String>> gMap = getNotification("src/main/resources/example.xml");
        Map<String, Map<String, String>> gMap = getNotification(Option.getURL((byte) 2, "20211102", "20211103", null, null));
//        Map<String, Map<String, String>> gMap = getNotification("https://torgi.gov.ru/opendata/notification/55829150");
        addDetailInfo(gMap);
        printNumber(gMap, "173");
    }

    public static void printAll(Map<String, Map<String, String>> gMap) {
        //Print global map
        for (Map.Entry<String, Map<String, String>> gm : gMap.entrySet()) {
            for (Map.Entry<String, String> m : gm.getValue().entrySet()) {
                System.out.println("Num " + gm.getKey() + " -- Key: " + m.getKey() + ", value: " + m.getValue());
            }
        }
    }

    public static void printNumber(Map<String, Map<String, String>> gMap, String idNotificaton) {
        //Print specify idNotificaton
        for (Map.Entry<String, Map<String, String>> gm : gMap.entrySet()) {
            if (gm.getKey().startsWith(idNotificaton) || gm.getKey().startsWith(idNotificaton + "-")) {
                for (Map.Entry<String, String> m : gm.getValue().entrySet()) {
                    System.out.println("Num " + gm.getKey() + " -- Key: " + m.getKey() + ", value: " + m.getValue());
                }
            }
        }
    }

    public static Map<String, Map<String, String>> getNotification(String url) {

        //Global map of notifications
        Map<String, Map<String, String>> gMap = new HashMap<>();

        Document doc = Option.getDoc(url);
        if (doc != null) {
            //Get main list of notification
            NodeList mainNodeList = doc.getElementsByTagName("notification");
            for (int i = 0; i < mainNodeList.getLength(); i++) {
                Node node = mainNodeList.item(i);
                //Get all tag (key, values) in node i
                Map<String, String> map = getTag(node);
                gMap.put(String.valueOf(i), map);
            }
        }
        return gMap;
    }

    public static void addDetailInfo(Map<String, Map<String, String>> gMap) {

        Map<String, String> temp = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> gm : gMap.entrySet()) {
            String idNotificaton = gm.getKey();
            //Check availability tag 'odDetailedHref'
            if (gm.getValue().containsKey("odDetailedHref")) {
                //Get doc odDetailedHref
                String url = gm.getValue().get("odDetailedHref").replace("http://", "https://");
                temp.put(idNotificaton, url);
            }
        }

        for (Map.Entry<String, String> e : temp.entrySet()) {
            Document detailDoc = Option.getDoc(e.getValue());
            if (detailDoc != null) {
                NodeList detailNodeList = detailDoc.getElementsByTagName("notification");
                for (int i = 0; i < detailNodeList.getLength(); i++) {
                    Node detailNode = detailNodeList.item(i);
                    Map<String, String> detailMap = getTag(detailNode);
                    gMap.put(e.getKey() + "-" + i, detailMap);
                }
            }
        }
    }

    public static void getTagDetail(Node node) {
        getTagDetail(node, "");
    }

    public static void getTagDetail(Node node, String tag) {
        if ((node.getNodeType() == Node.TEXT_NODE) && (hasText(node.getTextContent()))) {
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
                    + clearText(node.getTextContent()));
            System.out.println("--------------------------------------------------");
        } else {
            ArrayList<Node> nodeList = clearNodeList(node.getChildNodes());
            System.out.println("Size of Array: " + nodeList.size());
            for (int i = 0; i < nodeList.size(); i++) {
                System.out.println("Go from node '" + node.getParentNode().getNodeName() + "' to node №" + (i + 1)
                        + "' " + nodeList.get(i).getNodeName() + "' with tag: " +
                        tag + "/" + nodeList.get(i).getParentNode().getNodeName());
                getTagDetail(nodeList.get(i), tag + "/" + nodeList.get(i).getParentNode().getNodeName());
            }
        }
    }

    public static Map<String, String> getTag(Node node) {
        return getTag(node, "", new HashMap<>());
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
}
