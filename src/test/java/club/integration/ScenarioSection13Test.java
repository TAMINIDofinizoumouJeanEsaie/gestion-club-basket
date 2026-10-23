package club.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import club.exceptions.DonneeInvalideException;
import club.exceptions.PaiementRefuseException;
import club.gestion.Club;
import club.matchs.Classement;
import club.matchs.Match;
import club.matchs.Seance;
import club.matchs.SimulateurMatch;
import club.matchs.StatJoueur;
import club.paiements.Carte;
import club.paiements.Cotisation;
import club.paiements.Especes;
import club.paiements.MobileMoney;
import club.paiements.Operateur;
import club.paiements.Paiement;
import club.personnes.Categorie;
import club.personnes.Entraineur;
import club.personnes.Equipe;
import club.personnes.Joueur;
import club.personnes.Poste;
import club.rapports.Rapport;
import club.rapports.RapportCotisations;
import club.rapports.RapportMatchs;
import club.rapports.RapportPresences;
import club.stockage.Stockage;
import club.stockage.StockageCsv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test d'intégration : rejoue le scénario de démonstration de la section 13
 * du document de référence. Tous les modules doivent donner ces valeurs.
 */
class ScenarioSection13Test {

    private Club club;
    private Entraineur issa;
    private Equipe etalons;
    private Joueur awa;
    private Joueur moussa;
    private Joueur fanta;

    /** Étapes 1 à 3 du scénario : club, entraîneur, équipe, joueurs, affectations. */
    @BeforeEach
    void preparer() {
        club = new Club("Basket Club Bobo");

        issa = new Entraineur(club.prochainId("E"), "Traore", "Issa", "70123456",
                LocalDate.of(1985, 3, 12), "Defense", 10);
        club.ajouterEntraineur(issa);
        etalons = club.creerEquipe("Etalons", Categorie.SENIOR, issa);

        awa = new Joueur(club.prochainId("J"), "Ouedraogo", "Awa", "70000001",
                LocalDate.of(2000, 5, 10), 7, Poste.MENEUR);
        moussa = new Joueur(club.prochainId("J"), "Sawadogo", "Moussa", "70000002",
                LocalDate.of(1999, 8, 21), 10, Poste.PIVOT);
        fanta = new Joueur(club.prochainId("J"), "Kone", "Fanta", "70000003",
                LocalDate.of(2001, 1, 30), 4, Poste.AILIER);
        club.inscrireJoueur(awa);
        club.inscrireJoueur(moussa);
        club.inscrireJoueur(fanta);

        club.affecterJoueur(awa.getId(), "Etalons");
        club.affecterJoueur(moussa.getId(), "Etalons");
        club.affecterJoueur(fanta.getId(), "Etalons");
    }

    /** Paie la cotisation d'Awa en deux fois (étapes 4 et 5). */
    private void payerCotisationAwa() {
        club.enregistrerPaiement(awa.getId(), 15000,
                new MobileMoney(Operateur.ORANGE_MONEY, "70000000", 30000));
        club.enregistrerPaiement(awa.getId(), 10000, new Carte("4970123412345678", 50000));
    }

    /** Programme et simule le match contre AS Faso (étape 9). */
    private Match simulerMatch() {
        Match match = new Match(club.prochainId("M"), etalons, "AS Faso",
                LocalDateTime.of(2026, 11, 8, 16, 0), true);
        club.programmerMatch(match);
        SimulateurMatch.simuler(match, new Random(42));
        return match;
    }

    @Test
    void identifiantsGeneresSelonRG01() {
        assertEquals("E-001", issa.getId());
        assertEquals("J-001", awa.getId());
        assertEquals("J-002", moussa.getId());
        assertEquals("J-003", fanta.getId());
    }

    @Test
    void chaqueJoueurRecoitUneCotisationDe25000() {
        assertEquals(3, club.getCotisations().size());
        for (Joueur j : List.of(awa, moussa, fanta)) {
            Cotisation c = club.trouverCotisation(j.getId()).get();
            assertEquals(25000.0, c.getMontantDu(), 0.001);
            assertEquals(0.0, c.getMontantPaye(), 0.001);
            assertFalse(c.estSoldee());
        }
    }

    @Test
    void lesTroisJoueursSontDansLEquipe() {
        assertEquals(3, etalons.getEffectif());
        assertTrue(etalons.trouverJoueur(awa.getId()).isPresent());
    }

    @Test
    void premierPaiementMobileMoneyDAwa() {
        Paiement p = club.enregistrerPaiement(awa.getId(), 15000,
                new MobileMoney(Operateur.ORANGE_MONEY, "70000000", 30000));
        assertEquals(150.0, p.getFrais(), 0.001);
        assertEquals(15150.0, p.getTotalDebite(), 0.001);
        assertEquals(10000.0, club.trouverCotisation(awa.getId()).get().getReste(), 0.001);
    }

    @Test
    void secondPaiementCarteSoldeLaCotisation() {
        Carte carte = new Carte("4970123412345678", 50000);
        club.enregistrerPaiement(awa.getId(), 15000,
                new MobileMoney(Operateur.ORANGE_MONEY, "70000000", 30000));
        Paiement p = club.enregistrerPaiement(awa.getId(), 10000, carte);
        assertEquals(200.0, p.getFrais(), 0.001);
        assertEquals(10200.0, p.getTotalDebite(), 0.001);
        assertEquals("Carte ****5678", carte.nom());
        Cotisation c = club.trouverCotisation(awa.getId()).get();
        assertTrue(c.estSoldee());
        assertEquals(0.0, c.getReste(), 0.001);
        assertEquals(2, club.getPaiements().size());
    }

