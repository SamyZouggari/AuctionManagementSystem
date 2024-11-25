import java.sql.*;
import java.util.Scanner;
import objets.*;
import oracle.jdbc.driver.OracleDriver;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;


public class Interface { 

    String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    String USER = "zouggars";
    String PASSWD = "zouggars";
    Connection conn;
    int compteurIdVente;
    int compteurIdProduit;
    String currMail;

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

    public void incrCompteurIdVente(){
        this.compteurIdVente += 1;
    }

    public int getCompteurIdProduit(){
        return this.compteurIdProduit;
    }

    public void incrCompteurIdProduit(){
        this.compteurIdProduit += 1;
    }

    public void OuvrirSalle(){
        System.out.println("De quelle catégorie sont les produits que vous aimereriez vendre dans cette salle ?");
        Scanner scan = new Scanner ( System . in );
        String categorie = scan.next ();
        scan.nextLine ();

        Salle nouvelleSalle = new Salle( ,categorie);
    }

    public void CreerVente(){
        int idVente = getCompteurIdVente();
        incrCompteurIdVente();
        Produit produit = CreerProduit();
        System.out.println("A quel prix de départ voulez-vous commencez cette vente ?");
        Scanner scan = new Scanner ( System . in );
        Float prixDeDepart = scan.nextFloat ();
        System.out.println("Dans quel salle voulez vous vendre votre produit?");
        //affiche les salles possibles, l'utilisateur rentre un numero de salle
        //besoin d'ajouter la verification que la salle choisie ait la même catégorie que le produit
        int idSalle = scan.nextInt();
        System.out.println("Désireriez-vous que cette vente soit révocable ou non ? Répondez par OUI ou NON.");
        String revocable = scan.nextLine ();
        boolean revocableBool = false;
        if (revocable.equals("OUI")){
            revocableBool = true;
        }else if (revocable.equals("NON")){
            revocableBool = false;
        }
        System.out.println("Désireriez-vous que cette vente soit montante ou descendante ? Répondez par MONTANTE ou DESCENDANTE.");
        String montante = scan.nextLine ();
        boolean montanteBool = true;
        if (montante.equals("MONTANTE")){
            montanteBool = true;
        }else if (revocable.equals("DESCENDANTE")){
            montanteBool = false;
        }
        System.out.println("Désireriez-vous que cette vente soit multiple ou non ? Répondez par OUI ou NON.");
        String multiple = scan.nextLine ();
        boolean multpileBool = true;
        if (multiple.equals("OUI")){
            multpileBool = true;
        }else if (multiple.equals("NON")){
            multpileBool = false;
        }
        new Vente(idVente, prixDeDepart, revocableBool, montanteBool, multpileBool, idSalle, currMail,produit.getIdProduit());
        //    public Vente(int idVente, float prixDepart, boolean revocable, boolean montante, boolean offreMultiple, int idSalle, String mailVendeur, int idProduit){
    }

    public Produit CreerProduit(){
        //renvoie l'id du produit créé
        int idProduit = getCompteurIdProduit();
        incrCompteurIdProduit();
        System.out.println("De quelle catégorie sont le/les produit(s) que vous aimereriez vendre ?");
        Scanner scan = new Scanner ( System . in );
        String categorie = scan.next ();
        System.out.println("Quelle quantité de ce produit aimeriez vous vendre ? ");
        int  quantite = scan.nextInt();
        System.out.println("Quelle est le prix de revient de ce produit pour vous ? ");
        Float  prixDeRevient = scan.nextFloat();
        return new Produit(categorie, idProduit,  prixDeRevient,quantite);
    }

    public Connection connexion() throws SQLException {

        //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
        DriverManager.registerDriver (new OracleDriver ());

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