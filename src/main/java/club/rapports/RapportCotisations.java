package club.rapports;

import club.gestion.Club;
import club.paiements.Cotisation;

import java.util.ArrayList;
import java.util.List;

/**
 * Rapport des cotisations (RG17) : une ligne par joueur avec le montant payé,
 * le montant dû et le statut, puis le total encaissé et le total restant.
 *
 * Concepts : héritage et redéfinition (overriding) des méthodes de Rapport.
 */
public class RapportCotisations extends Rapport {

    @Override
    protected String titre() {
        return "RAPPORT DES COTISATIONS";
    }

    @Override
    protected List<String> lignes(Club club) {
        List<String> lignes = new ArrayList<>();
        for (Cotisation c : club.getCotisations()) {
            lignes.add(c.getJoueur().getId() + " " + c.getJoueur().getNomComplet()
                    + " | payé : " + formaterMontant(c.getMontantPaye())
                    + " | dû : " + formaterMontant(c.getMontantDu())
                    + " | statut : " + statut(c));
        }
        return lignes;
    }

    @Override
    protected String pied(Club club) {
        double totalEncaisse = 0;
        double totalRestant = 0;
        for (Cotisation c : club.getCotisations()) {
            totalEncaisse += c.getMontantPaye();
            totalRestant += c.getReste();
        }
        return "Total encaissé : " + formaterMontant(totalEncaisse) + "\n"
                + "Total restant : " + formaterMontant(totalRestant);
    }

    /** Statut lisible d'une cotisation : soldée, partielle ou impayée. */
    private String statut(Cotisation c) {
        if (c.estSoldee()) {
            return "SOLDÉE";
        }
        if (c.getMontantPaye() > 0) {
            return "PARTIELLE";
        }
        return "IMPAYÉE";
    }
}