    @Test
    void paiementSuperieurAuResteEstInvalide() {
        payerCotisationAwa();
        assertThrows(DonneeInvalideException.class,
                () -> club.enregistrerPaiement(awa.getId(), 5000, new Especes(5000)));
        assertEquals(2, club.getPaiements().size());
    }

    @Test
    void paiementRefuseNeModifieRien() {
        assertThrows(PaiementRefuseException.class,
                () -> club.enregistrerPaiement(moussa.getId(), 20000, new Especes(5000)));
        Cotisation c = club.trouverCotisation(moussa.getId()).get();
        assertEquals(0.0, c.getMontantPaye(), 0.001);
        assertEquals(25000.0, c.getReste(), 0.001);
        assertEquals(0, club.getPaiements().size());
    }

    @Test
    void seanceAvecDeuxPresentsSurTroisDonneUnTauxDeDeuxTiers() {
        Seance seance = new Seance(club.prochainId("S"), LocalDateTime.of(2026, 10, 26, 17, 0),
                "Stade municipal", etalons);
        club.planifierSeance(seance);
        seance.marquerPresent(awa);
        seance.marquerPresent(moussa);
        assertEquals(2, seance.getPresents().size());
        assertEquals(2.0 / 3.0, seance.tauxPresence(), 0.0001);
        assertEquals(0.67, seance.tauxPresence(), 0.005);
    }

    @Test
    void matchSimuleEstCoherent() {
        Match match = simulerMatch();
        assertTrue(match.isJoue());
        assertTrue(match.getScoreEquipe() >= 60 && match.getScoreEquipe() <= 102);
        assertTrue(match.getScoreAdversaire() >= 60 && match.getScoreAdversaire() <= 102);
        assertTrue(match.getScoreEquipe() != match.getScoreAdversaire());

        int somme = 0;
        for (StatJoueur s : match.getStats()) {
            somme += s.getPoints();
        }
        assertEquals(match.getScoreEquipe(), somme);
        assertEquals(3, match.getStats().size());
    }

    @Test
    void simulationReproductibleAvecLeMemeRandom() {
        Match m1 = simulerMatch();
        Match m2 = new Match(club.prochainId("M"), etalons, "AS Faso",
                LocalDateTime.of(2026, 11, 15, 16, 0), false);
        SimulateurMatch.simuler(m2, new Random(42));
        assertEquals(m1.getScoreEquipe(), m2.getScoreEquipe());
        assertEquals(m1.getScoreAdversaire(), m2.getScoreAdversaire());
    }

    @Test
    void classementEtBilanApresSimulation() {
        Match match = simulerMatch();
        List<StatJoueur> marqueurs = Classement.meilleursMarqueurs(club.getMatchs(), 3);
        assertEquals(3, marqueurs.size());
        for (int i = 1; i < marqueurs.size(); i++) {
            assertTrue(marqueurs.get(i - 1).getPoints() >= marqueurs.get(i).getPoints());
        }
        Classement.Bilan bilan = Classement.bilan(club.getMatchs(), etalons);
        assertEquals(1, bilan.victoires() + bilan.defaites());
        assertEquals(match.getScoreEquipe(), bilan.pointsMarques());
        assertEquals(match.getScoreAdversaire(), bilan.pointsEncaisses());
    }

    @Test
    void lesTroisRapportsSontGeneres() {
        payerCotisationAwa();
        simulerMatch();
        Rapport[] rapports = {new RapportCotisations(), new RapportPresences(), new RapportMatchs()};
        for (Rapport r : rapports) {
            String texte = r.generer(club);
            assertFalse(texte.isBlank());
        }
        assertTrue(new RapportCotisations().generer(club).contains("Ouedraogo"));
    }

    @Test
    void sauvegardePuisChargementDonnentDesRapportsIdentiques(@TempDir Path dossier) {
        payerCotisationAwa();
        Seance seance = new Seance(club.prochainId("S"), LocalDateTime.of(2026, 10, 26, 17, 0),
                "Stade municipal", etalons);
        club.planifierSeance(seance);
        seance.marquerPresent(awa);
        seance.marquerPresent(moussa);
        simulerMatch();

        Stockage stockage = new StockageCsv();
        stockage.sauvegarder(club, dossier);
        for (String nom : List.of("joueurs", "entraineurs", "equipes", "seances", "matchs",
                "paiements", "cotisations")) {
            assertTrue(Files.exists(dossier.resolve(nom + ".csv")), "Fichier manquant : " + nom + ".csv");
        }

        Club recharge = stockage.charger(dossier);
        assertEquals(club.getJoueurs().size(), recharge.getJoueurs().size());
        assertEquals(club.getPaiements().size(), recharge.getPaiements().size());

        Rapport[] rapports = {new RapportCotisations(), new RapportPresences(), new RapportMatchs()};
        for (Rapport r : rapports) {
            assertEquals(r.generer(club), r.generer(recharge));
        }
        // Les compteurs d'identifiants sont mis à jour au chargement (RG18).
        assertEquals("J-004", recharge.prochainId("J"));
    }
}
