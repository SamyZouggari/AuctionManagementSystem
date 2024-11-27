import java.sql.*;
import java.util.List;
import java.util.Scanner;

// Si vous lancez le test, le nom du produit c'est Manteau
// appuyez sur l'achat psk la vente n'est pas codée encore
// le prix de l'offre c'est un float et ça s'écrit avec une virgule pas un point
public class TestInterface {
    private static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    private static final String USER = "zouggars";
    private static final String PASSWD = "zouggars";

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
            System.out.println("Bonjour, veuillez vous identifier : ");
            System.out.println("Adresse mail : ");
            Scanner scannerMail = new Scanner(System.in);
            String mail = scannerMail.nextLine();

            System.out.println("Nom de famille : ");
            Scanner scannerNom = new Scanner(System.in);
            String nom = scannerNom.nextLine();

            System.out.println("Prenom : ");
            Scanner scannerPrenom = new Scanner(System.in);
            String prenom = scannerPrenom.nextLine();

            System.out.println("Adresse postale : ");
            Scanner scannerAdresse = new Scanner(System.in);
            String adresse = scannerAdresse.nextLine();

            PreparedStatement requestUser = conn.prepareStatement("SELECT EMAIL FROM  UTILISATEUR WHERE EMAIL = ?");
            requestUser.setString(1, mail);
            inter.setEmail(mail);
            ResultSet rs = requestUser.executeQuery();
            if (rs.next() && rs.getString(1).equals(mail)) {
                System.out.println("Vous êtes connecté " + prenom);
            } else {
                PreparedStatement statementPrix = conn.prepareStatement("INSERT INTO UTILISATEUR VALUES(?,?,?,?)");
                statementPrix.setString(1, mail);
                statementPrix.setString(2, nom);
                statementPrix.setString(3, prenom);
                statementPrix.setString(4, adresse);
                statementPrix.executeUpdate();
                System.out.println("Vous avez bien été enregistré " + prenom);
                System.out.println("----------------------------------------------");
            }



            System.out.println("Appuyer sur les touches suivantes");

            System.out.println("0 = Achat d'un produit");
            System.out.println("1 = Vente d'un produit");


            Scanner scannerNum = new Scanner(System.in);
            int num = scannerNum.nextInt();

            switch(num) {
                case 0:
                    inter.process_acheteur();
                    break;
                case 1:
                    inter.process_vendeur();
                    break;
            }

            //préparation de la requête
            //PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Utilisateur WHERE NOMUSER = ?");

        } catch( SQLException e )
    {
        e.printStackTrace();
    }
}
}
