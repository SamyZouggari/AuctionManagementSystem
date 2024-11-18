import java.sql.*;


public class Test_jcbd { 

    static final String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    static final String USER = "dubaja";
    static final String PASSWD = "dubaja";
    PreparedStatement statement = connection.prepareStatement(" SELECT * FROM prenom WHERE age = ? " );

    public static void main(String[] args){

        try {

            //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver ());

            //Lancement d'une première connexion
            System.out.println("Connecting to the database...");
            Connection conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected.");

            //Demande de l'age
            System.out.println("Age de la recherche ?");
            Scanner scan = new Scanner ( System . in );
            String age = scan.next ();
            scan.nextLine ();

            //On prépare la requête et on l'exécute
            statement.setString(1,age);
            ResultSet res = statement.executeQuery();
            
            //Fermeture de la connexion
            res.close();
            statement.close();
            conn.close();


            
            
        } catch ( SQLException e ) {
            e.printStackTrace ();
        }
    }
}