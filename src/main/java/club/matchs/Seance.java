package club.matchs;

import club.exceptions.DonneeInvalideException;
import club.personnes.Equipe;
import club.personnes.Joueur;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Séance d'entraînement d'une équipe, à une date et un lieu donnés.
 * Association 1-N : une séance référence ses joueurs présents (RG12).
 */
public class Seance {

    private final String id;
    private final LocalDateTime date;
    private final String lieu;
    private final Equipe equipe;
    private final List<Joueur> presents;

    /**
     * Crée une séance sans aucun présent.
     *
     * @throws DonneeInvalideException si l'id, la date, le lieu ou l'équipe est absent
     */
    public Seance(String id, LocalDateTime date, String lieu, Equipe equipe) {
        if (id == null || id.isBlank()) {
            throw new DonneeInvalideException("L'identifiant de la séance est obligatoire.");
        }
        if (date == null) {
            throw new DonneeInvalideException("La date de la séance est obligatoire.");
        }
        if (lieu == null || lieu.isBlank()) {
            throw new DonneeInvalideException("Le lieu de la séance est obligatoire.");
        }
        if (equipe == null) {
            throw new DonneeInvalideException("L'équipe de la séance est obligatoire.");
        }
        this.id = id;
        this.date = date;
        this.lieu = lieu.trim();
        this.equipe = equipe;
        this.presents = new ArrayList<>();
    }

    /**
     * Marque un joueur présent (RG12) : il doit appartenir à l'équipe
     * et ne peut pas être marqué deux fois.
     *
     * @throws DonneeInvalideException si le joueur est absent de l'équipe ou déjà présent
     */
    public void marquerPresent(Joueur j) {
        if (j == null) {
            throw new DonneeInvalideException("Le joueur à marquer présent est obligatoire.");
        }
        if (equipe.trouverJoueur(j.getId()).isEmpty()) {
            throw new DonneeInvalideException("Le joueur " + j.getId()
                    + " n'appartient pas à l'équipe " + equipe.getNom() + ".");
        }
        for (Joueur present : presents) {
            if (present.getId().equals(j.getId())) {
                throw new DonneeInvalideException("Le joueur " + j.getId()
                        + " est déjà marqué présent à la séance " + id + ".");
            }
        }
        presents.add(j);
    }

    /** Renvoie une copie non modifiable des joueurs présents. */
    public List<Joueur> getPresents() {
        return List.copyOf(presents);
    }

    /**
     * Taux de présence = présents / effectif de l'équipe (RG12).
     * Renvoie 0 si l'équipe est vide.
     */
    public double tauxPresence() {
        int effectif = equipe.getEffectif();
        if (effectif == 0) {
            return 0;
        }
        return (double) presents.size() / effectif;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLieu() {
        return lieu;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seance autre = (Seance) o;
        return id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Seance " + id + " le " + date + " à " + lieu + " (" + equipe.getNom()
                + ", " + presents.size() + "/" + equipe.getEffectif() + " présents)";
    }
}
