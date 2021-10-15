import org.w3c.dom.Document;

public class Runner {

    public static void main(String[] args) {

        System.out.println("------------ Start load... ------------");

        String url = "https://torgi.gov.ru/opendata/7710349494-torgi/data.xml?bidKind=13&publishDateFrom=20210821T0000&publishDateTo=20210901T0000&lastChangeFrom=20210101T0000&lastChangeTo=20210901T0000";
        Document doc = Option.getDoc(url);
        if (doc != null) Option.listOfTags(doc);

    }

}
