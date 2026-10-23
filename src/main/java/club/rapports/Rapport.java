package club.rapports;

import club.exceptions.DonneeInvalideException;
import club.gestion.Club;

import java.util.List;
import java.util.Locale;

/**
 * Document texte généré à partir des données du club.
 *
 * Concepts : classe abstraite et patron Méthode modèle (Template Method).
 * La méthode generer() est final : elle FIXE l'ordre « entête, lignes, pied ».
 * Les sous-classes ne peuvent pas changer cet ordre, elles fournissent seulement
 * les morceaux : titre() et lignes() sont abstraites (obligatoires),
 * entete() et pied() sont des « hooks » (points d'accroche) qu'on peut redéfinir.
 */
public abstract class Rapport {

    /**
     * Génère le rapport complet. Squelette fixe : entête, puis une ligne par élément, puis pied.
     *
     * @param club le club dont on lit les données
     * @return le rapport en texte brut (lignes séparées par \n)
     * @throws DonneeInvalideException si le club est absent
     */
    public final String generer(Club club) {
        if (club == null) {
            throw new DonneeInvalideException("Le club est obligatoire pour générer un rapport.");
        }
        StringBuilder texte = new StringBuilder();
        texte.append(entete()).append('\n');
        for (String ligne : lignes(club)) {
            texte.append(ligne).append('\n');
        }
        texte.append(pied(club));
        return texte.toString();
    }

    /** Titre du rapport, par exemple « RAPPORT DES COTISATIONS ». */
    protected abstract String titre();

    /** Une ligne de texte par élément du rapport. */
    protected abstract List<String> lignes(Club club);

    /** Hook : par défaut le titre encadré. Une sous-classe peut le redéfinir. */
    protected String entete() {
        return "===== " + titre() + " =====";
    }

    /** Hook : par défaut vide. Les trois rapports le redéfinissent pour leur total (RG17). */
    protected String pied(Club club) {
        return "";
    }

    /**
     * Aide commune : écrit un nombre avec le nombre de décimales demandé et une virgule
     * (format français), quel que soit l'ordinateur. Exemple : 0,67.
     */
    protected static String formaterNombre(double valeur, int decimales) {
        return String.format(Locale.FRANCE, "%." + decimales + "f", valeur);
    }

    /**
     * Aide commune : montant en FCFA, sans décimales s'il est entier (« 25000 FCFA »),
     * avec deux décimales sinon (« 150,50 FCFA »).
     */
    protected static String formaterMontant(double montant) {
        int decimales = (montant == Math.rint(montant)) ? 0 : 2;
        return formaterNombre(montant, decimales) + " FCFA";
    }
}
