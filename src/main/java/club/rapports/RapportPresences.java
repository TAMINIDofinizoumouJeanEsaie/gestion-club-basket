package club.rapports;

import club.gestion.Club;
import club.matchs.Seance;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Rapport des présences (RG17) : une ligne par séance avec son taux de présence,
 * puis le taux moyen de présence.
 *
 * Concepts : héritage et redéfinition (overriding) des méthodes de Rapport.
 */
public class RapportPresences extends Rapport {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected String titre() {
        return "RAPPORT DES PRÉSENCES";
    }

    @Override
    protected List<String> lignes(Club club) {
        List<String> lignes = new ArrayList<>();
        for (Seance s : club.getSeances()) {
            lignes.add(s.getId() + " | " + s.getDate().format(FORMAT_DATE)
                    + " | " + s.getEquipe().getNom()
                    + " | " + s.getLieu()
                    + " | présents : " + s.getPresents().size() + "/" + s.getEquipe().getEffectif()
                    + " | taux : " + formaterNombre(s.tauxPresence(), 2));
        }
        return lignes;
    }

    @Override
    protected String pied(Club club) {
        List<Seance> seances = club.getSeances();
        double somme = 0;
        for (Seance s : seances) {
            somme += s.tauxPresence();
        }
        double moyenne = seances.isEmpty() ? 0 : somme / seances.size();
        return "Taux moyen de présence : " + formaterNombre(moyenne, 2);
    }
}
