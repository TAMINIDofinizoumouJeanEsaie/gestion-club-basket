package club.personnes;

import club.exceptions.DonneeInvalideException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Equipe {
    public static final int MAX_JOUEURS = 15;
    private String nom;
    private Categorie categorie;
    private Entraineur entraineur;
    private final List<Joueur> joueurs;

    public Equipe(String nom, Categorie categorie, Entraineur entraineur) {
        setNom(nom);
        setCategorie(categorie);
        setEntraineur(entraineur);
        this.joueurs = new ArrayList<>();
    }

    public void ajouterJoueur(Joueur j) {
        if (j == null) {
            throw new DonneeInvalideException("Impossible d'ajouter un joueur null.");
        }
        if (joueurs.size() >= MAX_JOUEURS) {
            throw new DonneeInvalideException("L'équipe a atteint sa capacité maximale de " + MAX_JOUEURS + " joueurs.");
        }
        if (joueurs.contains(j)) {
            throw new DonneeInvalideException("Ce joueur fait déjà partie de l'équipe.");
        }
        for (Joueur existing : joueurs) {
            if (existing.getNumeroMaillot() == j.getNumeroMaillot()) {
                throw new DonneeInvalideException("Le numéro de maillot " + j.getNumeroMaillot() + " est déjà pris dans cette équipe.");
            }
        }
        int ageJoueur = j.getAge();
        if (ageJoueur > categorie.getAgeMax()) {
            throw new DonneeInvalideException("Le joueur est trop âgé pour la catégorie " + categorie.name());
        }
        
        joueurs.add(j);
    }

    public void retirerJoueur(String idJoueur) {
        joueurs.removeIf(j -> j.getId().equals(idJoueur));
    }

    public Optional<Joueur> trouverJoueur(String idJoueur) {
        return joueurs.stream().filter(j -> j.getId().equals(idJoueur)).findFirst();
    }

    public List<Joueur> getJoueurs() {
        return List.copyOf(joueurs);
    }

    public int getEffectif() {
        return joueurs.size();
    }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom de l'équipe ne peut pas être vide.");
        }
        this.nom = nom.trim();
    }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) {
        if (categorie == null) {
            throw new DonneeInvalideException("La catégorie ne peut pas être null.");
        }
        this.categorie = categorie;
    }

    public Entraineur getEntraineur() { return entraineur; }
    public void setEntraineur(Entraineur entraineur) {
        if (entraineur == null) {
            throw new DonneeInvalideException("L'entraîneur ne peut pas être null.");
        }
        this.entraineur = entraineur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipe equipe = (Equipe) o;
        return nom.equalsIgnoreCase(equipe.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom.toLowerCase());
    }

    @Override
    public String toString() {
        return "Equipe{" + "nom='" + nom + '\'' + ", categorie=" + categorie + ", effectif=" + getEffectif() + '}';
    }
}
