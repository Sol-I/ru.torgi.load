import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

}
