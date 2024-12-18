import java.sql.*;
import java.util.List;
import java.util.Scanner;

// Si vous lancez le test, le nom du produit c'est Manteau,
// appuyez sur l'achat psk la vente n'est pas codée encore
// le prix de l'offre c'est un float et ça s'écrit avec une virgule pas un point
public class TestInterface {
    private static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "zouggars";
    private static final String PASSWD = "zouggars";

    //On a utilisé comme base de données celle de Samy, où l'on a éxécuté nos codes SQL pour la ccréer et la remplir

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            
            //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            //Lancement d'une première connexion
            System.out.println("Connecting to the database...");
            Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            System.out.println("connected.");

            Interface inter = new Interface();
            inter.checkVentesDescendantes();
            inter.updateBD();
            String mail = inter.identification();
            System.out.println("Appuyer sur les touches suivantes");
            System.out.println("0 = Achat d'un produit");
            System.out.println("1 = Vente d'un produit");
            Scanner scannerNum = new Scanner(System.in);
            int num = scannerNum.nextInt();
            switch(num) {
                case 0:
                    inter.process_acheteur(mail);
                    break;
                case 1:
                    inter.process_vendeur();
                    break;

            }
            conn.commit();
            conn.close();
        } catch( SQLException e )
    {
        e.printStackTrace();
    }
}
}

