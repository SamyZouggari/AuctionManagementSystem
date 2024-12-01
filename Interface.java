import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import objets.*;
import oracle.jdbc.driver.OracleDriver;

import javax.print.DocFlavor;

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

    public Interface() throws SQLException {
        try {
            this.conn = connexion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(IdVente) FROM Vente");
        if (rs.next()) {
            compteurIdVente = rs.getInt(1); // Initialise à MAX
        } else {
            compteurIdVente = 0; // Si la table est vide
        }
        rs.close();
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


    public void OuvrirSalle(String categorie) {
        // Vérifier si des salles existent déjà pour cette catégorie
        try {
            if (!sallesExistantesPourCategorie(categorie)) {
                System.out.println("Aucune salle disponible pour cette catégorie. Voulez-vous en créer une nouvelle ?");
                System.out.println("Répondez par OUI pour créer une nouvelle salle ou NON pour annuler.");
                Scanner scan = new Scanner(System.in);
                String reponse = scan.nextLine();

                if (reponse.equals("OUI")) {
                    // Création d'une salle
                    int idNouvelleSalle = creerNouvelleSalle();
                    ajouterSalleCategorie(idNouvelleSalle, categorie);

                    System.out.println("Nouvelle salle créée avec succès avec l'ID " + idNouvelleSalle + ". Entrez cet ID.");
                } else {
                    System.out.println("Aucune salle n'a été créée.");
                }
            } else {
                System.out.println("Voici les salles disponibles. Entrez l'ID de la salle dans laquelle vous souhaitez vendre votre produit.");
                this.affichageSalles(categorie);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification des salles : " + e.getMessage());
        }
    }


    public boolean sallesExistantesPourCategorie(String categorie) throws SQLException {
        PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM Propose WHERE NomCategorie = ?");
        statement.setString(1, categorie);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
            return res.getInt(1) > 0; // Retourne true si des salles existent
        }
        statement.close();
        res.close();
        return false;
    }


    public int creerNouvelleSalle() throws SQLException {
        // Obtenir le nouvel ID de salle en incrémentant le MAX(IdSalle)
        PreparedStatement maxIdSalle = conn.prepareStatement("SELECT COALESCE(MAX(IdSalle), 0) + 1 AS NewIdSalle FROM SalleDeVente");
        ResultSet rs = maxIdSalle.executeQuery();
        int newIdSalle = 1;
        if (rs.next()) {
            newIdSalle = rs.getInt("NewIdSalle");
        }
        // Insérer la nouvelle salle
        PreparedStatement statement = conn.prepareStatement("INSERT INTO SalleDeVente(IdSalle) VALUES (?)");
        statement.setInt(1, newIdSalle);
        statement.executeUpdate();
        statement.close();
        rs.close();
        return newIdSalle;
    }

    ;

    public void ajouterSalleCategorie(int idNouvelleSalle, String categorie) throws SQLException {
        // Vérifier si la salle existe déjà dans Propose
        PreparedStatement checkStatement = conn.prepareStatement("SELECT COUNT(*) FROM Propose WHERE IdSalle = ?");
        checkStatement.setInt(1, idNouvelleSalle);
        ResultSet res = checkStatement.executeQuery();

        if (res.next() && res.getInt(1) > 0) { // il existe au moins une salle
            throw new SQLException("La salle avec l'ID " + idNouvelleSalle + " est déjà associée à une catégorie.");
        }

        // Si aucune violation, insérer dans Propose
        PreparedStatement statement = conn.prepareStatement("INSERT INTO Propose (NomCategorie, IdSalle) VALUES (?, ?)");
        statement.setString(1, categorie);
        statement.setInt(2, idNouvelleSalle);
        statement.executeUpdate();
        checkStatement.close();
        statement.close();
        res.close();
    }

    ;

    public int getStock(int idProduit) throws SQLException {
        try {
            PreparedStatement statementStock = conn.prepareStatement("SELECT Stock FROM Produit WHERE IdProduit = ? ");
            statementStock.setInt(1, idProduit);
            ResultSet res = statementStock.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
            statementStock.close();
            res.close();
        } catch (Exception e) {
            System.out.println("Produit introuvable !");
        }
        return -1;
    }

    public void CreerVente(int idProduit, String categorie) throws SQLException {
        incrCompteurIdVente();
        int idVente = getCompteurIdVente();
        System.out.println("Vente " + idVente);
        Scanner scan = new Scanner(System.in);
        System.out.println("A quel prix de départ voulez-vous commencez cette vente ?");
        while (!scan.hasNextFloat()) {
            System.out.println("Veuillez entrer un nombre valide (format décimal).");
            scan = new Scanner(System.in);
        }
        Float prixDeDepart = scan.nextFloat();
        this.OuvrirSalle(categorie);

        while (!scan.hasNextInt()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scan = new Scanner(System.in);
        }
        int idSalle = scan.nextInt();
        System.out.println("Combien voulez-vous en vendre ? Stock = " + getStock(idProduit));
        while (!scan.hasNextInt()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scan = new Scanner(System.in);
        }
        int quantite = scan.nextInt();
        while (quantite > getStock(idProduit)) {
            System.out.println("Vous ne pouvez pas vendre plus de produits qu'il n'y a en stock !");
            System.out.println("Combien voulez-vous en vendre ? Stock = " + getStock(idProduit));
            while (!scan.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre valide.");
                scan = new Scanner(System.in);
            }
            quantite = scan.nextInt();
        }
        System.out.println("Par défaut, la vente sera montante, non révocable, sans limite de temps, et à offres multiples.");
        System.out.println("Est-ce que cela vous convient ?");
        String convenance = "";
        while (!convenance.equals("OUI") && !convenance.equals("NON")) {
            System.out.println("Veuillez répondre par OUI ou par NON");
            Scanner scanConvenance = new Scanner(System.in);
            convenance = scanConvenance.nextLine();
        }
        if (convenance.equals("OUI")) {
            PreparedStatement statement1 = conn.prepareStatement("INSERT INTO Vente (IdVente, PrixDepart, Revocable, Montante, OffreMultiple, IdProduit, IdSalle, DateHeureVente, Quantite) VALUES (?,?,?,?,?,?,?,?,?)");
            statement1.setInt(1, idVente);
            statement1.setFloat(2, prixDeDepart);
            statement1.setInt(3, 0);
            statement1.setInt(4, 1);
            statement1.setInt(5, 1);
            statement1.setInt(6, idProduit);
            statement1.setInt(7, idSalle);
            statement1.setTimestamp(8, getDateActuelle());
            statement1.setInt(9, quantite);
            statement1.executeUpdate();
            decrementationStock(idProduit, quantite, false);
            statement1.close();
            int delai = 10;
            PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeIllimitee (IdVente, Delai) VALUES (?,?)");
            statement.setInt(1, idVente);
            statement.setInt(2, delai);
            statement.executeUpdate();
            statement.close();
        }
        if (convenance.equals("NON")) {
            System.out.println("Désireriez-vous que cette vente soit révocable ou non ? Répondez par OUI ou NON.");
            Scanner scanRev = new Scanner(System.in);
            String revocable = scanRev.nextLine();
            int revocableInt = 0;
            while (!revocable.equals("OUI") && !revocable.equals("NON")) {
                System.out.println("Veuillez répondre par OUI ou par NON.");
                Scanner scanRevBoucle = new Scanner(System.in);
                revocable = scanRevBoucle.nextLine();
            }
            if (revocable.equals("OUI")) {
                revocableInt = 1;
            }
            System.out.println("Désireriez-vous que cette vente soit montante ou descendante ? Répondez par MONTANTE ou DESCENDANTE.");
            Scanner scanMon = new Scanner(System.in);
            String montante = scanMon.nextLine();
            int montanteInt = 1;
            while (!montante.equals("MONTANTE") && !montante.equals("DESCENDANTE")) {
                System.out.println("Veuillez répondre par MONTANTE ou DESCENDANTE.");
                Scanner scanMonBoucle = new Scanner(System.in);
                montante = scanMonBoucle.nextLine();
            }
            if (montante.equals("MONTANTE")) {
                montanteInt = 1;
            } else if (montante.equals("DESCENDANTE")) {
                montanteInt = 0;
            }
            System.out.println("Désireriez-vous que cette vente soit à offres multiples ou non ? Répondez par OUI ou NON.");
            Scanner scanMult = new Scanner(System.in);
            String multiple = scanMult.nextLine();
            scan.nextLine();
            while (!multiple.equals("OUI") && !multiple.equals("NON")) {
                System.out.println("Veuillez répondre par OUI ou par NON.");
                Scanner scanMultBoucle = new Scanner(System.in);
                multiple = scanMultBoucle.nextLine();
            }
            int multipleInt = 1;
            if (multiple.equals("OUI")) {
                multipleInt = 1;
            } else if (multiple.equals("NON")) {
                multipleInt = 0;
            }
            System.out.println("Désireriez-vous que cette vente soit à durée limitée ou non ? Répondez par OUI  ou NON. ");
            Scanner scanLim = new Scanner(System.in);
            String limité = scanLim.nextLine();
            while (!limité.equals("OUI") && !limité.equals("NON")) {
                System.out.println("Veuillez répondre par OUI ou par NON.");
                Scanner scanLimBoucle = new Scanner(System.in);
                limité = scanLimBoucle.nextLine();
            }
            PreparedStatement statement1 = conn.prepareStatement("INSERT INTO Vente (IdVente, PrixDepart, Revocable, Montante, OffreMultiple, IdProduit, IdSalle, DateHeureVente, Quantite) VALUES (?,?,?,?,?,?,?,?,?)");
            statement1.setInt(1, idVente);
            statement1.setFloat(2, prixDeDepart);
            statement1.setInt(3, revocableInt);
            statement1.setInt(4, montanteInt);
            statement1.setInt(5, multipleInt);
            statement1.setInt(6, idProduit);
            statement1.setInt(7, idSalle);
            statement1.setTimestamp(8, getDateActuelle());
            statement1.setInt(9, quantite);
            statement1.executeUpdate();
            statement1.close();
            if (limité.equals("OUI")) {
                boolean success = false;
                while (!success) {
                    try {
                        System.out.println("Entrez la date et l'heure de fin sous forme AAAA-MM-JJ HH:MI:SS");
                        Scanner scan6 = new Scanner(System.in);
                        String DateHeureFin = scan6.nextLine();
                        System.out.println(DateHeureFin);
                        PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeLimitee (IdVente, DateHeureFin) VALUES (?,TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))");
                        statement.setInt(1, idVente);
                        statement.setString(2, DateHeureFin);
                        statement.executeUpdate();
                        statement.close();
                        success = true;
                    } catch (SQLException e) {
                        System.out.println("Vous n'avez pas respecté le format AAAA-MM-JJ HH:MI:SS !");
                    }
                }
            } else if (limité.equals("NON")) {
                int delai = 10;
                PreparedStatement statement = conn.prepareStatement("INSERT INTO VenteDureeIllimitee (IdVente, Delai) VALUES (?,?)");
                statement.setInt(1, idVente);
                statement.setInt(2, delai);
                statement.executeUpdate();
                statement.close();
            }
            decrementationStock(idProduit, quantite, false);
            System.out.println("Vente effectuée !");
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
        Timestamp date = null;
        while (res3.next()) {
            date = res3.getTimestamp(1);
        }
        statementDerniereOffre.close();
        res3.close();
        return date;
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
        statement1.close();
        res.close();
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

        statement1.setString(1, categorie);

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


    public boolean affichageVentes(int IdSalle) throws SQLException {
        // À durée limitée
        PreparedStatement statement1 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vd.DateHeureFin FROM Vente v JOIN VenteDureeLimitee vd on v.idvente = vd.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement1.setInt(1, IdSalle);
        ResultSet res = statement1.executeQuery();
        boolean flag = false;

        this.clearScreen();

        this.header("VENTES DANS LA SALLE N°" + IdSalle);
        while (res.next()) {

            flag = true;

            int curr_vente = res.getInt(1);
            String curr_nom = res.getString(2);
            Timestamp dateFin = res.getTimestamp(4);

            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res2 = statementOffreMax.executeQuery();


            while (res2.next()) {
                float prix = res2.getFloat(1);
                if (prix == 0.0) {
                    prix = res.getFloat(3);
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateFinSansMilli = formatter.format(dateFin);
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix + " €. Date de fin de vente : " + dateFinSansMilli);
            }
            statementOffreMax.close();
        }

        // À durée illimitée
        PreparedStatement statement2 = conn.prepareStatement("SELECT v.IdVente,p.NomProduit, v.PrixDepart, vi.delai FROM Vente v JOIN VenteDureeIllimitee vi on v.idvente = vi.idvente JOIN Produit p ON p.idProduit = v.idProduit WHERE v.IdSalle = ?");
        statement2.setInt(1, IdSalle);
        ResultSet res2 = statement2.executeQuery();

        while (res2.next()) {

            flag = true;

            int curr_vente = res2.getInt(1);
            String curr_nom = res2.getString(2);
            int delai = res2.getInt(4);
            PreparedStatement statementOffreMax = conn.prepareStatement("SELECT MAX(PrixAchat) FROM Offre WHERE IdVente = (select idProduit from Vente where idVente = ?) ");

            statementOffreMax.setInt(1, curr_vente);

            ResultSet res3 = statementOffreMax.executeQuery();
            while (res3.next()) {
                Timestamp DateHeureDerniereOffre = getDateHeureDerniereOffre(curr_vente);
                if (DateHeureDerniereOffre == null) {
                    DateHeureDerniereOffre = getDateActuelle();
                }
                Timestamp dateFin = ajouteDelai(DateHeureDerniereOffre, delai);
                float prix = res2.getFloat(1);
                if (prix == 0.0) {
                    prix = res2.getFloat(3);
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateFinSansMilli = formatter.format(dateFin);
                System.out.println("Vente n°" + curr_vente + " , Produit : " + curr_nom + " , Prix : " + prix + " €. Date de fin de vente : " + dateFinSansMilli);
            }
            statementOffreMax.close();
            res3.close();
        }
        statement1.close();
        statement2.close();
        res.close();
        res2.close();
        return flag;
    }


    public void process_acheteur(String mail) throws SQLException {
        this.affichageSalles();

        System.out.println("\n\nDans quelle salle désirez vous vous rendre ? ");

        Scanner scannerNum = new Scanner(System.in);
        int num = scannerNum.nextInt();

        if (this.affichageVentes(num)) {

            System.out.println("\n\nSur quelle vente voulez-vous enchérir ?");
            Scanner scannerVente = new Scanner(System.in);
            int idVente = scannerNum.nextInt();

            enchere(idVente, mail);
        } else {
            System.out.println("\n\nIl n'y a aucune vente en cours dans cette salle. \nTapez 0 pour revenir au choix précédent");

            Scanner scannerMenu = new Scanner(System.in);
            int menu = scannerNum.nextInt();

            if (menu == 0) {
                process_acheteur(mail);
            }
        }
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
        statement1.close();
        res.close();
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
        statement1.close();
        res.close();
    }


    public void process_vendeur() throws SQLException {

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
        while (res.next()) {
            int rev = res.getInt(1);
            if (rev == 0) {
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
        while (res.next()) {
            int montante = res.getInt(1);
            if (montante == 0) {
                statementPrix.close();
                res.close();
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
        while (res.next()) {
            int multiple = res.getInt(1);
            if (multiple == 0) {
                statementPrix.close();
                res.close();
                return true;
            }
        }
        return false;
    }

    public boolean IsDureeLimitee(int IdVente) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT IDVENTE FROM VENTEDUREELIMITEE WHERE IDVENTE = ?");
        statementPrix.setInt(1, IdVente);
        ResultSet res = statementPrix.executeQuery();
        if (res.next() && res.getInt(1) >= 0) {
            statementPrix.close();
            res.close();
            return true;
        }
        return false;
    }

    public void enchere(int idVente, String mail) throws SQLException {
        System.out.println("----------------------------------------------------");
        // L'utilisateur ne peut enchérir qu'une seule fois si la vente n'est pas à offre multiple
        if (IsOffreMultiple(idVente)) {
            PreparedStatement VerifUserOffre = conn.prepareStatement("SELECT EMAIL FROM OFFRE WHERE IDVENTE = ?");
            VerifUserOffre.setInt(1, idVente);
            ResultSet res = VerifUserOffre.executeQuery();
            while (res.next()) {
                String email = res.getString(1);
                if (email.equals(mail)) {
                    System.out.println("Vous ne pouvez enchérir qu'une seule fois sur cette vente");
                }
            }
        } else {
            PreparedStatement statementProduit = conn.prepareStatement("SELECT NomProduit FROM Produit JOIN Vente on Vente.idProduit=Produit.idProduit WHERE Vente.idVente = ?");
            statementProduit.setInt(1, idVente);
            ResultSet resProduit = statementProduit.executeQuery();
            String produit = "";
            if (resProduit.next()) {
                produit = resProduit.getString(1);
            }
            statementProduit.close();
            resProduit.close();
            System.out.println("Bienvenue dans l'enchère du produit correspondant : " + produit);
            String montante;

            if (IsMontante(getIdVente(produit))) {
                montante = "montante";
            } else {
                montante = "descendante";
            }

            System.out.println("La vente est " + montante + ", est-ce que cela vous convient? (oui/non) :");
            Scanner scanner = new Scanner(System.in);
            String rep = scanner.next();
            if (rep.equals("non")) {
                //
            } else {
                if (montante.equals("descendante")) {
                    PreparedStatement statementPrix = conn.prepareStatement("SELECT QUANTITE FROM Vente WHERE IdVente = ?");
                    statementPrix.setInt(1, idVente);
                    ResultSet res = statementPrix.executeQuery();
                    while (res.next()) {
                        // cas où il y a déjà des offres
                        PreparedStatement statementOffreMax = conn.prepareStatement("SELECT VENTE.PRIXDEPART FROM VENTE WHERE VENTE.IDPRODUIT = ?");
                        statementOffreMax.setInt(1, getIdProduit(produit));
                        ResultSet resOffreMax = statementOffreMax.executeQuery();
                        if (resOffreMax.next()) {
                            int quantite = res.getInt(1);
                            int prixDepart = resOffreMax.getInt(1);
                            System.out.println("Il y a " + quantite + " " + produit + ", le prix du produit est de : " + prixDepart + " euros");
                            // Vérification qu'il y a assez de produits en stock
                            System.out.println("Combien voulez-vous en acheter ? ");
                            Scanner scanQuantite = new Scanner(System.in);
                            int quantiteOffre = scanQuantite.nextInt();
                            while (quantiteOffre > quantite) {
                                System.out.println("Il n'y a pas assez de produits à vendre !");
                                System.out.println("Combien voulez-vous en acheter ? ");
                                scanQuantite = new Scanner(System.in);
                                quantiteOffre = scanQuantite.nextInt();
                            }
                            decrementationQuantite(idVente, quantiteOffre);
                            System.out.println("Vous avez acheté " + quantiteOffre + " " + produit + " !");
                        }
                    }
                } else {
                    PreparedStatement statementPrix = conn.prepareStatement("SELECT QUANTITE FROM VENTE WHERE IDVENTE = ?");
                    statementPrix.setInt(1, idVente);
                    ResultSet res = statementPrix.executeQuery();
                    while (res.next()) {
                        // cas où il y a déjà des offres
                        PreparedStatement statementOffreMax = conn.prepareStatement("SELECT VENTE.PRIXDEPART, COALESCE(MAX(OFFRE.PrixAchat),0) FROM VENTE LEFT JOIN OFFRE ON OFFRE.IDVENTE = VENTE.IDVENTE WHERE VENTE.IDPRODUIT = ? GROUP BY VENTE.PRIXDEPART");
                        statementOffreMax.setInt(1, getIdProduit(produit));
                        ResultSet resOffreMax = statementOffreMax.executeQuery();
                        if (resOffreMax.next()) {
                            float offreMax = resOffreMax.getFloat(2);
                            if (offreMax == 0) {
                                offreMax = resOffreMax.getFloat(1);
                            }
                            int quantite = res.getInt(1);
                            System.out.println("Il y a " + quantite + " " + produit + ", le prix de la dernière enchère est de : " + offreMax + " euros");
                            // Vérification suivant si l'offre est montante ou pas
                            System.out.println("Quelle est votre offre (en euros) ? ");
                            Scanner scanOffre = new Scanner(System.in);
                            float offre = scanOffre.nextFloat();
                            while (offre <= offreMax) {
                                System.out.println("L'offre est montante, vous ne pouvez pas réaliser une offre strictement supérieure au prix de la dernière offre");
                                System.out.println("Quelle est votre offre (en euros) ? ");
                                scanOffre = new Scanner(System.in);
                                offre = scanOffre.nextFloat();
                            }

                            // Vérification qu'il y a assez de produits en stock
                            System.out.println("Combien voulez-vous en acheter ? ");
                            Scanner scanQuantite = new Scanner(System.in);
                            int nouvelleQuantite = scanQuantite.nextInt();
                            while (nouvelleQuantite > quantite) {
                                System.out.println("Il n'y a pas assez de produits en stock");
                                System.out.println("Combien voulez-vous en acheter ? ");
                                scanQuantite = new Scanner(System.in);
                                nouvelleQuantite = scanQuantite.nextInt();
                            }
                            ajouteOffre(idVente, mail, offre, nouvelleQuantite);
                            System.out.println("Enchère effectuée");
                        }
                    }
                }
            }
        }
    }

    // retourne true si timestamp2 > timestamp 1
    public boolean compareTemps(Timestamp timestamp1, Timestamp timestamp2) {
        return timestamp1.after(timestamp2);
    }

    public int compareTimestampsMinutes(Timestamp timestamp1, Timestamp timestamp2) {
        // Calcul de la différence absolue en millisecondes
        long diffInMillis = Math.abs(timestamp2.getTime() - timestamp1.getTime());

        // Retourner la différence sous forme de chaîne
        return (int) TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
    }


    public String getEMail(String produit) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT Email FROM Produit WHERE NOMPRODUIT = ?");
        statementPrix.setString(1, produit);
        ResultSet res = statementPrix.executeQuery();
        while (res.next()) {
            String email = res.getString(1);
            statementPrix.close();
            res.close();
            return email;
        }
        return "";
    }

    public void setEmail(String mail) {
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
            int idProduit = res.getInt(1);
            statementPrix.close();
            res.close();
            return idProduit;
        }
        return -1;
    }


    public int getIdVente(String produit) throws SQLException {
        PreparedStatement statementPrix = conn.prepareStatement("SELECT V.IDVENTE FROM PRODUIT P,VENTE V WHERE P.IDPRODUIT = V.IDPRODUIT AND P.NOMPRODUIT = ?");
        statementPrix.setString(1, produit);
        ResultSet res = statementPrix.executeQuery();
        while (res.next()) {
            int idProduit = res.getInt(1);
            statementPrix.close();
            res.close();
            return idProduit;
        }
        return -1;
    }

    public void ajouteOffre(int IdVente, String eMail, float PrixAchat, int quantite) throws SQLException {
        Timestamp dateActuelle = getDateActuelle();
        PreparedStatement statementDateHeureOffre = conn.prepareStatement("INSERT INTO DateHeureOffre VALUES(?)");
        statementDateHeureOffre.setTimestamp(1, dateActuelle);
        statementDateHeureOffre.executeUpdate();
        PreparedStatement statementPrix = conn.prepareStatement("INSERT INTO OFFRE VALUES(?,?,?,?,?)");
        statementPrix.setInt(1, IdVente);
        statementPrix.setString(2, eMail);
        statementPrix.setFloat(3, PrixAchat);
        statementPrix.setInt(4, quantite);
        statementPrix.setTimestamp(5, dateActuelle);
        statementPrix.executeUpdate();

        statementDateHeureOffre.close();
        statementPrix.close();
    }

    public void suppressionProduit(int idProduit) throws SQLException {
        suppressionAllOffresProduit(idProduit);
        PreparedStatement statementOffre = conn.prepareStatement("SELECT IDVENTE FROM Vente WHERE idProduit = ?");
        statementOffre.setInt(1, idProduit);
        ResultSet res = statementOffre.executeQuery();
        while (res.next()) {
            int idv = res.getInt(1);
            suppressionVente(idv);
        }
        PreparedStatement statementProduit = conn.prepareStatement("DELETE FROM Produit WHERE idProduit = ?");
        statementProduit.setString(1, Integer.toString(idProduit));
        statementProduit.executeQuery();

        statementOffre.close();
        statementProduit.close();
    }


    public void suppressionVente(int idVente) throws SQLException {
        boolean lim = IsDureeLimitee(idVente);
        PreparedStatement statementVentelim;
        if (lim) {
            statementVentelim = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
        } else {
            statementVentelim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
        }
        statementVentelim.setInt(1, idVente);
        statementVentelim.executeUpdate();

        PreparedStatement statementVente = conn.prepareStatement("DELETE FROM Vente WHERE idVente = ?");
        statementVente.setInt(1, idVente);
        statementVente.executeUpdate();

        statementVentelim.close();
        statementVente.close();
    }

    /*
     * Methode pour supprimer toutes les offres qui ont ete effectuees sur un produit à partir de l'id de la vente */
    public void suppressionAllOffres(int idVente) throws SQLException {
        PreparedStatement statementOffre = conn.prepareStatement("DELETE FROM Offre WHERE IdVente = ?");
        statementOffre.setInt(1, idVente);
        statementOffre.executeUpdate();
        statementOffre.close();
    }

    public void suppressionAllOffresProduit(int idProduit) throws SQLException {
        PreparedStatement statementOffre = conn.prepareStatement("SELECT IDVENTE FROM Vente WHERE idProduit = ?");
        statementOffre.setInt(1, idProduit);
        ResultSet res = statementOffre.executeQuery();
        while (res.next()) {
            int idp = res.getInt(1);
            suppressionAllOffres(idp);
        }
        statementOffre.close();
    }

    /*On va l'appeler quand qqn met en vente un produit */
    public void incrementeStock(int idp, int quantite) throws SQLException {
        PreparedStatement statementStock = conn.prepareStatement("SELECT Stock FROM Produit WHERE IDPRODUIT = ?");
        statementStock.setInt(1, idp);
        ResultSet res = statementStock.executeQuery();
        int stock = 0;

        if (res.next()) {
            stock = res.getInt(1);
        }

        PreparedStatement incr = conn.prepareStatement("UPDATE Produit SET Stock = ? WHERE IDPRODUIT = ?");
        incr.setInt(1, quantite + stock);
        incr.setInt(2, idp);
        incr.executeQuery();
    }

    /* Methode qui va etre appelee à la fin d'une enchere pour decrementer le stock d'un produit
     * est le supprimer si ce stock passe à 0*/
    public void decrementationStock(int idProduit, int quantiteProduit, boolean suppression) throws SQLException {
        // On part du principe que l'offre s'est terminee, qu'on a deja verifie que stock > quantiteProduit
        PreparedStatement statementStock = conn.prepareStatement("SELECT p.Stock FROM Produit p WHERE p.idProduit = ?");
        statementStock.setInt(1, idProduit);
        ResultSet res = statementStock.executeQuery();
        while (res.next()) {
            int stockRestant = res.getInt(1);
            if (stockRestant - quantiteProduit == 0) {
                if (suppression) {
                    suppressionProduit(idProduit);
                }
            } else {
                // S'il reste encore du stock à la fin de l'achat, on décrémente simplement le stock
                PreparedStatement statementAlter = conn.prepareStatement("UPDATE Produit SET Stock = ? WHERE Produit.idProduit = ?");
                statementAlter.setInt(1, stockRestant - quantiteProduit);
                statementAlter.setInt(2, idProduit);
                statementAlter.executeUpdate();
                statementAlter.close();
            }
        }
        statementStock.close();
        res.close();
    }

    /*Méthode qui va être appelée lorsqu'un utilisateur fait une offre sur une vente descendante*/
    public void decrementationQuantite(int idVente, int quantiteProduit) throws SQLException {
        PreparedStatement statementStock = conn.prepareStatement("SELECT v.quantite FROM Vente v WHERE v.idVente = ?");
        statementStock.setInt(1, idVente);
        ResultSet res = statementStock.executeQuery();
        while (res.next()) {
            int quantite = res.getInt(1);
            // S'il n'y a plus de produit dans la vente, on supprime la vente
            if (quantite - quantiteProduit == 0) {
                suppressionVente(idVente);
            } else {
                // S'il reste encore du stock à la fin de l'achat, on décrémente simplement le stock
                PreparedStatement statementAlter = conn.prepareStatement("UPDATE Vente SET Quantite = ? WHERE Vente.IdVente = ?");
                statementAlter.setInt(1, quantite - quantiteProduit);
                statementAlter.setInt(2, idVente);
                statementAlter.executeUpdate();
                statementAlter.close();
            }
        }
        statementStock.close();
        res.close();
    }


    public void checkVentesDescendantes() throws SQLException {
        PreparedStatement statementVentesDescendantes = conn.prepareStatement("SELECT Montante, dateHeureVente, idVente, PrixDepart FROM Vente");
        ResultSet res = statementVentesDescendantes.executeQuery();
        while (res.next()) {
            int montante = res.getInt(1);
            if (montante == 0) {
                Timestamp dateHeureVente = res.getTimestamp(2);
                int idVente = res.getInt(3);
                int prixDepart = res.getInt(4);
                int diffMinutes;
                diffMinutes = compareTimestampsMinutes(dateHeureVente, getDateActuelle());
                int nouveauPrix = prixDepart - diffMinutes;
                if (nouveauPrix <= 0) {
                    suppressionAllOffres(idVente);
                    suppressionVente(idVente);
                } else {
                    PreparedStatement statementAlter = conn.prepareStatement("UPDATE Vente SET PrixDepart = ?, DateHeureVente = ? WHERE Vente.idVente = ?");
                    statementAlter.setInt(1, nouveauPrix);
                    statementAlter.setTimestamp(2, getDateActuelle());
                    statementAlter.setInt(3, idVente);
                    statementAlter.executeUpdate();
                    statementAlter.close();
                }
            }
        }
        statementVentesDescendantes.close();
        res.close();
    }

    public int resteVente(int idVente) throws SQLException {
        PreparedStatement statementReste = conn.prepareStatement("SELECT Vente.Quantite, COALESCE(MAX(Offre.PRIXACHAT),0), Produit.Stock FROM Vente JOIN Offre ON Offre.IdVente=Vente.IdVente JOIN Produit ON Produit.idProduit=Vente.idProduit WHERE Vente.idVente = ? GROUP BY Produit.Stock, Vente.Quantite");
        statementReste.setInt(1, idVente);
        ResultSet res = statementReste.executeQuery();
        try {
            while (res.next()) {
                int stock = res.getInt(3);
                int maxPrixAchat = res.getInt(2);
                if (maxPrixAchat == 0) {
                    statementReste.close();
                    res.close();
                    return stock;
                } else {
                    PreparedStatement statementQuantite = conn.prepareStatement("SELECT QuantiteProduit FROM Offre WHERE PrixAchat = ?");
                    statementQuantite.setInt(1, maxPrixAchat);
                    ResultSet res2 = statementQuantite.executeQuery();
                    if (res2.next()) {
                        int quantite = res2.getInt(1);
                        return stock + quantite;
                    }
                }
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }

    public void vainqueurVente(int idVente) throws SQLException {
        PreparedStatement offreMax = conn.prepareStatement("SELECT Email FROM Offre WHERE idvente = ? and PrixAchat = (SELECT MAX(PrixAchat) FROM Offre WHERE idVente = ?)");
        offreMax.setInt(1, idVente);
        offreMax.setInt(2, idVente);
        ResultSet res = offreMax.executeQuery();
        while (res.next()) {
            String email = res.getString(1); // On a récupéré le Mail de la personne qui a remporté l'enchère
            // Maintenant il faut qu'on regarde quel produit la personne vient d'acheter
            PreparedStatement produitAcheter = conn.prepareStatement("SELECT NomProduit FROM Produit p, Vente v WHERE idVente = ? and p.idProduit = v.idProduit");
            produitAcheter.setInt(1, idVente);
            ResultSet res1 = produitAcheter.executeQuery();
            while (res1.next()) {
                String nomProduit = res1.getString(1);
                System.out.println(email + "a gagne l'enchere sur le produit " + nomProduit);
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
            if (!compareTemps(dateHeureFinVente, actualDate)) {
                vainqueurVente(idVente);
                // Si elle est terminée alors, on regarde si la vente associée était révocable
                PreparedStatement statementRevocable = conn.prepareStatement("SELECT Vente.Revocable, Vente.idProduit FROM Vente WHERE idVente = ?");
                statementRevocable.setInt(1, idVente);
                ResultSet res2 = statementRevocable.executeQuery();
                while (res2.next()) {
                    int revoc = res2.getInt(1); // On va voir si la vente était révocable
                    int idProduit = res2.getInt(2); // On récupère l'idProduit du produit vendu
                    switch (revoc) {
                        case 0: // Si la vente n'était pas révocable on supprime la vente, le produit vendu et les offres
                            int nouvelleQuantite = resteVente(idVente);
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
                            PreparedStatement selectValeursProduit = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                            selectValeursProduit.setInt(1, idProduit);
                            ResultSet resProd = selectValeursProduit.executeQuery();
                            while (resProd.next()) {
                                // On peut maintenant supprimer le produit
                                suppressionProduit(idProduit);
                                // On recrée maintenant le produit
                                PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                creeProp.setInt(1, resProd.getInt(1));
                                creeProp.setString(2, resProd.getString(2));
                                creeProp.setFloat(3, resProd.getFloat(3));
                                creeProp.setInt(4, nouvelleQuantite);
                                creeProp.setString(5, resProd.getString(5));
                                creeProp.setString(6, resProd.getString(6));
                                creeProp.executeUpdate();
                            }
                            break;
                        case 1: // Si la vente était révocable On doit supprimer la vente et les offres si le prix d'achat
                            // est inférieur au prix de revient, mais on ne supprime pas le produit,
                            // Il faut réussir à sortir le produit de la salle de vente dans laquelle il est
                            PreparedStatement statementQtProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                            statementQtProduit.setInt(1, idVente);
                            ResultSet res4 = statementQtProduit.executeQuery();
                            PreparedStatement selectPrixRevient = conn.prepareStatement("SELECT Produit.PrixDeRevient FROM Produit WHERE idProduit = ?");
                            selectPrixRevient.setInt(1, idProduit);
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
                                        int nouvelleQuantiteRevoc = resteVente(idVente);
                                        // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                        // On supprime les offres.
                                        suppressionAllOffres(idVente);
                                        // On supprime les ventes à durée Limité
                                        PreparedStatement statementSupprVenteLimRevoc = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                        statementSupprVenteLimRevoc.setInt(1, idVente);
                                        statementSupprVenteLimRevoc.executeUpdate();
                                        // On supprime la vente
                                        suppressionVente(idVente);
                                        // On supprime le produit en faisant appel a la methode decrementationStock
                                        PreparedStatement selectValeursProduitRevoc = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                        selectValeursProduitRevoc.setInt(1, idProduit);
                                        ResultSet resProdRevoc = selectValeursProduitRevoc.executeQuery();
                                        while (resProdRevoc.next()) {
                                            // On peut maintenant supprimer le produit
                                            suppressionProduit(idProduit);
                                            // On recrée maintenant le produit
                                            PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                            creeProp.setInt(1, resProdRevoc.getInt(1));
                                            creeProp.setString(2, resProdRevoc.getString(2));
                                            creeProp.setFloat(3, resProdRevoc.getFloat(3));
                                            creeProp.setInt(4, nouvelleQuantiteRevoc);
                                            creeProp.setString(5, resProdRevoc.getString(5));
                                            creeProp.setString(6, resProdRevoc.getString(6));
                                            creeProp.executeUpdate();
                                        }
                                    } else {
                                        // Si le vendeur ne va pas gagner d'argent, on annule son offre
                                        // On commence par supprimer les offres associées à la vente.
                                        suppressionAllOffres(idVente);
                                        // On supprime ensuite la vente
                                        suppressionVente(idVente);
                                        // On doit maintenant gérer le produit
                                        // la seule solution au'on voit c'est de supprimer le produit et de le re-créer
                                        // on va d'abord recup toutes les values du produit
                                        PreparedStatement selectQuantite = conn.prepareStatement("SELECT Quantite FROM Vente WHERE idVente = ?");
                                        selectQuantite.setInt(1, idVente);
                                        ResultSet resProdRevoc2 = selectQuantite.executeQuery();
                                        int quantite = 0;
                                        if (resProdRevoc2.next()) {
                                            quantite = resProdRevoc2.getInt(1);
                                        }

                                        int nouvelleQuantiteRevoc = resteVente(idVente);
                                        // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                        // On supprime les offres.
                                        suppressionAllOffres(idVente);
                                        // On supprime les ventes à durée Limité
                                        PreparedStatement statementSupprVenteLimRevoc = conn.prepareStatement("DELETE FROM VenteDureeLimitee WHERE idVente = ?");
                                        statementSupprVenteLimRevoc.setInt(1, idVente);
                                        statementSupprVenteLimRevoc.executeUpdate();
                                        // On supprime la vente
                                        suppressionVente(idVente);
                                        // On supprime le produit en faisant appel a la methode decrementationStock
                                        PreparedStatement selectValeursProduitRevoc = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                        selectValeursProduitRevoc.setInt(1, idProduit);
                                        ResultSet resProdRevoc = selectValeursProduitRevoc.executeQuery();
                                        while (resProdRevoc.next()) {
                                            // On peut maintenant supprimer le produit
                                            suppressionProduit(idProduit);
                                            // On recrée maintenant le produit
                                            PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                            creeProp.setInt(1, resProdRevoc.getInt(1));
                                            creeProp.setString(2, resProdRevoc.getString(2));
                                            creeProp.setFloat(3, resProdRevoc.getFloat(3));
                                            creeProp.setInt(4, quantite);
                                            creeProp.setString(5, resProdRevoc.getString(5));
                                            creeProp.setString(6, resProdRevoc.getString(6));
                                            creeProp.executeUpdate();
                                        }
                                    }
                                }
                                break;
                            }
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
                if (DateHeureDerniereOffre == null) {
                    continue;
                }
                Timestamp dateHeureFinVente = ajouteDelai(DateHeureDerniereOffre, delai);
                //On vérifie que l'offre n'est pas terminée
                if (!compareTemps(dateHeureFinVente, actualDate)) {
                    vainqueurVente(idVente);
                    // Si elle est terminée alors, on regarde si la vente associée était révocable
                    PreparedStatement statementRevocable = conn.prepareStatement("SELECT Vente.Revocable, Vente.idProduit FROM Vente WHERE idVente = ?");
                    statementRevocable.setInt(1, idVente);
                    ResultSet res2 = statementRevocable.executeQuery();
                    while (res2.next()) {
                        int revoc = res2.getInt(1); // On va voir si la vente était révocable
                        int idProduit = res2.getInt(2); // On récupère l'idProduit du produit vendu
                        switch (revoc) {
                            case 0: // Si la vente n'était pas révocable on supprime la vente, le produit vendu et les offres
                                int nouvelleQuantite = resteVente(idVente);
                                // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                // On supprime les offres.
                                suppressionAllOffres(idVente);
                                // On supprime les ventes à durée Limité
                                PreparedStatement statementSupprVenteLim = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                statementSupprVenteLim.setInt(1, idVente);
                                statementSupprVenteLim.executeUpdate();
                                // On supprime la vente
                                suppressionVente(idVente);
                                // On supprime le produit en faisant appel a la methode decrementationStock
                                PreparedStatement selectValeursProduit = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                selectValeursProduit.setInt(1, idProduit);
                                ResultSet resProd = selectValeursProduit.executeQuery();
                                while (resProd.next()) {
                                    // On peut maintenant supprimer le produit
                                    suppressionProduit(idProduit);
                                    // On recrée maintenant le produit
                                    PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                    creeProp.setInt(1, resProd.getInt(1));
                                    creeProp.setString(2, resProd.getString(2));
                                    creeProp.setFloat(3, resProd.getFloat(3));
                                    creeProp.setInt(4, nouvelleQuantite);
                                    creeProp.setString(5, resProd.getString(5));
                                    creeProp.setString(6, resProd.getString(6));
                                    creeProp.executeUpdate();
                                }
                                break;
                            case 1: // Si la vente était révocable On doit supprimer la vente et les offres si le prix d'achat
                                // est inférieur au prix de revient, mais on ne supprime pas le produit,
                                // Il faut réussir à sortir le produit de la salle de vente dans laquelle il est
                                PreparedStatement statementQtProduit = conn.prepareStatement("SELECT o.QuantiteProduit FROM Vente v, Offre o WHERE o.idVente = ?");
                                statementQtProduit.setInt(1, idVente);
                                ResultSet res4 = statementQtProduit.executeQuery();
                                PreparedStatement selectPrixRevient = conn.prepareStatement("SELECT Produit.PrixDeRevient FROM Produit WHERE idProduit = ?");
                                selectPrixRevient.setInt(1, idProduit);
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
                                            int nouvelleQuantiteRevoc = resteVente(idVente);
                                            // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                            // On supprime les offres.
                                            suppressionAllOffres(idVente);
                                            // On supprime les ventes à durée Limité
                                            PreparedStatement statementSupprVenteLimRevoc = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                            statementSupprVenteLimRevoc.setInt(1, idVente);
                                            statementSupprVenteLimRevoc.executeUpdate();
                                            // On supprime la vente
                                            suppressionVente(idVente);
                                            // On supprime le produit en faisant appel a la methode decrementationStock
                                            PreparedStatement selectValeursProduitRevoc = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                            selectValeursProduitRevoc.setInt(1, idProduit);
                                            ResultSet resProdRevoc = selectValeursProduitRevoc.executeQuery();
                                            while (resProdRevoc.next()) {
                                                // On peut maintenant supprimer le produit
                                                suppressionProduit(idProduit);
                                                // On recrée maintenant le produit
                                                PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                                creeProp.setInt(1, resProdRevoc.getInt(1));
                                                creeProp.setString(2, resProdRevoc.getString(2));
                                                creeProp.setFloat(3, resProdRevoc.getFloat(3));
                                                creeProp.setInt(4, nouvelleQuantiteRevoc);
                                                creeProp.setString(5, resProdRevoc.getString(5));
                                                creeProp.setString(6, resProdRevoc.getString(6));
                                                creeProp.executeUpdate();
                                                creeProp.close();
                                                resProdRevoc.close();
                                                selectValeursProduitRevoc.close();
                                                statementRevocable.close();
                                                statementOffreMax.close();
                                                statementQtProduit.close();
                                                statementSupprVenteLimRevoc.close();
                                            }
                                        } else {
                                            // Si le vendeur ne va pas gagner d'argent, on annule son offre
                                            // On commence par supprimer les offres associées à la vente.
                                            suppressionAllOffres(idVente);
                                            // On supprime ensuite la vente
                                            suppressionVente(idVente);
                                            // On doit maintenant gérer le produit
                                            // la seule solution au'on voit c'est de supprimer le produit et de le re-créer
                                            // on va d'abord recup toutes les values du produit
                                            PreparedStatement selectQuantite = conn.prepareStatement("SELECT Quantite FROM Vente WHERE idVente = ?");
                                            selectQuantite.setInt(1, idVente);
                                            ResultSet resProdRevoc2 = selectQuantite.executeQuery();
                                            int quantite = 0;
                                            if (resProdRevoc2.next()) {
                                                quantite = resProdRevoc2.getInt(1);
                                            }
                                            // On doit d'abord DROP les offres, puis les ventes (Limitée ou non) puis les ventes puis les produits
                                            // On supprime les offres.
                                            suppressionAllOffres(idVente);
                                            // On supprime les ventes à durée Limité
                                            PreparedStatement statementSupprVenteLimRevoc = conn.prepareStatement("DELETE FROM VenteDureeIllimitee WHERE idVente = ?");
                                            statementSupprVenteLimRevoc.setInt(1, idVente);
                                            statementSupprVenteLimRevoc.executeUpdate();
                                            // On supprime la vente
                                            suppressionVente(idVente);
                                            // On supprime le produit en faisant appel a la methode decrementationStock
                                            PreparedStatement selectValeursProduitRevoc = conn.prepareStatement("SELECT idProduit, NomProduit, PrixDeRevient,Stock,NomCategorie,Email FROM Produit WHERE idProduit = ?");
                                            selectValeursProduitRevoc.setInt(1, idProduit);
                                            ResultSet resProdRevoc = selectValeursProduitRevoc.executeQuery();
                                            while (resProdRevoc.next()) {
                                                // On peut maintenant supprimer le produit
                                                suppressionProduit(idProduit);
                                                // On recrée maintenant le produit
                                                PreparedStatement creeProp = conn.prepareStatement("INSERT INTO Produit VALUES(?,?,?,?,?,?)");
                                                creeProp.setInt(1, resProdRevoc.getInt(1));
                                                creeProp.setString(2, resProdRevoc.getString(2));
                                                creeProp.setFloat(3, resProdRevoc.getFloat(3));
                                                creeProp.setInt(4, quantite);
                                                creeProp.setString(5, resProdRevoc.getString(5));
                                                creeProp.setString(6, resProdRevoc.getString(6));
                                                creeProp.executeUpdate();
                                                selectQuantite.close();
                                                selectPrixRevient.close();
                                                selectValeursProduitRevoc.close();
                                                creeProp.close();
                                                resProdRevoc.close();
                                                resProdRevoc2.close();
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
            }
            System.out.println("Base de données mise à jour");
        res.close();
        }
    }
