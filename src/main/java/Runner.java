import java.sql.SQLException;

public class Runner {

    public static void main(String[] args) throws SQLException {
        System.out.println("------------ Start load... ------------");
        String url = LoadData.getURL((byte) 2, "20211102", "20211104", null, null);
        System.out.println(url);
        LoadData.load(url);
        System.out.println("------------ End load... ------------");
    }
}