package objets;

public class Vente {
    private int idVente;
    private float prixDepart;
    private boolean revocable;
    private boolean montante;
    private boolean offreMultiple;
    private int idSalle;
    private String mailVendeur;

    // Constructeur par défaut
    Vente(int idVente, float prixDepart, int idSalle, String mailVendeur){
        this.idVente = idVente;
        this.prixDepart = prixDepart;
        this.revocable = false;
        this.montante = true;
        this.offreMultiple = true;
        this.idSalle = idSalle;
        this.mailVendeur = mailVendeur;
    }

    // Constructeur
    Vente(int idVente, float prixDepart, boolean revocable, boolean montante, boolean offreMultiple, int idSalle, String mailVendeur){
        this.idVente = idVente;
        this.prixDepart = prixDepart;
        this.revocable = revocable;
        this.montante = montante;
        this.offreMultiple = offreMultiple;
        this.idSalle = idSalle;
        this.mailVendeur = mailVendeur;
    }


    // Getters
    public int getIdVente(){
        return this.idVente;
    }

    public int getIdSalle(){
        return this.idSalle;
    }

    public float getPrixDepart(){
        return this.prixDepart;
    }

    public String getMailVendeur(){
        return this.mailVendeur;
    }
}
