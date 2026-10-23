package club.matchs;

import club.exceptions.DonneeInvalideException;
import club.personnes.Equipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Match d'une équipe contre un adversaire.
 * Composition : un match possède ses StatJoueur (elles n'existent pas sans lui).
 * Ordre obligatoire : d'abord le score, ensuite les statistiques (RG13, RG14).
 */
public class Match {

    private final String id;
    private final Equipe equipe;
    private final String adversaire;
    private final LocalDateTime date;
    private final boolean domicile;
    private int scoreEquipe;
    private int scoreAdversaire;
    private boolean joue;
    private final List<StatJoueur> stats;

    /**
     * Crée un match à jouer (score 0-0, aucune statistique).
     *
     * @throws DonneeInvalideException si l'id, l'équipe, l'adversaire ou la date est absent
     */
    public Match(String id, Equipe equipe, String adversaire, LocalDateTime date, boolean domicile) {
        if (id == null || id.isBlank()) {
            throw new DonneeInvalideException("L'identifiant du match est obligatoire.");
        }
        if (equipe == null) {
            throw new DonneeInvalideException("L'équipe du match est obligatoire.");
        }
        if (adversaire == null || adversaire.isBlank()) {
            throw new DonneeInvalideException("Le nom de l'adversaire est obligatoire.");
        }
        if (date == null) {
            throw new DonneeInvalideException("La date du match est obligatoire.");
        }
        this.id = id;
        this.equipe = equipe;
        this.adversaire = adversaire.trim();
        this.date = date;
        this.domicile = domicile;
        this.scoreEquipe = 0;
        this.scoreAdversaire = 0;
        this.joue = false;
        this.stats = new ArrayList<>();
    }

    /**
     * Enregistre le score final (RG13) : scores supérieurs ou égaux à 0,
     * égalité interdite, enregistrement possible une seule fois.
     *
     * @throws DonneeInvalideException si une de ces règles n'est pas respectée
     */
    public void enregistrerScore(int scoreEquipe, int scoreAdversaire) {
        if (joue) {
            throw new DonneeInvalideException("Le score du match " + id + " est déjà enregistré.");
        }
        if (scoreEquipe < 0 || scoreAdversaire < 0) {
            throw new DonneeInvalideException("Les scores doivent être supérieurs ou égaux à 0.");
        }
        if (scoreEquipe == scoreAdversaire) {
            throw new DonneeInvalideException("L'égalité est interdite : un match se joue jusqu'à un vainqueur.");
        }
        this.scoreEquipe = scoreEquipe;
        this.scoreAdversaire = scoreAdversaire;
        this.joue = true;
    }

    /**
     * Ajoute la statistique d'un joueur (RG14) : score déjà enregistré,
     * joueur de l'équipe, une seule statistique par joueur, et somme des
     * points inférieure ou égale au score de l'équipe.
     *
     * @throws DonneeInvalideException si une de ces règles n'est pas respectée
     */
    public void ajouterStat(StatJoueur s) {
        if (s == null) {
            throw new DonneeInvalideException("La statistique à ajouter est obligatoire.");
        }
        if (!joue) {
            throw new DonneeInvalideException("Les statistiques se saisissent après le score du match " + id + ".");
        }
        String idJoueur = s.getJoueur().getId();
        if (equipe.trouverJoueur(idJoueur).isEmpty()) {
            throw new DonneeInvalideException("Le joueur " + idJoueur
                    + " n'appartient pas à l'équipe " + equipe.getNom() + ".");
        }
        int totalPoints = s.getPoints();
        for (StatJoueur existante : stats) {
            if (existante.getJoueur().getId().equals(idJoueur)) {
                throw new DonneeInvalideException("Le joueur " + idJoueur
                        + " a déjà une statistique pour le match " + id + ".");
            }
            totalPoints += existante.getPoints();
        }
        if (totalPoints > scoreEquipe) {
            throw new DonneeInvalideException("La somme des points des joueurs (" + totalPoints
                    + ") dépasse le score de l'équipe (" + scoreEquipe + ").");
        }
        stats.add(s);
    }

    /** Renvoie une copie non modifiable des statistiques. */
    public List<StatJoueur> getStats() {
        return List.copyOf(stats);
    }

    /** A_JOUER tant qu'aucun score n'est enregistré, sinon VICTOIRE ou DEFAITE. */
    public Resultat getResultat() {
        if (!joue) {
            return Resultat.A_JOUER;
        }
        if (scoreEquipe > scoreAdversaire) {
            return Resultat.VICTOIRE;
        }
        return Resultat.DEFAITE;
    }

    public boolean isJoue() {
        return joue;
    }

    public String getId() {
        return id;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public String getAdversaire() {
        return adversaire;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isDomicile() {
        return domicile;
    }

    public int getScoreEquipe() {
        return scoreEquipe;
    }

    public int getScoreAdversaire() {
        return scoreAdversaire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match autre = (Match) o;
        return id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String lieu = domicile ? "domicile" : "extérieur";
        String score = joue ? scoreEquipe + "-" + scoreAdversaire : "pas encore joué";
        return "Match " + id + " : " + equipe.getNom() + " contre " + adversaire
                + " (" + lieu + ") " + score + " [" + getResultat() + "]";
    }
}
