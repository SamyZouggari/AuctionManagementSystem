import java.sql.*;
import java.util.Scanner;
import objets.*;


public class Interface { 

    String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    String USER = "zouggars";
    String PASSWD = "zouggars";
    Connection conn;
    int compteurIdVente;


    public Interface(){
        try {
            this.conn = connexion();
        }catch(SQLException e){
            e.printStackTrace();
        }
        this.compteurIdVente = 0;
    }

    public int getCompteurIdVente(){
        return this.compteurIdVente;
    }

    public void setCompteurIdVente(int compteurIdVente){
        this.compteurIdVente = compteurIdVente;
    }

    public void OuvrirSalle(){
        System.out.println("De quelle catégorie sont les produits que vous aimereriez vendre dans cette salle ?");
        Scanner scan = new Scanner ( System . in );
        String categorie = scan.next ();
        scan.nextLine ();

        Salle nouvelleSalle = new Salle( ,categorie);
    }

    public void CreerVente(){
        System.out.println("");
    }

    public Connection connexion() throws SQLException {

        //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
        DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver ());

        //Lancement d'une première connexion
        System.out.println("Connecting to the database...");
        Connection conn = DriverManager.getConnection(this.CONN_URL, this.USER, this.PASSWD);
        System.out.println("connected.");

        return conn;
    }


    public void dumpResult(ResultSet r) throws SQLException {
        while (r.next()) {
            System.out.println( "age: " + r.getString(1) +
            "- prenom: " + r.getString(2) +
            " - " + r.getInt(3) + " frères et soeurs");
        }
    }



}