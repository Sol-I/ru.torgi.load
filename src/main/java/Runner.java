import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class Runner {

    public static void main(String[] args) {

        System.out.println("------------ Start load... ------------");

        String url = Option.getURL((byte) 2, "20211102", "20211103",
                null, null);
//        String url = "https://torgi.gov.ru/opendata/7710349494-torgi/data.xml?bidKind=13&publishDateFrom=20210821T0000&publishDateTo=20210901T0000&lastChangeFrom=20210101T0000&lastChangeTo=20210901T0000";
        System.out.println("URL: " + url + "\n");

//        Document doc = Option.getDoc(url);
//        assert doc != null;

        //show list of tags
        //Option.listOfTags(doc);
        System.out.println("-------------------------------------------");

        //System.out.println(doc.getElementsByTagName("notification"));
        //Option.loadNotification(doc);

//        System.out.println("Max ID: " + Option.getLastNumber());

        Document doc2 = Option.getDoc("https://torgi.gov.ru/opendata/notification/55816779");
        assert doc2 != null;
//        Option.loadNotification(doc2);
        //Temp.listOfTags(doc2);
//        System.out.println(doc2);
//        assert doc2 != null;
//        Temp.listOfTags(doc2);

    }

}
