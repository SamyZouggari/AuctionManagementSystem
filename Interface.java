import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
        System.out.println("Combien voulez-vous en vendre ?");
        int quantite = scan.nextInt();
        System.out.println("Désireriez-vous que cette vente soit révocable ou non ? Répondez par OUI ou NON.");
        Scanner scanRev = new Scanner(System.in);
        String revocable = scanRev.nextLine();
        int revocableInt = 0;
        if (revocable.equals("OUI")) {
            revocableInt = 1;
        }
        else if (revocable.equals("NON")) {
            revocableInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit montante ou descendante ? Répondez par MONTANTE ou DESCENDANTE.");
        Scanner scanMon = new Scanner(System.in);
        String montante = scanMon.nextLine();
        int montanteInt = 1;
        if (montante.equals("MONTANTE")) {
            montanteInt = 1;
        } else if (montante.equals("DESCENDANTE")) {
            montanteInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit à offres multiples ou non ? Répondez par OUI ou NON.");
        Scanner scanMult = new Scanner(System.in);
        String multiple = scanMult.nextLine();
        scan.nextLine();
        int multipleInt = 1;
        if (multiple.equals("OUI")) {
            multipleInt = 1;
        } else if (multiple.equals("NON")) {
            multipleInt = 0;
        }
        System.out.println("Désireriez-vous que cette vente soit à durée limitée ou non ? Répondez par OUI  ou NON. ");
        Scanner scanLim = new Scanner(System.in);
        String limité = scanLim.nextLine();
        PreparedStatement statement1 = conn.prepareStatement("INSERT INTO Vente (IdVente, PrixDepart, Revocable, Montante, OffreMultiple, IdProduit, IdSalle) VALUES (?,?,?,?,?,?,?,?)");
        statement1.setInt(1, idVente);
        statement1.setFloat(2, prixDeDepart);
        statement1.setInt(3, revocableInt);
        statement1.setInt(4, montanteInt);
        statement1.setInt(5, multipleInt);
        statement1.setInt(6, idProduit);
        statement1.setInt(7, idSalle);
        statement1.setTimestamp(8,getDateActuelle());
        statement1.executeUpdate();
        if (limité.equals("OUI")) {
            System.out.println("Entrez la date et l'heure de fin sous forme AAAA-MM-JJ HH:MI:SS");
            Scanner scan6 = new Scanner(System.in);
            String DateHeureFin = scan6.nextLine();
            System.out.println(DateHeureFin);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeLimitee (IdVente, DateHeureFin) VALUES (?,TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))");
            statement.setInt(1, idVente);
            statement.setString(2, DateHeureFin);
            statement.executeUpdate();
        } else if (limité.equals("NON")) {
            int delai = 10;
            PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeIllimitee (IdVente, Delai) VALUES (?,?)");
            statement.setInt(1, idVente);
            statement.setInt(2, delai);
            statement.executeUpdate();
        }
        incrementeStock(idProduit, quantite);
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
        // Créer une instance de Calendar et définir la date initiale
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateHeure);

        // Ajouter le délai en minutes
        cal.add(Calendar.MINUTE, delai);

        // Retourner le nouveau Timestamp
        return new Timestamp(cal.getTimeInMillis());
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
        // À durée limitée
        PreparedStatement statement1 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vd.DateHeureFin FROM Vente v JOIN VenteDureeLimitee vd on v.idvente = vd.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement1.setInt(1, IdSalle);
        ResultSet res = statement1.executeQuery();

        this.clearScreen();

        this.header("VENTES DANS LA SALLE N°" + IdSalle);
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
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateFinSansMilli = formatter.format(dateFin);
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix+ " €. Date de fin de vente : "+ dateFinSansMilli);
            }
        }

        // À durée illimitée
        PreparedStatement statement2 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vi.delai FROM Vente v JOIN VenteDureeIllimitee vi on v.idvente = vi.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement2.setInt(1, IdSalle);
        ResultSet res2 = statement2.executeQuery();

        while (res2.next()) {

            int curr_vente = res2.getInt(1);
            String curr_nom = res2.getString(2);
            int delai = res2.getInt(4);
            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res3 =  statementOffreMax.executeQuery();
            while(res3.next()){
                Timestamp DateHeureDerniereOffre = getDateHeureDerniereOffre(curr_vente);
                if(DateHeureDerniereOffre == null){
                    DateHeureDerniereOffre = getDateActuelle();
                }
                Timestamp dateFin = ajouteDelai(DateHeureDerniereOffre, delai);
                float prix = res2.getFloat(1);
                if(prix == 0.0){
                    prix = res2.getFloat(3);
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateFinSansMilli = formatter.format(dateFin);
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix+ " €. Date de fin de vente : "+ dateFinSansMilli);
            }
        }
    }
    public void process_acheteur(String mail) throws SQLException{
        this.affichageSalles();

        System.out.println("\n\nDans quelle salle désirez vous vous rendre ? ");

        Scanner scannerNum = new Scanner(System.in);
        int num = scannerNum.nextInt();

        this.affichageVentes(num);

        System.out.println("\n\nSur quelle vente voulez-vous enchérir ?");
        Scanner scannerVente = new Scanner(System.in);
        int idVente = scannerNum.nextInt();

        enchere(idVente, mail);
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
        PreparedStatement statementPrix = conn.prepareStatement("SELECT IDVENTE FROM VENTEDUREELIMITEE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        if(res.next() && res.getInt(1) >=0 ) {
            return true;
        }
        return false;
    }

    public void enchere(int idVente, String mail) throws SQLException {
        System.out.println("----------------------------------------------------");
        PreparedStatement statementProduit = conn.prepareStatement("SELECT NomProduit FROM Produit JOIN Vente on Vente.idProduit=Produit.idProduit WHERE Vente.idVente = ?");
        statementProduit.setInt(1, idVente);
        ResultSet resProduit = statementProduit.executeQuery();
        String produit="";
        if(resProduit.next()) {
            produit = resProduit.getString(1);
        }

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

        System.out.println("La vente est "+ montante+", est-ce que cela vous convient? (oui/non) :" );
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
                // cas où il y a déjà des offres
                PreparedStatement statementOffreMax = conn.prepareStatement("SELECT VENTE.PRIXDEPART, COALESCE(MAX(OFFRE.PrixAchat),0) FROM VENTE LEFT JOIN OFFRE ON OFFRE.IDVENTE = VENTE.IDVENTE WHERE VENTE.IDPRODUIT = ? GROUP BY VENTE.PRIXDEPART");
                statementOffreMax.setInt(1, getIdProduit(produit));
                ResultSet resOffreMax = statementOffreMax.executeQuery();
                if (resOffreMax.next()) {
                    float offreMax = resOffreMax.getFloat(2);
                    if(offreMax==0){
                        offreMax = resOffreMax.getFloat(1);
                    }
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
                    ajouteOffre(idVente, mail, offre, quantite);
                    //decrementationStock(getIdProduit(produit),quantite);
                    System.out.println("Enchère effectuée");
                }
            }
        }
    }

    // retourne true si timestamp2 > timestamp 1
    public boolean compareTemps(Timestamp timestamp1, Timestamp timestamp2 ) {
        return timestamp1.after(timestamp2);
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
        Timestamp dateActuelle = getDateActuelle();
        PreparedStatement statementDateHeureOffre = conn.prepareStatement("INSERT INTO DateHeureOffre VALUES(?)");
        statementDateHeureOffre.setTimestamp(1,dateActuelle);
        statementDateHeureOffre.executeUpdate();
        PreparedStatement statementPrix = conn.prepareStatement("INSERT INTO OFFRE VALUES(?,?,?,?,?)");
        statementPrix.setInt(1,IdVente);
        statementPrix.setString(2, eMail);
        statementPrix.setFloat(3, PrixAchat);
        statementPrix.setInt(4, quantite);
        statementPrix.setTimestamp(5, dateActuelle);

        statementPrix.executeUpdate();
    }

    public void suppressionProduit(int idProduit) throws SQLException {
        suppressionAllOffresProduit(idProduit);
        PreparedStatement statementOffre = conn.prepareStatement("SELECT IDVENTE FROM Vente WHERE idProduit = ?");
        statementOffre.setInt(1, idProduit);
        ResultSet res= statementOffre.executeQuery();
        while(res.next()){
            int idv = res.getInt(1);
            suppressionVente(idv);
        }
        PreparedStatement statementProduit = conn.prepareStatement("DELETE FROM Produit WHERE idProduit = ?");
        statementProduit.setString(1, Integer.toString(idProduit));
        statementProduit.executeQuery();
    }


    public void suppressionVente(int idVente) throws SQLException {
        boolean lim = IsDureeLimitee(idVente);
        PreparedStatement statementVentelim;
        if(lim){
            statementVentelim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
        }
        else {
            statementVentelim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
        }
        statementVentelim.setInt(1, idVente);
        statementVentelim.executeUpdate();

        PreparedStatement statementVente = conn.prepareStatement("DELETE FROM Vente WHERE idVente = ?");
        statementVente.setInt(1, idVente);
        statementVente.executeUpdate();
    }

    /*
     * Methode pour supprimer toutes les offres qui ont ete effectuees sur un produit à partir de l'id de la vente */
    public void suppressionAllOffres(int idVente) throws SQLException {
        PreparedStatement statementOffre = conn.prepareStatement("DELETE FROM Offre WHERE IdVente = ?");
        statementOffre.setInt(1, idVente);
        statementOffre.executeUpdate();
    }

    public void suppressionAllOffresProduit(int idProduit) throws SQLException {
        PreparedStatement statementOffre = conn.prepareStatement("SELECT IDVENTE FROM Vente WHERE idProduit = ?");
        statementOffre.setInt(1, idProduit);
        ResultSet res= statementOffre.executeQuery();
        while(res.next()){
            int idp = res.getInt(1);
            suppressionAllOffres(idp);
        }
    }

    /*
     * Methode pour supprimer une offre spécifique, a partir du nom du produit et du mail de la personne
     * ayant fait l'enchere */
    public void suppressionOffre(String nomProduit, String mail) throws SQLException {
        PreparedStatement statementProduit = conn.prepareStatement("SELECT v.idVente FROM Vente v, Produit p WHERE p.idProduit = v.idProduit and p.NomProduit = ?");
        statementProduit.setString(1, nomProduit);
        ResultSet res = statementProduit.executeQuery();
        while (res.next()) {
            PreparedStatement statementOffre = conn.prepareStatement("DELETE FROM Offre WHERE idVente = ? and Email = ?");
            statementOffre.setInt(1, res.getInt(1));
            statementOffre.setString(2, mail);
            statementOffre.executeUpdate();
        }
    }

    /*On va l'appeler quand qqn met en vente un produit */
    public void incrementeStock(int idp, int quantite) throws SQLException {
        PreparedStatement statementStock = conn.prepareStatement("SELECT Stock FROM Produit WHERE IDPRODUIT = ?");
        statementStock.setInt(1, idp);
        ResultSet res = statementStock.executeQuery();
        int stock=0;

        if (res.next()) {
            stock = res.getInt(1);
        }

        PreparedStatement incr = conn.prepareStatement("UPDATE Produit SET Stock = ? WHERE IDPRODUIT = ?");
        incr.setInt(1, quantite+stock);
        incr.setInt(2, idp);
        incr.executeQuery();
    }

    /* Methode qui va etre appelee à la fin d'une enchere pour decrementer le stock d'un produit
    * est le supprimer si ce stock passe à 0*/
    public void decrementationStock(int idProduit, int quantiteProduit) throws SQLException {
        // On part du principe que l'offre s'est terminee, qu'on a deja verifie que stock > quantiteProduit
        PreparedStatement statementStock = conn.prepareStatement("SELECT p.Stock FROM Produit p WHERE p.idProduit = ?");
        statementStock.setInt(1,idProduit);
        ResultSet res = statementStock.executeQuery();
        while (res.next()) {
            int stockRestant = res.getInt(1);
            if (stockRestant - quantiteProduit == 0) {
                suppressionProduit(idProduit);
            } else {
                // S'il reste encore du stock à la fin de l'achat, on décrémente simplement le stock
                PreparedStatement statementAlter = conn.prepareStatement("UPDATE Produit SET Stock = ? WHERE Produit.idProduit = ?");
                statementAlter.setInt(1,stockRestant - quantiteProduit);
                statementAlter.setInt(2,idProduit);
                statementAlter.executeUpdate();
            }
        }
    }

    /* Méthode qui va être appelée à chaque connection, et qui va verifier si des ventes se sont
    * terminées depuis la dernière connection, et en fonction va supprimer ces ventes, offres associées
    * et les produits vendus.
    * Attention si la vente était révocable et que le prix d'achat de la dernière offre est inférieur
    * au prix de revient, la vente et l'offre sont supprimées, mais pas le produit, on le sort simplement
    * de la salle de vente */
    public void updateBD() throws SQLException {
        Timestamp actualDate = getDateActuelle();
        Statement checkOffres = conn.createStatement();
        // On commence par gérer le cas des ventes à durée limitée
        ResultSet res = checkOffres.executeQuery("SELECT idVente, DateHeureFin FROM VenteDureeLimitee");
        // On a besoin de l'idVente pour aller voir si la vente associée était révocable
        while (res.next()) {
            int idVente = res.getInt(1); // On récupère l'idVente
            Timestamp dateHeureFinVente = res.getTimestamp(2); // On récupère la date

            //On vérifie que l'offre n'est pas terminée
            if (!compareTemps(dateHeureFinVente,actualDate)) {
                // Si elle est terminée alors, on regarde si la vente associée était révocable
                PreparedStatement statementRevocable = conn.prepareStatement("SELECT Vente.Revocable, Vente.idProduit FROM Vente WHERE idVente = ?");
                statementRevocable.setInt(1,idVente);
                ResultSet res2 = statementRevocable.executeQuery();
                while (res2.next()) {
                    int revoc = res2.getInt(1); // On va voir si la vente était révocable
                    int idProduit = res2.getInt(2); // On récupère l'idProduit du produit vendu
                    switch (revoc) {
                        case 0: // Si la vente n'était pas révocable on supprime la vente, le produit vendu et les offres
                            PreparedStatement statementProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                            statementProduit.setInt(1,idVente);
                            ResultSet res3 = statementProduit.executeQuery();
                                if (res3.next()) {
                                    int quantiteProd = res3.getInt(1);

                                    // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                    // On supprime les offres.
                                    suppressionAllOffres(idVente);
                                    // On supprime les ventes à durée Limité
                                    PreparedStatement statementSupprVenteLim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                    statementSupprVenteLim.setInt(1, idVente);
                                    statementSupprVenteLim.executeUpdate();
                                    // On supprime la vente
                                    suppressionVente(idVente);
                                    // On supprime le produit en faisant appel a la methode decrementationStock
                                    decrementationStock(idProduit, quantiteProd);
                                }
                            else {
                                PreparedStatement statementSupprVenteLim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                statementSupprVenteLim.setInt(1, idVente);
                                statementSupprVenteLim.executeUpdate();
                                // On supprime la vente
                                suppressionVente(idVente);
                            }
                            break;
                        case 1: // Si la vente était révocable On doit supprimer la vente et les offres si le prix d'achat
                            // est inférieur au prix de revient, mais on ne supprime pas le produit,
                            // Il faut réussir à sortir le produit de la salle de vente dans laquelle il est
                            PreparedStatement statementQtProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                            statementQtProduit.setInt(1,idVente);
                            ResultSet res4 = statementQtProduit.executeQuery();
                            PreparedStatement selectPrixRevient = conn.prepareStatement("SELECT Produit.PrixDeRevient FROM Produit WHERE idProduit = ?");
                            selectPrixRevient.setInt(1,idProduit);
                            ResultSet prixRevient = selectPrixRevient.executeQuery();

                            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT VENTE.PRIXDEPART, COALESCE(MAX(OFFRE.PrixAchat),0) FROM VENTE LEFT JOIN OFFRE ON OFFRE.IDVENTE = VENTE.IDVENTE WHERE VENTE.IDPRODUIT = ? GROUP BY VENTE.PRIXDEPART");
                            statementOffreMax.setInt(1, idProduit);
                            ResultSet resOffreMax = statementOffreMax.executeQuery();
                            if (resOffreMax.next()) {
                                float offreMax = resOffreMax.getFloat(2);
                                if (offreMax == 0) {
                                    offreMax = resOffreMax.getFloat(1);
                                }
                                if (prixRevient.next()) {
                                    float prixRevientFloat = prixRevient.getFloat(1);
                                    if (offreMax >= prixRevientFloat) {
                                        // Si le vendeur va gagner de l'argent on peut effectuer la vente
                                        // On commence par supprimer l'offre
                                        suppressionAllOffres(idVente);
                                        // On supprime ensuite les ventes à durée illimitée
                                        PreparedStatement statementSupprVenteIllim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                        statementSupprVenteIllim.setInt(1, idVente);
                                        statementSupprVenteIllim.executeUpdate();
                                        // On supprime ensuite la vente associée à l'offre
                                        suppressionVente(idVente);
                                        // On decrémente enfin le produit
                                        if (res4.next()) {
                                            int quantiteProd = res4.getInt(1);
                                            decrementationStock(idProduit, quantiteProd);
                                            // On decremente le nombre de produit vendu et c'est fini
                                        }
                                    } else {
                                        // Si le vendeur ne va pas gagner d'argent on annule son offre
                                        // On commence par supprimer les offres associées à la vente
                                        suppressionAllOffres(idVente);
                                        // On supprimme ensuite les offres Illimitée
                                        PreparedStatement statementSupprVenteIllim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                        statementSupprVenteIllim.setInt(1, idVente);
                                        statementSupprVenteIllim.executeUpdate();
                                        // On supprime ensuite la vente
                                        suppressionVente(idVente);
                                        // On doit maintenant gérer le produit
                                        // la seule solution au'on voit c'est de supprimer le produit et de le re-créer
                                        // on va d'abord recup toutes les values du produit
                                        PreparedStatement selectValeursProduit = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                        selectValeursProduit.setInt(1,idProduit);
                                        ResultSet resProd = selectValeursProduit.executeQuery();
                                        while (resProd.next()) {
                                            // On peut maintenant supprimer le produit
                                            suppressionProduit(idProduit);
                                            // On re-crée maintenant le produit
                                            PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                            creeProp.setInt(1,resProd.getInt(1));
                                            creeProp.setString(2,resProd.getString(2));
                                            creeProp.setFloat(3,resProd.getFloat(3));
                                            creeProp.setInt(4,resProd.getInt(4));
                                            creeProp.setString(5, resProd.getString(5));
                                            creeProp.setString(6, resProd.getString(6));
                                            creeProp.executeUpdate();
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        Statement checkOffres2 = conn.createStatement();
        // On gère le cas des ventes à durée illimitée
        ResultSet resIllimitee = checkOffres2.executeQuery("SELECT idVente, Delai FROM VenteDureeIllimitee");
        // On a besoin de l'idVente pour aller voir si la vente associée était révocable
        while (resIllimitee.next()) {
            int idVente = resIllimitee.getInt(1); // On récupère l'idVente
            int delai = resIllimitee.getInt(2); // On récupère la date
            Timestamp DateHeureDerniereOffre = getDateHeureDerniereOffre(idVente);
            if(DateHeureDerniereOffre == null){
                break;
            }
            Timestamp dateHeureFinVente = ajouteDelai(DateHeureDerniereOffre, delai);
            //On vérifie que l'offre n'est pas terminée
            if (!compareTemps(dateHeureFinVente,actualDate)) {
                // Si elle est terminée alors, on regarde si la vente associée était révocable
                PreparedStatement statementRevocable = conn.prepareStatement("SELECT Vente.Revocable, Vente.idProduit FROM Vente WHERE idVente = ?");
                statementRevocable.setInt(1,idVente);
                ResultSet res2 = statementRevocable.executeQuery();
                while (res2.next()) {
                    int revoc = res2.getInt(1); // On va voir si la vente était révocable
                    int idProduit = res2.getInt(2); // On récupère l'idProduit du produit vendu
                    switch (revoc) {
                        case 0: // Si la vente n'était pas révocable on supprime la vente, le produit vendu et les offres
                            PreparedStatement statementProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                            statementProduit.setInt(1,idVente);
                            ResultSet res3 = statementProduit.executeQuery();
                            if (res3.next()) {
                                int quantiteProd = res3.getInt(1);

                                // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                // On supprime les offres.
                                suppressionAllOffres(idVente);
                                // On supprime les ventes à durée illimitée
                                PreparedStatement statementSupprVenteLim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                statementSupprVenteLim.setInt(1,idVente);
                                statementSupprVenteLim.executeUpdate();
                                // On supprime la vente
                                suppressionVente(idVente);
                                // On supprime le produit en faisant appel a la methode decrementationStock
                                decrementationStock(idProduit,quantiteProd);
                            }
                            else {
                                PreparedStatement statementSupprVenteLim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                statementSupprVenteLim.setInt(1,idVente);
                                statementSupprVenteLim.executeUpdate();
                                // On supprime la vente
                                suppressionVente(idVente);
                            }
                            break;
                        case 1: // Si la vente était révocable On doit supprimer la vente et les offres si le prix d'achat
                            // est inférieur au prix de revient, mais on ne supprime pas le produit,
                            // Il faut réussir à sortir le produit de la salle de vente dans laquelle il est
                            PreparedStatement statementQtProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                            statementQtProduit.setInt(1,idVente);
                            ResultSet res4 = statementQtProduit.executeQuery();
                            PreparedStatement selectPrixRevient = conn.prepareStatement("SELECT Produit.PrixDeRevient FROM Produit WHERE idProduit = ?");
                            selectPrixRevient.setInt(1,idProduit);
                            ResultSet prixRevient = selectPrixRevient.executeQuery();
                            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT VENTE.PRIXDEPART, COALESCE(MAX(OFFRE.PrixAchat),0) FROM VENTE LEFT JOIN OFFRE ON OFFRE.IDVENTE = VENTE.IDVENTE WHERE VENTE.IDPRODUIT = ? GROUP BY VENTE.PRIXDEPART");
                            statementOffreMax.setInt(1, idProduit);
                            ResultSet resOffreMax = statementOffreMax.executeQuery();
                            if (resOffreMax.next()) {
                                float offreMax = resOffreMax.getFloat(2);
                                if (offreMax == 0) {
                                    offreMax = resOffreMax.getFloat(1);
                                }
                                if (prixRevient.next()) {
                                    float prixRevientFloat = prixRevient.getFloat(1);
                                    if (offreMax >= prixRevientFloat) {
                                        // Si le vendeur va gagner de l'argent on peut effectuer la vente
                                        // On commence par supprimer l'offre
                                        suppressionAllOffres(idVente);
                                        // On supprime ensuite les ventes à durée illimitée
                                        PreparedStatement statementSupprVenteIllim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                        statementSupprVenteIllim.setInt(1, idVente);
                                        statementSupprVenteIllim.executeUpdate();
                                        // On supprime ensuite la vente associée à l'offre
                                        suppressionVente(idVente);
                                        // On decrémente enfin le produit

                                        if (res4.next()) {
                                            int quantiteProd = res4.getInt(1);
                                            decrementationStock(idProduit, quantiteProd);
                                            // On decremente le nombre de produit vendu et c'est fini
                                        }
                                    } else {
                                        // Si le vendeur ne va pas gagner d'argent on annule son offre
                                        // On commence par supprimer les offres associées à la vente
                                        suppressionAllOffres(idVente);
                                        // On supprimme ensuite les offres Illimitée
                                        PreparedStatement statementSupprVenteIllim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                        statementSupprVenteIllim.setInt(1, idVente);
                                        statementSupprVenteIllim.executeUpdate();
                                        // On supprime ensuite la vente
                                        suppressionVente(idVente);
                                        // On doit maintenant gérer le produit
                                        // la seule solution au'on voit c'est de supprimer le produit et de le re-créer
                                        // on va d'abord recup toutes les values du produit
                                        PreparedStatement selectValeursProduit = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Sotck,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                        selectValeursProduit.setInt(1,idProduit);
                                        ResultSet resProd = selectValeursProduit.executeQuery();
                                        while (resProd.next()) {
                                            // On peut maintenant supprimer le produit
                                            suppressionProduit(idProduit);
                                            // On re-crée maintenant le produit
                                            PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                            creeProp.setInt(1,resProd.getInt(1));
                                            creeProp.setString(2,resProd.getString(2));
                                            creeProp.setFloat(3,resProd.getFloat(3));
                                            creeProp.setInt(4,resProd.getInt(4));
                                            creeProp.setString(5, resProd.getString(5));
                                            creeProp.setString(6, resProd.getString(6));
                                            creeProp.executeUpdate();
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        System.out.println("Base de données mise à jour");
    }
}
