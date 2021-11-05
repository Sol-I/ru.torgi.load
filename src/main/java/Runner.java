import java.sql.SQLException;

public class Runner {

    public static void main(String[] args) throws SQLException {
        System.out.println("------------ Start load... ------------");
        String url = LoadData.getURL((byte) 2, "20211102", "20211103", null, null);
        System.out.println(url);
        LoadData.load(url);
        //LoadData.printNumber(gMap, "173");
        System.out.println("------------ End load... ------------");
    }
}