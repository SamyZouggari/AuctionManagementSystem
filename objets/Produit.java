package objets;

public class Produit {

    private String categorieProduit;
    private int idProduit;
    private float prixDeRevient;
    private int stock;

    public Produit(String cateProd, int idProd, float prixDeRevient, int stock) {
        this.categorieProduit = cateProd;
        this.idProduit = idProd;
        this.prixDeRevient = prixDeRevient;
        this.stock = stock;
    }
    public String getCategorieProduit() {
        return categorieProduit;
    }
    public int getIdProduit() {
        return idProduit;
    }
    public float getPrixDeRevient() {
        return prixDeRevient;
    }

    public int getStock() {
        return stock;
    }
}
