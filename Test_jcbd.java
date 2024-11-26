import java.sql.*;
import java.util.Scanner;




public class Test_jcbd { 

    static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    static final String USER = "zouggars";
    static final String PASSWD = "zouggars";

    public static void main(String[] args){

        try {

            //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver ());

            //Lancement d'une première connexion
            System.out.println("Connecting to the database...");
            Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected.");

            //préparation de la requête
            PreparedStatement statement = conn.prepareStatement(" SELECT * FROM Utilisateur WHERE NOMUSER = ?" );

            //Demande de l'age
            System.out.println("Nom de la personne recherchée :");
            Scanner scan = new Scanner ( System . in );
            String age = scan.next ();
            scan.nextLine ();

            //On prépare la requête et on l'exécute
            statement.setString(1,age);
            ResultSet res = statement.executeQuery();

//            //Affichage du résultat
            dumpResult(res);
//
//            //Fermeture de la connexion
//            res.close();
//            statement.close();
//            conn.close();
//
//
//
//
        } catch ( SQLException e ) {
            e.printStackTrace ();
        }
    }


    public static void dumpResult(ResultSet r) throws SQLException {
        ResultSetMetaData rsetmd = r.getMetaData();
        int i = rsetmd.getColumnCount();
        while (r.next()) {
            for (int j = 1; j <= i; j++) {
                System.out.print(r.getString(j) + "\t");
            }
            System.out.println();
        }
    }

}