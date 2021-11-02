import org.w3c.dom.Document;

public class Runner {

    public static void main(String[] args) {

        System.out.println("------------ Start load... ------------");

        String url = Option.getURL((byte) 2, "20211102", "20211103",
                null, null);
//        String url = "https://torgi.gov.ru/opendata/7710349494-torgi/data.xml?bidKind=13&publishDateFrom=20210821T0000&publishDateTo=20210901T0000&lastChangeFrom=20210101T0000&lastChangeTo=20210901T0000";
        System.out.println("URL: " + url + "\n");

        Document doc = Option.getDoc(url);
        assert doc != null;

        //show list of tags
        Option.listOfTags(doc);
        System.out.println("-------------------------------------------");

        //System.out.println(doc.getElementsByTagName("organizationName"));

    }

}
