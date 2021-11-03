import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public static void getProperties() {

        try {
            FileReader reader = new FileReader("src/main/resources/db.properties");
            Properties p = new Properties();
            p.load(reader);
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            String driver = p.getProperty("driver");
            System.out.println(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
