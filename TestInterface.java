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

            System.out.println("0 = Créer une salle de ventes");
            System.out.println("1 = Créer une vente dans une salle déjà existante");
            System.out.println("2 = Afficher la liste des produits en ventes");
            Scanner scanner = new Scanner(System.in);
            int num = scanner.nextInt();

            /*switch(num){
                case 0:
                    inter.OuvrirSalle();
                    break;
                case 1:*/

            //préparation de la requête
            //PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Utilisateur WHERE NOMUSER = ?");

        } catch( SQLException e )
    {
        e.printStackTrace();
    }
}
}
