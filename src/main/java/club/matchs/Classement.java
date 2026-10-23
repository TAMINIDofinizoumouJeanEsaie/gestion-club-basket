package club.matchs;

import club.exceptions.DonneeInvalideException;
import club.personnes.Equipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculs de classement (RG15) : classe utilitaire, uniquement des méthodes statiques.
 * Seuls les matchs joués sont pris en compte.
 */
public class Classement {

    /** Bilan d'une équipe : victoires, défaites, points marqués et encaissés. */
    public record Bilan(int victoires, int defaites, int pointsMarques, int pointsEncaisses) {
    }

    private Classement() {
        // Classe utilitaire : on ne crée jamais d'objet Classement.
    }

    /**
     * Meilleurs marqueurs : cumule points, rebonds et passes de chaque joueur
     * sur les matchs joués, puis trie par points décroissants (compareTo de StatJoueur).
     * Une égalité de points est départagée par l'ordre alphabétique du nom complet.
     *
     * @param n nombre maximum de joueurs renvoyés
     * @throws DonneeInvalideException si la liste est absente ou si n est négatif
     */
    public static List<StatJoueur> meilleursMarqueurs(List<Match> matchs, int n) {
        if (matchs == null) {
            throw new DonneeInvalideException("La liste des matchs est obligatoire.");
        }
        if (n < 0) {
            throw new DonneeInvalideException("Le nombre de marqueurs demandé doit être supérieur ou égal à 0.");
        }
        Map<String, StatJoueur> cumuls = new LinkedHashMap<>();
        for (Match match : matchs) {
            if (!match.isJoue()) {
                continue;
            }
            for (StatJoueur stat : match.getStats()) {
                String idJoueur = stat.getJoueur().getId();
                StatJoueur ancienne = cumuls.get(idJoueur);
                if (ancienne == null) {
                    cumuls.put(idJoueur, stat);
                } else {
                    cumuls.put(idJoueur, new StatJoueur(stat.getJoueur(),
                            ancienne.getPoints() + stat.getPoints(),
                            ancienne.getRebonds() + stat.getRebonds(),
                            ancienne.getPasses() + stat.getPasses()));
                }
            }
        }
        List<StatJoueur> classement = new ArrayList<>(cumuls.values());
        classement.sort((a, b) -> {
            int comparaison = a.compareTo(b);
            if (comparaison != 0) {
                return comparaison;
            }
            return a.getJoueur().getNomComplet().compareToIgnoreCase(b.getJoueur().getNomComplet());
        });
        int limite = Math.min(n, classement.size());
        return List.copyOf(classement.subList(0, limite));
    }

    /**
     * Bilan d'une équipe sur ses matchs joués (RG15).
     * Les équipes sont comparées par leur nom, qui les identifie dans le Club.
     *
     * @throws DonneeInvalideException si la liste ou l'équipe est absente
     */
    public static Bilan bilan(List<Match> matchs, Equipe equipe) {
        if (matchs == null) {
            throw new DonneeInvalideException("La liste des matchs est obligatoire.");
        }
        if (equipe == null) {
            throw new DonneeInvalideException("L'équipe du bilan est obligatoire.");
        }
        int victoires = 0;
        int defaites = 0;
        int pointsMarques = 0;
        int pointsEncaisses = 0;
        for (Match match : matchs) {
            if (!match.isJoue() || !match.getEquipe().getNom().equals(equipe.getNom())) {
                continue;
            }
            if (match.getResultat() == Resultat.VICTOIRE) {
                victoires++;
            } else {
                defaites++;
            }
            pointsMarques += match.getScoreEquipe();
            pointsEncaisses += match.getScoreAdversaire();
        }
        return new Bilan(victoires, defaites, pointsMarques, pointsEncaisses);
    }
}
