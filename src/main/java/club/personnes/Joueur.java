package club.personnes;

import club.exceptions.DonneeInvalideException;
import java.time.LocalDate;

public class Joueur extends Personne {
    private int numeroMaillot;
    private Poste poste;

    public Joueur(String id, String nom, String prenom, String telephone, LocalDate dateNaissance, int numeroMaillot, Poste poste) {
        super(id, nom, prenom, telephone, dateNaissance);
        setNumeroMaillot(numeroMaillot);
        setPoste(poste);
    }

    @Override
    public String getRole() {
        return "JOUEUR";
    }

    public int getNumeroMaillot() {
        return numeroMaillot;
    }

    public void setNumeroMaillot(int numeroMaillot) {
        if (numeroMaillot < 0 || numeroMaillot > 99) {
            throw new DonneeInvalideException("Le numéro de maillot doit être compris entre 0 et 99.");
        }
        this.numeroMaillot = numeroMaillot;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        if (poste == null) {
            throw new DonneeInvalideException("Le poste ne peut pas être null.");
        }
        this.poste = poste;
    }

    @Override
    public String toString() {
        return "Joueur{" + "id='" + getId() + '\'' + ", nomComplet='" + getNomComplet() + '\'' + ", maillot=" + numeroMaillot + ", poste=" + poste + '}';
    }
}
