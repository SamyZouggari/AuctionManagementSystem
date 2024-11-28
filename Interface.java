import java.sql.*;
import java.util.Locale;
import java.util.Scanner;
import objets.*;
import oracle.jdbc.driver.OracleDriver;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import java.util.List;
import java.util.ArrayList;
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

    public void CreerVente(int idProduit, String categorie) throws SQLException {
        int idVente = getCompteurIdVente();
        incrCompteurIdVente();
        Scanner scan = new Scanner(System.in);
        System.out.println("A quel prix de départ voulez-vous commencez cette vente ?");
        Float prixDeDepart = scan.nextFloat();
        this.affichageSalles(categorie);
        System.out.println("Dans quelle salle voulez vous vendre votre produit ?");
        //affiche les salles possibles, l'utilisateur rentre un numero de salle
        //besoin d'ajouter la verification que la salle choisie ait la même catégorie que le produit
        int idSalle = scan.nextInt();
        System.out.println("Désireriez-vous que cette vente soit révocable ou non ? Répondez par OUI ou NON.");
        String revocable = scan.nextLine();
        int revocableInt = 0;
        if (revocable.equals("OUI")) {
            revocableInt = 1;
        } else if (revocable.equals("NON")) {
            revocableInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit montante ou descendante ? Répondez par MONTANTE ou DESCENDANTE.");
        String montante = scan.nextLine();
        int montanteInt = 1;
        if (montante.equals("MONTANTE")) {
            montanteInt = 1;
        } else if (revocable.equals("DESCENDANTE")) {
            montanteInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit multiple ou non ? Répondez par OUI ou NON.");
        String multiple = scan.nextLine();
        int multipleInt = 1;
        if (multiple.equals("OUI")) {
            multipleInt = 1;
        } else if (multiple.equals("NON")) {
            multipleInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit limitée ou non ? Répondez par OUI  ou NON. Si oui, entrez la date et l'heure de fin sous forme AAAA-MM-JJ.");
        String limité = scan.nextLine();
        int limiteInt = 1;
        if (limité.equals("OUI")) {
            limiteInt = 1;
        } else if (limité.equals("NON")) {
            limiteInt = 0;
        }
        PreparedStatement statement = conn.prepareStatement("INSERT INTO Vente (IdVente, PrixDepart, DureeLimite, Revocable, Montante, OffreMultiple, IdProduit, IdSalle) VALUES (?,?,?,?,?,?, ?, ?)");
        statement.setInt(1, idVente);
        statement.setFloat(2, prixDeDepart);
        statement.setInt(3, limiteInt);
        statement.setInt(4, revocableInt);
        statement.setInt(5, montanteInt);
        statement.setInt(6, multipleInt);
        statement.setInt(7, idProduit);
        statement.setInt(8, idSalle);

    }

//    public Produit CreerProduit() throws SQLException {
//        //renvoie l'id du produit créé
//        int idProduit = getCompteurIdProduit();
//        incrCompteurIdProduit();
//        System.out.println("De quelle catégorie sont le/les produit(s) que vous aimereriez vendre ?");
//        Scanner scan = new Scanner(System.in);
//        String categorie = scan.next();
//        System.out.println("Quelle est le nom du produit que vous aimeriez vendre ?");
//        String nom_produit = scan.next();
//        System.out.println("Quelle quantité de ce produit aimeriez vous vendre ? ");
//        int quantite = scan.nextInt();
//        System.out.println("Quelle est le prix de revient de ce produit pour vous ? ");
//        Float prixDeRevient = scan.nextFloat();
//        //String produit = "(" + "'"+ Integer.toString(idProduit) + "' , '" + nom_produit+ "', '" + Float.toString(prixDeRevient)+ "', '"+ Integer.toString(quantite) + "', '" +categorie+ "', '"+ this.currMail +"'" +")";
//        //System.out.println(produit);
//        PreparedStatement statement = conn.prepareStatement("INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (?,?,?,?,?,?)");
//        statement.setInt(1, idProduit);
//        statement.setString(2, nom_produit);
//        statement.setFloat(3, prixDeRevient);
//        statement.setInt(4, quantite);
//        statement.setString(5, categorie);
//        statement.setString(6, this.currMail);
//        statement.executeUpdate();
//        statement.close();
//        return new Produit(categorie, idProduit, prixDeRevient, quantite);
//    }

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
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" \t \t " + titre);
        System.out.println("--------------------------------------------------------------------------------");
    }

    public static void clearScreen() {  
    System.out.print("\033[H\033[2J");  
    System.out.flush();  
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //INTERFACE ACHETEUR

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void affichageSalles() throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement(" SELECT SalleDeVente.IdSalle,NomCategorie FROM SalleDeVente LEFT JOIN Propose ON SalleDeVente.IdSalle = Propose.IdSalle");

        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("LISTE DES SALLES");

        while (res.next()) {

            int curr_salle = res.getInt(1);
            String curr_categorie = res.getString(2);

            System.out.println("Salle n°" + curr_salle + " , Catégorie : " + curr_categorie);


        }
    }

    public void affichageSalles(String categorie) throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement(" SELECT SalleDeVente.IdSalle,NomCategorie FROM SalleDeVente LEFT JOIN Propose ON SalleDeVente.IdSalle = Propose.IdSalle WHERE NomCategorie = ?");

        statement1.setString(1,categorie);

        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("LISTE DES SALLES");

        while (res.next()) {

            int curr_salle = res.getInt(1);
            String curr_categorie = res.getString(2);

            System.out.println("Salle n°" + curr_salle + " , Catégorie : " + curr_categorie);
        }
        res.close();
        statement1.close();
    }



    public void affichageVentes(int IdSalle) throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement(" SELECT IdVente,NomProduit, PrixDepart FROM Vente LEFT JOIN Produit ON Vente.idProduit = Produit.idProduit WHERE IdSalle = ?");

        statement1.setInt(1, IdSalle);

        
        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("VENTES DANS LA SALLE N°" + IdSalle);

        while (res.next()) {

            int curr_vente = res.getInt(1);
            String curr_nom = res.getString(2);

            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res2 =  statementOffreMax.executeQuery();

            while(res2.next()){
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + res2.getString(1));
            }



        }
    }







    public void process_acheteur() throws SQLException{
        this.affichageSalles();

        System.out.println("\n\nDans quelle salle désirez vous vous rendre ? ");

        Scanner scannerNum = new Scanner(System.in);
        int num = scannerNum.nextInt();

        this.affichageVentes(num);

        System.out.println("\n\nSur quelle vente voulez-vous enchérir ?");

        //Gestion de l'enchère avec le code de Samy
    }   

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //INTERFACE VENDEUR

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void affichageCategories() throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement(" SELECT DISTINCT NomCategorie FROM Produit");

        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("LISTE DES CATEGORIES");

        while (res.next()) {
            String curr_categorie = res.getString(1);

            System.out.println("Catégorie : " + curr_categorie);


        }
    }

    public void affichageProduits(String cat) throws SQLException {
        PreparedStatement statement1 = conn.prepareStatement("SELECT NomProduit, Stock, IdProduit FROM Produit WHERE NomCategorie = ?");

        statement1.setString(1, cat);

        ResultSet res = statement1.executeQuery();
        
    
        this.clearScreen();

        this.header("LISTE DES PRODUITS");

        while (res.next()) {
            String curr_produit = res.getString(1);
            int curr_stock = res.getInt(2);
            int curr_id_produit = res.getInt(3);

            System.out.println("Produit : " + curr_produit + " , Stock : " + curr_stock + ", IdProduit : " + curr_id_produit);
        }

    }




    public void process_vendeur() throws SQLException{
        
        this.affichageCategories();

        System.out.println("\n\n Quelle catégorie de produit désirez-vous vendre ?");

        Scanner scannerNum = new Scanner(System.in);
        String num = scannerNum.nextLine();

        this.affichageProduits(num);

        System.out.println("\n\n Quelle produit désirez vous vendre ? Entrez l'identifiant du produit.");
        int IdProduit = scannerNum.nextInt();
        CreerVente(IdProduit, num);
    }



















    public boolean verifieProduit(String mail) throws SQLException {

        


        /* System.out.println("Quel produit voulez-vous acheter ?");
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
        } */

        return true;
    }

    public boolean IsRevocable(int IdVente) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT REVOCABLE FROM VENTE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        while(res.next()) {
            int rev = res.getInt(1);
            if(rev ==0 ){
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean IsMontante(int IdVente) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT MONTANTE FROM VENTE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        while(res.next()) {
            int montante = res.getInt(1);
            if(montante ==0 ){
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean IsOffreMultiple(int IdVente) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT MONTANTE FROM VENTE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        while(res.next()) {
            int multiple = res.getInt(1);
            if(multiple ==0 ){
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean IsDureeLimitee(int IdVente) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT IDVENTE FROM VENTEDUREEILLIMITEE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        while(res.next() && res.getInt(1) >=0 ) {
            return true;
        }
        return false;
    }

    public void enchere(String produit, String mail) throws SQLException {
        System.out.println("----------------------------------------------------");
        System.out.println("Bienvenue dans l'enchère du produit correspondant : " + produit);
        String rev;
        String montante;
        String lim;

        if(IsMontante(getIdVente(produit))) {
            montante = "montante";
        }
        else {
            montante = "non montante";
        }
        if(IsDureeLimitee(getIdVente(produit))) {
            lim = "à durée limitée";
        }
        else {
            lim = "à durée illimitée";
        }

        System.out.println("La vente est "+ montante+" et "+ lim +", est-ce que cela vous convient? (oui/non) :" );
        Scanner scanner = new Scanner(System.in);
        String rep = scanner.next();
        if (rep.equals("non")){
            //
        }
        else {
            PreparedStatement statementPrix = conn.prepareStatement("SELECT STOCK FROM Produit WHERE NOMPRODUIT = ?");
            statementPrix.setString(1, produit);
            ResultSet res = statementPrix.executeQuery();
            while (res.next()) {
                PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM OFFRE, VENTE WHERE OFFRE.IDVENTE=VENTE.IDVENTE GROUP BY ? ");
                statementOffreMax.setString(1, "" + getIdProduit(produit));
                ResultSet resOffreMax = statementOffreMax.executeQuery();
                while (resOffreMax.next()) {
                    float offreMax = resOffreMax.getFloat(1);
                    int stock = res.getInt(1);
                    System.out.println("Il y a " + stock + " " + produit + ", le prix de la dernière enchère est de : " + offreMax + " euros");
                    // Vérification suivant si l'offre est montante ou pas

                    System.out.println("Quelle est votre offre (en euros) ? ");
                    Scanner scanOffre = new Scanner(System.in);
                    float offre = scanOffre.nextFloat();
                    if(IsMontante(getIdVente(produit))) {
                        while( offre <= offreMax){
                            System.out.println("L'offre est montante, vous ne pouvez pas réaliser une offre strictement supérieure au prix de la dernière offre");
                            System.out.println("Quelle est votre offre (en euros) ? ");
                            scanOffre = new Scanner(System.in);
                            offre = scanOffre.nextFloat();
                        }
                    }
                    else {
                        while( offre >= offreMax){
                            System.out.println("L'offre est descendante, vous ne pouvez pas réaliser une offre strictement inférieure au prix de la dernière offre");
                            System.out.println("Quelle est votre offre (en euros) ? ");
                            scanOffre = new Scanner(System.in);
                            offre = scanOffre.nextFloat();
                        }
                    }

                    // Vérification qu'il y a assez de produits en stock
                    System.out.println("Combien voulez-vous en acheter ? ");
                    Scanner scanQuantite = new Scanner(System.in);
                    int quantite = scanQuantite.nextInt();
                    while (quantite > stock) {
                        System.out.println("Il n'y a pas assez de produits en stock");
                        System.out.println("Combien voulez-vous en acheter ? ");
                        scanQuantite = new Scanner(System.in);
                        quantite = scanQuantite.nextInt();
                    }
                    ajouteOffre(getIdProduit(produit), mail, offre, quantite);
                    System.out.println("Enchère effectuée");
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

    public void setEmail(String mail){
        this.currMail = mail;
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


    public int getIdVente(String produit) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT V.IDVENTE FROM PRODUIT P,VENTE V WHERE P.IDPRODUIT = V.IDPRODUIT AND P.NOMPRODUIT = ?");
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
