package tn.esprit.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance ;

    private final String URL="jdbc:mysql://localhost:3306/database";
    private final String USERNAME ="root";
    private final String PASSWORD ="";
    private  Connection cnx ;

    private DataBase (){

        try {
            cnx = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connected success");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(" ___ Connection Failed ___");
        }
    }


    public static DataBase getInstance() {
        if(instance == null)
            instance = new DataBase();

        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }

}
