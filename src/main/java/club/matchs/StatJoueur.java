package club.matchs;

import club.exceptions.DonneeInvalideException;
import club.personnes.Joueur;

/**
 * Statistiques d'un joueur sur un match (ou cumulées sur plusieurs matchs).
 * Implémente Comparable : l'ordre naturel est celui des points décroissants,
 * donc Collections.sort place le meilleur marqueur en premier.
 */
public class StatJoueur implements Comparable<StatJoueur> {

    private final Joueur joueur;
    private final int points;
    private final int rebonds;
    private final int passes;

    /**
     * @throws DonneeInvalideException si le joueur est absent ou si une valeur est négative
     */
    public StatJoueur(Joueur joueur, int points, int rebonds, int passes) {
        if (joueur == null) {
            throw new DonneeInvalideException("Le joueur de la statistique est obligatoire.");
        }
        if (points < 0 || rebonds < 0 || passes < 0) {
            throw new DonneeInvalideException("Points, rebonds et passes doivent être supérieurs ou égaux à 0.");
        }
        this.joueur = joueur;
        this.points = points;
        this.rebonds = rebonds;
        this.passes = passes;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public int getPoints() {
        return points;
    }

    public int getRebonds() {
        return rebonds;
    }

    public int getPasses() {
        return passes;
    }

    /** Points décroissants : celui qui a le plus de points est « plus petit » donc placé en premier. */
    @Override
    public int compareTo(StatJoueur autre) {
        return Integer.compare(autre.points, this.points);
    }

    @Override
    public String toString() {
        return joueur.getNomComplet() + " : " + points + " pts, "
                + rebonds + " reb, " + passes + " passes";
    }
}
