import java.sql.*;
import java.util.*;

import objets.*;
import oracle.jdbc.driver.OracleDriver;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;


public class Interface {

    String CONN_URL = "jdbc:oracle:thin:@oracle1.ensimag.fr:1521:oracle1";
    String USER = "zouggars";
    String PASSWD = "zouggars";
    Connection conn;
    int compteurIdVente;
    //ArrayList<Pair<String, String>> salles;
    int compteurIdProduit;
    String currMail;


    public Interface() throws SQLException{
        try {
            this.conn = connexion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(IdVente) FROM Vente");
        if (rs.next()) {
            compteurIdVente = rs.getInt(1) ; // Initialise à MAX
        } else {
            compteurIdVente = 0; // Si la table est vide
        }
        this.compteurIdVente = compteurIdVente;
    }

    public int getCompteurIdVente() {
        return this.compteurIdVente;
    }

    public void incrCompteurIdVente() {
        this.compteurIdVente++;
    }

    public int getCompteurIdProduit() {
        return this.compteurIdProduit;
    }

    public void incrCompteurIdProduit() {
        this.compteurIdProduit += 1;
    }

//    public void OuvrirSalle() {
//        System.out.println("De quelle catégorie sont les produits que vous aimereriez vendre dans cette salle ?");
//        Scanner scan = new Scanner(System.in);
//        String categorie = scan.next();
//        scan.nextLine();
//        List new_list = new ArrayList<Vente>();
//        Salle nouvelleSalle = new Salle(new_list, categorie);
//    }

    public void OuvrirSalle() {
        // catégorie qu'il souhaite vendre
        System.out.println("De quelle catégorie sont les produits que vous aimeriez vendre dans cette salle ?");
        Scanner scan = new Scanner(System.in);
        String categorie = scan.nextLine();

        // Vérifier si des salles existent déjà pour cette catégorie
        try {
            if (!sallesExistantesPourCategorie(categorie)) {
                System.out.println("Aucune salle disponible pour cette catégorie. Voulez-vous en créer une nouvelle ?");
                System.out.println("Répondez par OUI pour créer une nouvelle salle ou NON pour annuler.");
                String reponse = scan.nextLine();

                if (reponse.equals("OUI")) {
                    // Création d'une salle
                    int idNouvelleSalle = creerNouvelleSalle();
                    ajouterSalleCategorie(idNouvelleSalle, categorie);

                    System.out.println("Nouvelle salle créée avec succès avec l'ID " + idNouvelleSalle);
                } else {
                    System.out.println("Aucune salle n'a été créée.");
                }
            } else {
                System.out.println("Voici les salles disponibles.");
                this.affichageSalles(categorie);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification des salles : " + e.getMessage());
        }
    }


    public boolean sallesExistantesPourCategorie(String categorie)throws SQLException{
        PreparedStatement statement = conn.prepareStatement("SELECT COUNT FROM Propose WHERE NomCategorie = ?");
        statement.setString(1, categorie);
        ResultSet res = statement.executeQuery();

        if (res.next()) {
            return res.getInt(1) > 0; // Retourne true si des salles existent
        }
        return false;
    }


    public int creerNouvelleSalle() throws  SQLException{
        PreparedStatement maxIdSalle = conn.prepareStatement("SELECT MAX(IdSalle) FROM SalleDeVente");
        ResultSet rs = maxIdSalle.executeQuery();

        int newIdSalle = 1;
        if (rs.next()) {
            newIdSalle = rs.getInt(1) + 1;
        }

        PreparedStatement statement = conn.prepareStatement("INSERT INTO SalleDeVente(IdSalle) VALUES (?)");
        statement.setInt(1, newIdSalle);
        int res = statement.executeUpdate();

        if (res == 1) {
            return 1; // Retourne 1 si des salles existent
        }
        else{
            throw new SQLException("Erreur lors de la génération de la salle");
        }
    };

    public void ajouterSalleCategorie(int idNouvelleSalle, String categorie)throws SQLException{
        PreparedStatement statement = conn.prepareStatement("INSERT INTO Propose(NomCategorie, IdSalle) VALUES (?;?)");
        statement.setString(1, categorie);
        statement.setInt(2, idNouvelleSalle);
        statement.executeUpdate();
    };
    
    public void CreerVente(int idProduit, String categorie) throws SQLException {
        incrCompteurIdVente();
        int idVente = getCompteurIdVente();
        System.out.println("Vente " + idVente);
        Scanner scan = new Scanner(System.in);
        System.out.println("A quel prix de départ voulez-vous commencez cette vente ?");
        Float prixDeDepart = scan.nextFloat();
        this.affichageSalles(categorie);
        System.out.println("\n"+ "Voici les salles dans lesquelles vous pouvez vendre votre produit. Entrez l'ID de la salle dans laquelle vous souhaitez vendre votre produit.");
        //affiche les salles possibles, l'utilisateur rentre un numero de salle
        //besoin d'ajouter la verification que la salle choisie ait la même catégorie que le produit

        int idSalle = scan.nextInt();
        scan.nextLine();
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
        System.out.println("Désireriez-vous que cette vente soit limitée ou non ? Répondez par OUI  ou NON. ");
        String limité = scan.nextLine();
        PreparedStatement statement1 = conn.prepareStatement("INSERT INTO Vente (IdVente, PrixDepart, Revocable, Montante, OffreMultiple, IdProduit, IdSalle) VALUES (?,?,?,?,?,?, ?)");
        statement1.setInt(1, idVente);
        statement1.setFloat(2, prixDeDepart);
        statement1.setInt(3, revocableInt);
        statement1.setInt(4, montanteInt);
        statement1.setInt(5, multipleInt);
        statement1.setInt(6, idProduit);
        statement1.setInt(7, idSalle);
        statement1.executeUpdate();
        if (limité.equals("OUI")) {
            System.out.println("Si oui, entrez la date et l'heure de fin sous forme AAAA-MM-JJ HH:MI:SS");
            Scanner scan6 = new Scanner(System.in);
            String DateHeureFin = scan6.nextLine();
            System.out.println(DateHeureFin);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeLimitee (IdVente, DateHeureFin) VALUES (?,TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))");
            statement.setInt(1, idVente);
            statement.setString(2, DateHeureFin);
            statement.executeUpdate();
        } else if (limité.equals("NON")) {
            int delai = 10;
            PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeIllimitee (IdVente, DateHeureFin) VALUES (?,?)");
            statement.setInt(1, idVente);
            statement.setInt(2, delai);
            statement.executeUpdate();
        }

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

    public Timestamp getDateHeureDerniereOffre(int IdVente) throws SQLException {
        PreparedStatement statementDerniereOffre = conn.prepareStatement("SELECT DateHeure FROM Offre WHERE IdVente = ? ");
        statementDerniereOffre.setInt(1, IdVente);
        ResultSet res3 = statementDerniereOffre.executeQuery();
        while (res3.next()) {
            return res3.getTimestamp(1);
        }
        return null;
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


    public Timestamp ajouteDelai(Timestamp dateHeure, int delai) {
        if (dateHeure == null) {
            throw new IllegalArgumentException("La date ne peut pas être null");
        }
        return dateHeure;
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
        // a durée limitée
        PreparedStatement statement1 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vd.DateHeureFin FROM Vente v JOIN VenteDureeLimitee vd on v.idvente = vd.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement1.setInt(1, IdSalle);
        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("VENTES DANS LA SALLE N°" + IdSalle);
        System.out.println("Ventes à durée limitée : ");
        while (res.next()) {

            int curr_vente = res.getInt(1);
            String curr_nom = res.getString(2);
            Timestamp dateFin = res.getTimestamp(4);

            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res2 =  statementOffreMax.executeQuery();


            while(res2.next()){
                float prix = res2.getFloat(1);
                if(prix == 0.0){
                    prix = res.getFloat(3);
                }
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix+ ". Date de fin de vente : "+ dateFin);
            }
        }

        // A durée illimitée
        System.out.println("----------------------------------------------");
        System.out.println("Ventes à durée illimitée : ");
        PreparedStatement statement2 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vi.delai FROM Vente v JOIN VenteDureeIllimitee vi on v.idvente = vi.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement2.setInt(1, IdSalle);
        ResultSet res2 = statement2.executeQuery();

        while (res2.next()) {

            int curr_vente = res2.getInt(1);
            String curr_nom = res2.getString(2);
            int delai = res2.getInt(4);
            Timestamp DateHeureDerniereOffre = getDateHeureDerniereOffre(curr_vente);
            Timestamp dateFin = ajouteDelai(DateHeureDerniereOffre, delai);

            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res3 =  statementOffreMax.executeQuery();
            while(res3.next()){
                float prix = res2.getFloat(1);
                if(prix == 0.0){
                    prix = res2.getFloat(3);
                }
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix+ ". Date de fin de vente : "+ dateFin);
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

    // retourne true si timestamp2 > timestamp 1
    public boolean compareTemps(Timestamp timestamp1, Timestamp timestamp2 ) {
        return (Math.abs(timestamp2.getTime() - timestamp1.getTime()) > 0);
    }

    public static String compareTimestamps(Timestamp timestamp1, Timestamp timestamp2) {
        // Calcul de la différence absolue en millisecondes
        long diffInMillis = Math.abs(timestamp2.getTime() - timestamp1.getTime());

        // Conversion en jours, heures, minutes et secondes
        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;

        // Retourner la différence sous forme de chaîne
        return String.format("%d jours, %d heures, %d minutes, %d secondes", days, hours, minutes);
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
