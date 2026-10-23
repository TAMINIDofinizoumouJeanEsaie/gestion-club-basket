package club.matchs;

import club.exceptions.DonneeInvalideException;
import club.personnes.Joueur;

import java.util.List;
import java.util.Random;

/**
 * Simulateur de match (RG16). Avec le même Random (même graine), le résultat est
 * toujours identique. Ordre FIXE des tirages, pour que tout le groupe obtienne
 * les mêmes valeurs :
 * 1. score de l'équipe : 60 + nextInt(41)
 * 2. score de l'adversaire : 60 + nextInt(41)
 * 3. si égalité : +2 pour l'équipe (pas de tirage)
 * 4. répartition des points : un tirage nextInt(effectif) par point marqué
 * 5. pour chaque joueur, dans l'ordre de l'équipe : rebonds nextInt(13), puis passes nextInt(11)
 */
public class SimulateurMatch {

    private static final int SCORE_MIN = 60;
    private static final int SCORE_MAX = 100;
    private static final int REBONDS_MAX = 12;
    private static final int PASSES_MAX = 10;

    private SimulateurMatch() {
        // Classe utilitaire : on ne crée jamais d'objet SimulateurMatch.
    }

    /**
     * Simule le match : génère le score puis les statistiques de chaque joueur.
     * La somme des points des joueurs est exactement égale au score de l'équipe.
     *
     * @throws DonneeInvalideException si le match ou le Random est absent, si le match
     *                                 est déjà joué ou si l'équipe n'a aucun joueur
     */
    public static void simuler(Match match, Random random) {
        if (match == null) {
            throw new DonneeInvalideException("Le match à simuler est obligatoire.");
        }
        if (random == null) {
            throw new DonneeInvalideException("Le générateur aléatoire est obligatoire.");
        }
        if (match.isJoue()) {
            throw new DonneeInvalideException("Le match " + match.getId() + " est déjà joué.");
        }
        List<Joueur> joueurs = match.getEquipe().getJoueurs();
        if (joueurs.isEmpty()) {
            throw new DonneeInvalideException("Impossible de simuler un match : l'équipe n'a aucun joueur.");
        }

        int etendue = SCORE_MAX - SCORE_MIN + 1;
        int scoreEquipe = SCORE_MIN + random.nextInt(etendue);
        int scoreAdversaire = SCORE_MIN + random.nextInt(etendue);
        if (scoreEquipe == scoreAdversaire) {
            scoreEquipe = scoreEquipe + 2;
        }
        match.enregistrerScore(scoreEquipe, scoreAdversaire);

        int[] points = new int[joueurs.size()];
        for (int i = 0; i < scoreEquipe; i++) {
            points[random.nextInt(joueurs.size())]++;
        }

        for (int i = 0; i < joueurs.size(); i++) {
            int rebonds = random.nextInt(REBONDS_MAX + 1);
            int passes = random.nextInt(PASSES_MAX + 1);
            match.ajouterStat(new StatJoueur(joueurs.get(i), points[i], rebonds, passes));
        }
    }
}
