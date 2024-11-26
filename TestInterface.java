import java.sql.*;
import java.util.Scanner;


public class TestInterface {
    static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    static final String USER = "zouggars";
    static final String PASSWD = "zouggars";

    public static void main(String[] args) {
        try {
            //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            //Lancement d'une première connexion
            System.out.println("Connecting to the database...");
            Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected.");

            Interface inter = new Interface();
            inter.header("VENTE AUX ENCHERES");

            //préparation de la requête
            //PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Utilisateur WHERE NOMUSER = ?");

        } catch( SQLException e )
    {
        e.printStackTrace();
    }
}
}
