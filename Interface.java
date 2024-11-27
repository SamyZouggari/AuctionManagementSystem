import java.sql.*;
import java.util.Locale;
import java.util.Scanner;
import objets.*;
import oracle.jdbc.driver.OracleDriver;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import java.util.List;
import java.util.ArrayList;
//import java.util.Pair;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class Interface {

    String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    String USER = "zouggars";
    String PASSWD = "zouggars";
    Connection conn;
    int compteurIdVente;
    //ArrayList<Pair<String, String>> salles;
    int compteurIdProduit;
    String currMail;

    public Interface() {
        try {
            this.conn = connexion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.compteurIdVente = 0;
    }

    public int getCompteurIdVente() {
        return this.compteurIdVente;
    }

    public void incrCompteurIdVente() {
        this.compteurIdVente += 1;
    }

    public int getCompteurIdProduit() {
        return this.compteurIdProduit;
    }

    public void incrCompteurIdProduit() {
        this.compteurIdProduit += 1;
    }

    public void OuvrirSalle() {
        System.out.println("De quelle catégorie sont les produits que vous aimereriez vendre dans cette salle ?");
        Scanner scan = new Scanner(System.in);
        String categorie = scan.next();
        scan.nextLine();
        List new_list = new ArrayList<Vente>();
        Salle nouvelleSalle = new Salle(new_list, categorie);
    }

    public void CreerVente() throws SQLException {
        int idVente = getCompteurIdVente();
        incrCompteurIdVente();
        Produit produit = CreerProduit();
        System.out.println("A quel prix de départ voulez-vous commencez cette vente ?");
        Scanner scan = new Scanner(System.in);
        Float prixDeDepart = scan.nextFloat();
        System.out.println("Dans quel salle voulez vous vendre votre produit?");
        //affiche les salles possibles, l'utilisateur rentre un numero de salle
        //besoin d'ajouter la verification que la salle choisie ait la même catégorie que le produit
        int idSalle = scan.nextInt();
        System.out.println("Désireriez-vous que cette vente soit révocable ou non ? Répondez par OUI ou NON.");
        String revocable = scan.nextLine();
        boolean revocableBool = false;
        if (revocable.equals("OUI")) {
            revocableBool = true;
        } else if (revocable.equals("NON")) {
            revocableBool = false;
        }
        System.out.println("Désireriez-vous que cette vente soit montante ou descendante ? Répondez par MONTANTE ou DESCENDANTE.");
        String montante = scan.nextLine();
        boolean montanteBool = true;
        if (montante.equals("MONTANTE")) {
            montanteBool = true;
        } else if (revocable.equals("DESCENDANTE")) {
            montanteBool = false;
        }
        System.out.println("Désireriez-vous que cette vente soit multiple ou non ? Répondez par OUI ou NON.");
        String multiple = scan.nextLine();
        boolean multpileBool = true;
        if (multiple.equals("OUI")) {
            multpileBool = true;
        } else if (multiple.equals("NON")) {
            multpileBool = false;
        }
        new Vente(idVente, prixDeDepart, revocableBool, montanteBool, multpileBool, idSalle, currMail, produit.getIdProduit());
    }

    public Produit CreerProduit() throws SQLException {
        //renvoie l'id du produit créé
        int idProduit = getCompteurIdProduit();
        incrCompteurIdProduit();
        System.out.println("De quelle catégorie sont le/les produit(s) que vous aimereriez vendre ?");
        Scanner scan = new Scanner(System.in);
        String categorie = scan.next();
        System.out.println("Quelle est le nom du produit que vous aimeriez vendre ?");
        String nom_produit = scan.next();
        System.out.println("Quelle quantité de ce produit aimeriez vous vendre ? ");
        int quantite = scan.nextInt();
        System.out.println("Quelle est le prix de revient de ce produit pour vous ? ");
        Float prixDeRevient = scan.nextFloat();
        String produit = "(" + "'"+ Integer.toString(idProduit) + "' , '" + nom_produit+ "', '" + Float.toString(prixDeRevient)+ "', '"+ Integer.toString(quantite) + "', '"+ this.currMail +"'" +")";
        PreparedStatement statement = conn.prepareStatement("INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES ?;");
        statement.setString(1, produit);
        statement.executeQuery();
        return new Produit(categorie, idProduit, prixDeRevient, quantite);
    }

    public Connection connexion() throws SQLException {

        //Enregistrement du pilote spécifique à oracle fourni par oracle.jdbc
        DriverManager.registerDriver(new OracleDriver());

        //Lancement d'une première connexion
        System.out.println("Connecting to the database...");
        Connection conn = DriverManager.getConnection(this.CONN_URL, this.USER, this.PASSWD);
        System.out.println("connected.");

        return conn;
    }


    public void dumpResult(ResultSet r) throws SQLException {
        while (r.next()) {
            System.out.println("age: " + r.getString(1) +
                    "- prenom: " + r.getString(2) +
                    " - " + r.getInt(3) + " frères et soeurs");
        }
    }


    public void header(String titre) {
        System.out.println("--------------------------------------------------------------------------------------");
        System.out.println(" \t \t " + titre);
        System.out.println("--------------------------------------------------------------------------------------");
    }


    public void affichageSalles() throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement(" SELECT IdSalle,NomCategorie FROM SalleDeVente LEFT JOIN Propose ON SalleDeVente.IdSalle = Propose.IdSalle");

        ResultSet res = statement1.executeQuery();

        this.header("LISTE DES SALLES");

        while (res.next()) {

            String curr_salle = res.getString(0);
            String curr_categorie = res.getString(1);

            System.out.println("Salle n°" + curr_salle + " , Catégorie : " + curr_categorie);

            //this.salles.add(new Pair(curr_salle, curr_categorie));
        }
    }

    public boolean verifieProduit(String mail) throws SQLException {
        System.out.println("Quel produit voulez-vous acheter ?");
        PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM Produit WHERE NOMPRODUIT = ?");
        Scanner scannerProduit = new Scanner(System.in);
        String produit = scannerProduit.next();
        statement.setString(1, produit);
        ResultSet res = statement.executeQuery();
        if (res.next() && res.getInt(1) > 0) {
            System.out.println("Ce produit est disponible");
            enchere(produit, mail);
            return true;
        } else {
            return false;
        }
    }

    public void enchere(String produit, String mail) throws SQLException {
        System.out.println("----------------------------------------------------");
        System.out.println("Bienvenue dans l'enchère du produit correspondant : " + produit);
        PreparedStatement statementPrix = conn.prepareStatement("SELECT STOCK FROM Produit WHERE NOMPRODUIT = ?");
        statementPrix.setString(1, produit);
        ResultSet res = statementPrix.executeQuery();
        while (res.next()) {
            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");
            statementOffreMax.setString(1, "" + getIdProduit(produit));
            ResultSet resOffreMax = statementOffreMax.executeQuery();
            while (resOffreMax.next()) {
                float offreMax = resOffreMax.getFloat(1);
                int stock = res.getInt(1);
                System.out.println("Il y a " + stock + " " + produit + ", le prix de la dernière enchère est de : " + offreMax + " euros");
                System.out.println("Quelle est votre offre (en euros) ? ");
                Scanner scanOffre = new Scanner(System.in);
                float offre = scanOffre.nextFloat();
                System.out.println("Combien voulez-vous en acheter ? ");
                Scanner scanQuantite = new Scanner(System.in);
                int quantite = scanQuantite.nextInt();
                if (offre > offreMax) {
                    ajouteOffre(getIdProduit(produit), mail, offre, quantite);
                    System.out.println("Enchère effectuée");
                } else {
                    System.out.println("Vous ne pouvez pas réaliser une offre inférieure au prix de la dernière offre");
                }
            }
        }
    }

    public String getEMail(String produit) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT Email FROM Produit WHERE NOMPRODUIT = ?");
        statementPrix.setString(1, produit);
        ResultSet res = statementPrix.executeQuery();
        while (res.next()) {
            return res.getString(1);
        }
        return "";
    }

    public Timestamp getDateActuelle() {
        return Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime());
    }

    public int getIdProduit(String produit) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT idProduit FROM Produit WHERE NOMPRODUIT = ?");
        statementPrix.setString(1, produit);
        ResultSet res = statementPrix.executeQuery();
        while (res.next()) {
            return res.getInt(1);
        }
        return -1;
    }

    public void ajouteOffre(int IdVente, String eMail, float PrixAchat, int quantite) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("INSERT INTO OFFRE VALUES(?,?,?,?,?)");
        statementPrix.setInt(1,IdVente);
        statementPrix.setString(2, eMail);
        statementPrix.setFloat(3, PrixAchat);
        statementPrix.setTimestamp(4, getDateActuelle());
        statementPrix.setInt(5, quantite);
        statementPrix.executeUpdate();
    }

    public void suppressionData(int idProduit) throws SQLException {
        PreparedStatement statementProduit = conn.prepareStatement("DELETE FROM Produit WHERE idProduit = ?");
        statementProduit.setString(1, Integer.toString(idProduit));
        ResultSet res = statementProduit.executeQuery();
        //System.out.println("Produit supprimé de la base de donnée");
    }
}
