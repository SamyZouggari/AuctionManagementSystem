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

            System.out.println("Appuyer sur les touches suivantes");

            System.out.println("0 = Achat d'un produit");
            System.out.println("1 = Vente d'un produit");


            Scanner scannerNum = new Scanner(System.in);
            int num = scannerNum.nextInt();

            switch(num) {
                case 0:
                    System.out.println("Quelle est la catégorie de votre produit ?");
                    PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Produit WHERE NOMCATEGORIE = ?");
                    Scanner scannerProduit = new Scanner(System.in);
                    String produit = scannerProduit.next();
                    statement.setString(1,produit);
                    ResultSet res = statement.executeQuery();
                    Test_jcbd.dumpResult(res);
                    if(res.)
                    break;
                case 1:
                    System.out.println("KObz");
            }

            //préparation de la requête
            //PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Utilisateur WHERE NOMUSER = ?");

        } catch( SQLException e )
    {
        e.printStackTrace();
    }
}
}
