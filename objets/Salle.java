package objets;
import java.util.*;

public class Salle {
    private List<Vente> listeDeVentes;
    private String categorie;

    public Salle(List<Vente> listVentes, String categorie) {
        this.listeDeVentes = listVentes;
        this.categorie = categorie;
    }

    public Salle(String categorie) {
        this.categorie = categorie;
    }

    public List<Vente> getListeDeVentes() {
        return listeDeVentes;
    }

    public String getCategorie() {
        return categorie;
    }
}
