package club.rapports;

import club.gestion.Club;
import club.matchs.Match;
import club.matchs.Resultat;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Rapport des matchs (RG17) : une ligne par match avec son résultat,
 * puis le bilan des victoires et des défaites.
 *
 * Concepts : héritage et redéfinition (overriding) des méthodes de Rapport.
 */
public class RapportMatchs extends Rapport {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected String titre() {
        return "RAPPORT DES MATCHS";
    }

    @Override
    protected List<String> lignes(Club club) {
        List<String> lignes = new ArrayList<>();
        for (Match m : club.getMatchs()) {
            String lieu = m.isDomicile() ? "domicile" : "extérieur";
            String score = m.isJoue()
                    ? m.getScoreEquipe() + "-" + m.getScoreAdversaire()
                    : "non joué";
            lignes.add(m.getId() + " | " + m.getDate().format(FORMAT_DATE)
                    + " | " + m.getEquipe().getNom() + " contre " + m.getAdversaire()
                    + " | " + lieu
                    + " | score : " + score
                    + " | résultat : " + m.getResultat());
        }
        return lignes;
    }

    @Override
    protected String pied(Club club) {
        int victoires = 0;
        int defaites = 0;
        for (Match m : club.getMatchs()) {
            if (m.getResultat() == Resultat.VICTOIRE) {
                victoires++;
            } else if (m.getResultat() == Resultat.DEFAITE) {
                defaites++;
            }
        }
        return "Bilan : " + victoires + " victoire(s), " + defaites + " défaite(s)";
    }
}
