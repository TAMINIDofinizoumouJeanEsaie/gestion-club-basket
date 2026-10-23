package club.ui;

import club.exceptions.ClubException;
import club.exceptions.DonneeInvalideException;
import club.exceptions.ElementIntrouvableException;
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
import club.paiements.ModePaiement;
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

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

/**
 * Menu console de l'application.
 * Il ne connaît que la façade Club (et les interfaces/abstractions Stockage et Rapport) :
 * il ne modifie jamais directement les listes internes du club.
 * Toute erreur métier (ClubException) est affichée sans arrêter le programme.
 */
public class Menu {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Club club;
    private Scanner scanner;

    /**
     * Crée le menu pour un club donné.
     *
     * @param club le club à gérer
     */
    public Menu(Club club) {
        this.club = club;
        this.scanner = new Scanner(System.in);
    }

    // ------------------------------------------------------------------
    // Menu principal
    // ------------------------------------------------------------------

    /**
     * Affiche le menu principal et traite les choix jusqu'à la sortie.
     */
    public void demarrer() {
        boolean continuer = true;
        while (continuer) {
            System.out.println();
            System.out.println("===== " + club.getNom() + " =====");
            System.out.println("1. Administrateur");
            System.out.println("2. Entraîneur");
            System.out.println("3. Trésorier");
            System.out.println("4. Rapports");
            System.out.println("5. Statistiques (marqueurs et bilan)");
            System.out.println("0. Quitter");
            try {
                int choix = lireEntier("Votre choix");
                switch (choix) {
                    case 1:
                        menuAdministrateur();
                        break;
                    case 2:
                        menuEntraineur();
                        break;
                    case 3:
                        menuTresorier();
                        break;
                    case 4:
                        afficherRapports();
                        break;
                    case 5:
                        afficherStatistiques();
                        break;
                    case 0:
                        continuer = false;
                        System.out.println("Au revoir.");
                        break;
                    default:
                        System.out.println("Choix inconnu.");
                }
            } catch (ClubException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------
    // Section Administrateur
    // ------------------------------------------------------------------

    private void menuAdministrateur() {
        boolean retour = false;
        while (!retour) {
            System.out.println();
            System.out.println("--- Administrateur ---");
            System.out.println("1. Inscrire un joueur");
            System.out.println("2. Ajouter un entraîneur");
            System.out.println("3. Créer une équipe");
            System.out.println("4. Affecter un joueur à une équipe");
            System.out.println("5. Lister joueurs, entraîneurs et équipes");
            System.out.println("6. Sauvegarder les données");
            System.out.println("7. Charger les données");
            System.out.println("0. Retour");
            try {
                int choix = lireEntier("Votre choix");
                switch (choix) {
                    case 1:
                        inscrireJoueur();
                        break;
                    case 2:
                        ajouterEntraineur();
                        break;
                    case 3:
                        creerEquipe();
                        break;
                    case 4:
                        affecterJoueur();
                        break;
                    case 5:
                        listerTout();
                        break;
                    case 6:
                        sauvegarder();
                        break;
                    case 7:
                        charger();
                        break;
                    case 0:
                        retour = true;
                        break;
                    default:
                        System.out.println("Choix inconnu.");
                }
            } catch (ClubException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void inscrireJoueur() {
        String nom = lireTexte("Nom");
        String prenom = lireTexte("Prénom");
        String telephone = lireTexte("Téléphone (8 chiffres)");
        LocalDate naissance = lireDate("Date de naissance (jj/mm/aaaa)");
        int maillot = lireEntier("Numéro de maillot (0 à 99)");
        Poste poste = choisirPoste();
        Joueur joueur = new Joueur(club.prochainId("J"), nom, prenom, telephone, naissance, maillot, poste);
        club.inscrireJoueur(joueur);
        System.out.println("Joueur inscrit : " + joueur);
        System.out.println("Cotisation créée : " + Club.COTISATION_ANNUELLE + " FCFA");
    }

    private void ajouterEntraineur() {
        String nom = lireTexte("Nom");
        String prenom = lireTexte("Prénom");
        String telephone = lireTexte("Téléphone (8 chiffres)");
        LocalDate naissance = lireDate("Date de naissance (jj/mm/aaaa)");
        String specialite = lireTexte("Spécialité");
        int annees = lireEntier("Années d'expérience");
        Entraineur entraineur = new Entraineur(club.prochainId("E"), nom, prenom, telephone, naissance,
                specialite, annees);
        club.ajouterEntraineur(entraineur);
        System.out.println("Entraîneur ajouté : " + entraineur);
    }

    private void creerEquipe() {
        String nom = lireTexte("Nom de l'équipe");
        Categorie categorie = choisirCategorie();
        Entraineur entraineur = chercherEntraineur(lireTexte("Identifiant de l'entraîneur (ex : E-001)"));
        Equipe equipe = club.creerEquipe(nom, categorie, entraineur);
        System.out.println("Equipe créée : " + equipe);
    }

    private void affecterJoueur() {
        String idJoueur = lireTexte("Identifiant du joueur (ex : J-001)");
        String nomEquipe = lireTexte("Nom de l'équipe");
        club.affecterJoueur(idJoueur, nomEquipe);
        System.out.println("Joueur " + idJoueur + " affecté à l'équipe " + nomEquipe + ".");
    }

    private void listerTout() {
        System.out.println("--- Joueurs ---");
        for (Joueur j : club.getJoueurs()) {
            System.out.println(j);
        }
        System.out.println("--- Entraîneurs ---");
        for (Entraineur e : club.getEntraineurs()) {
            System.out.println(e);
        }
        System.out.println("--- Equipes ---");
        for (Equipe e : club.getEquipes()) {
            System.out.println(e);
        }
    }

    private void sauvegarder() {
        Stockage stockage = new StockageCsv();
        Path dossier = Path.of(lireTexte("Dossier de sauvegarde"));
        stockage.sauvegarder(club, dossier);
        System.out.println("Données sauvegardées dans : " + dossier);
    }

    private void charger() {
        Stockage stockage = new StockageCsv();
        Path dossier = Path.of(lireTexte("Dossier à charger"));
        club = stockage.charger(dossier);
        System.out.println("Données chargées depuis : " + dossier);
    }

    // ------------------------------------------------------------------
    // Section Entraîneur
    // ------------------------------------------------------------------

    private void menuEntraineur() {
        boolean retour = false;
        while (!retour) {
            System.out.println();
            System.out.println("--- Entraîneur ---");
            System.out.println("1. Planifier une séance");
            System.out.println("2. Marquer les présences d'une séance");
            System.out.println("3. Programmer un match");
            System.out.println("4. Enregistrer le score d'un match");
            System.out.println("5. Enregistrer les statistiques d'un match");
            System.out.println("6. Simuler un match");
            System.out.println("0. Retour");
            try {
                int choix = lireEntier("Votre choix");
                switch (choix) {
                    case 1:
                        planifierSeance();
                        break;
                    case 2:
                        marquerPresences();
                        break;
                    case 3:
                        programmerMatch();
                        break;
                    case 4:
                        enregistrerScore();
                        break;
                    case 5:
                        enregistrerStatistiques();
                        break;
                    case 6:
                        simulerMatch();
                        break;
                    case 0:
                        retour = true;
                        break;
                    default:
                        System.out.println("Choix inconnu.");
                }
            } catch (ClubException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void planifierSeance() {
        Equipe equipe = chercherEquipe(lireTexte("Nom de l'équipe"));
        LocalDateTime date = lireDateHeure("Date et heure (jj/mm/aaaa hh:mm)");
        String lieu = lireTexte("Lieu");
        Seance seance = new Seance(club.prochainId("S"), date, lieu, equipe);
        club.planifierSeance(seance);
        System.out.println("Séance planifiée : " + seance);
    }

    private void marquerPresences() {
        Seance seance = chercherSeance(lireTexte("Identifiant de la séance (ex : S-001)"));
        boolean fini = false;
        while (!fini) {
            String idJoueur = lireTexte("Identifiant du joueur présent (vide pour terminer)");
            if (idJoueur.isEmpty()) {
                fini = true;
            } else {
                try {
                    seance.marquerPresent(chercherJoueur(idJoueur));
                    System.out.println("Présence enregistrée.");
                } catch (ClubException e) {
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }
        System.out.println("Taux de présence : " + String.format("%.2f", seance.tauxPresence()));
    }

    private void programmerMatch() {
        Equipe equipe = chercherEquipe(lireTexte("Nom de l'équipe"));
        String adversaire = lireTexte("Adversaire");
        LocalDateTime date = lireDateHeure("Date et heure (jj/mm/aaaa hh:mm)");
        String reponse = lireTexte("Match à domicile ? (o/n)");
        boolean domicile = reponse.equalsIgnoreCase("o");
        Match match = new Match(club.prochainId("M"), equipe, adversaire, date, domicile);
        club.programmerMatch(match);
        System.out.println("Match programmé : " + match);
    }

    private void enregistrerScore() {
        Match match = chercherMatch(lireTexte("Identifiant du match (ex : M-001)"));
        int scoreEquipe = lireEntier("Score de notre équipe");
        int scoreAdversaire = lireEntier("Score de l'adversaire");
        match.enregistrerScore(scoreEquipe, scoreAdversaire);
        System.out.println("Score enregistré : " + match);
    }

    private void enregistrerStatistiques() {
        Match match = chercherMatch(lireTexte("Identifiant du match (ex : M-001)"));
        boolean fini = false;
        while (!fini) {
            String idJoueur = lireTexte("Identifiant du joueur (vide pour terminer)");
            if (idJoueur.isEmpty()) {
                fini = true;
            } else {
                try {
                    Joueur joueur = chercherJoueur(idJoueur);
                    int points = lireEntier("Points");
                    int rebonds = lireEntier("Rebonds");
                    int passes = lireEntier("Passes");
                    match.ajouterStat(new StatJoueur(joueur, points, rebonds, passes));
                    System.out.println("Statistique enregistrée.");
                } catch (ClubException e) {
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }
    }

    private void simulerMatch() {
        Match match = chercherMatch(lireTexte("Identifiant du match (ex : M-001)"));
        SimulateurMatch.simuler(match, new Random());
        System.out.println("Match simulé : " + match);
        for (StatJoueur s : match.getStats()) {
            System.out.println("  " + s);
        }
    }

    // ------------------------------------------------------------------
    // Section Trésorier
    // ------------------------------------------------------------------

    private void menuTresorier() {
        boolean retour = false;
        while (!retour) {
            System.out.println();
            System.out.println("--- Trésorier ---");
            System.out.println("1. Enregistrer un paiement de cotisation");
            System.out.println("2. Consulter l'état des cotisations");
            System.out.println("0. Retour");
            try {
                int choix = lireEntier("Votre choix");
                switch (choix) {
                    case 1:
                        enregistrerPaiement();
                        break;
                    case 2:
                        afficherCotisations();
                        break;
                    case 0:
                        retour = true;
                        break;
                    default:
                        System.out.println("Choix inconnu.");
                }
            } catch (ClubException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void enregistrerPaiement() {
        String idJoueur = lireTexte("Identifiant du joueur (ex : J-001)");
        double montant = lireDecimal("Montant à payer (FCFA)");
        // Le menu ne connaît que l'interface ModePaiement une fois le mode créé.
        ModePaiement mode = creerModePaiement();
        Paiement paiement = club.enregistrerPaiement(idJoueur, montant, mode);
        System.out.println("Paiement enregistré : " + paiement);
        System.out.println(mode.recu(montant));
        System.out.println("Frais : " + paiement.getFrais() + " FCFA");
        System.out.println("Total débité : " + paiement.getTotalDebite() + " FCFA");
        Optional<Cotisation> cotisation = club.trouverCotisation(idJoueur);
        if (cotisation.isPresent()) {
            System.out.println("Reste à payer : " + cotisation.get().getReste() + " FCFA");
        }
    }

    private ModePaiement creerModePaiement() {
        System.out.println("Mode de paiement :");
        System.out.println("1. Espèces");
        System.out.println("2. Mobile Money");
        System.out.println("3. Carte");
        int choix = lireEntier("Votre choix");
        if (choix == 1) {
            double remis = lireDecimal("Montant remis en espèces (FCFA)");
            return new Especes(remis);
        } else if (choix == 2) {
            Operateur operateur = choisirOperateur();
            String telephone = lireTexte("Téléphone du compte");
            double solde = lireDecimal("Solde du compte (FCFA)");
            return new MobileMoney(operateur, telephone, solde);
        } else if (choix == 3) {
            String numero = lireTexte("Numéro de la carte");
            double plafond = lireDecimal("Plafond de la carte (FCFA)");
            return new Carte(numero, plafond);
        } else {
            throw new DonneeInvalideException("Mode de paiement inconnu : " + choix);
        }
    }

    private void afficherCotisations() {
        System.out.println("--- Etat des cotisations (saison " + Club.SAISON_COURANTE + ") ---");
        for (Cotisation c : club.getCotisations()) {
            String statut = c.estSoldee() ? "SOLDEE" : "EN COURS";
            System.out.println(c.getJoueur().getNomComplet()
                    + " | payé : " + c.getMontantPaye()
                    + " | dû : " + c.getMontantDu()
                    + " | reste : " + c.getReste()
                    + " | " + statut);
        }
    }

    // ------------------------------------------------------------------
    // Rapports et statistiques (accessibles à tous)
    // ------------------------------------------------------------------

    private void afficherRapports() {
        // Polymorphisme : on appelle generer() sans connaître le type exact du rapport.
        Rapport[] rapports = {new RapportCotisations(), new RapportPresences(), new RapportMatchs()};
        for (Rapport rapport : rapports) {
            System.out.println();
            System.out.println(rapport.generer(club));
        }
    }

    private void afficherStatistiques() {
        System.out.println("--- Meilleurs marqueurs ---");
        List<StatJoueur> marqueurs = Classement.meilleursMarqueurs(club.getMatchs(), 5);
        int rang = 1;
        for (StatJoueur s : marqueurs) {
            System.out.println(rang + ". " + s.getJoueur().getNomComplet() + " : " + s.getPoints() + " points");
            rang++;
        }
        String nomEquipe = lireTexte("Nom de l'équipe pour le bilan");
        Equipe equipe = chercherEquipe(nomEquipe);
        Classement.Bilan bilan = Classement.bilan(club.getMatchs(), equipe);
        System.out.println("--- Bilan de " + equipe.getNom() + " ---");
        System.out.println("Victoires : " + bilan.victoires());
        System.out.println("Défaites : " + bilan.defaites());
        System.out.println("Points marqués : " + bilan.pointsMarques());
        System.out.println("Points encaissés : " + bilan.pointsEncaisses());
    }

    // ------------------------------------------------------------------
    // Recherches (lèvent ElementIntrouvableException si absent)
    // ------------------------------------------------------------------

    private Joueur chercherJoueur(String id) {
        Optional<Joueur> resultat = club.trouverJoueur(id);
        if (resultat.isEmpty()) {
            throw new ElementIntrouvableException("Joueur introuvable : " + id);
        }
        return resultat.get();
    }

    private Equipe chercherEquipe(String nom) {
        Optional<Equipe> resultat = club.trouverEquipe(nom);
        if (resultat.isEmpty()) {
            throw new ElementIntrouvableException("Equipe introuvable : " + nom);
        }
        return resultat.get();
    }

    private Seance chercherSeance(String id) {
        Optional<Seance> resultat = club.trouverSeance(id);
        if (resultat.isEmpty()) {
            throw new ElementIntrouvableException("Séance introuvable : " + id);
        }
        return resultat.get();
    }

    private Match chercherMatch(String id) {
        Optional<Match> resultat = club.trouverMatch(id);
        if (resultat.isEmpty()) {
            throw new ElementIntrouvableException("Match introuvable : " + id);
        }
        return resultat.get();
    }

    private Entraineur chercherEntraineur(String id) {
        // Le contrat n'a pas de trouverEntraineur : on parcourt la liste non modifiable du club.
        for (Entraineur e : club.getEntraineurs()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        throw new ElementIntrouvableException("Entraîneur introuvable : " + id);
    }

    // ------------------------------------------------------------------
    // Choix dans les énumérations
    // ------------------------------------------------------------------

    private Poste choisirPoste() {
        Poste[] postes = Poste.values();
        for (int i = 0; i < postes.length; i++) {
            System.out.println((i + 1) + ". " + postes[i]);
        }
        int choix = lireEntier("Poste");
        if (choix < 1 || choix > postes.length) {
            throw new DonneeInvalideException("Poste inconnu : " + choix);
        }
        return postes[choix - 1];
    }

    private Categorie choisirCategorie() {
        Categorie[] categories = Categorie.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int choix = lireEntier("Catégorie");
        if (choix < 1 || choix > categories.length) {
            throw new DonneeInvalideException("Catégorie inconnue : " + choix);
        }
        return categories[choix - 1];
    }

    private Operateur choisirOperateur() {
        Operateur[] operateurs = Operateur.values();
        for (int i = 0; i < operateurs.length; i++) {
            System.out.println((i + 1) + ". " + operateurs[i]);
        }
        int choix = lireEntier("Opérateur");
        if (choix < 1 || choix > operateurs.length) {
            throw new DonneeInvalideException("Opérateur inconnu : " + choix);
        }
        return operateurs[choix - 1];
    }

    // ------------------------------------------------------------------
    // Lecture au clavier
    // ------------------------------------------------------------------

    private String lireTexte(String invite) {
        System.out.print(invite + " : ");
        return scanner.nextLine().trim();
    }

    private int lireEntier(String invite) {
        String texte = lireTexte(invite);
        try {
            return Integer.parseInt(texte);
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Nombre entier attendu : " + texte);
        }
    }

    private double lireDecimal(String invite) {
        String texte = lireTexte(invite).replace(',', '.');
        try {
            return Double.parseDouble(texte);
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Nombre attendu : " + texte);
        }
    }

    private LocalDate lireDate(String invite) {
        String texte = lireTexte(invite);
        try {
            return LocalDate.parse(texte, FORMAT_DATE);
        } catch (DateTimeParseException e) {
            throw new DonneeInvalideException("Date invalide (format jj/mm/aaaa) : " + texte);
        }
    }

    private LocalDateTime lireDateHeure(String invite) {
        String texte = lireTexte(invite);
        try {
            return LocalDateTime.parse(texte, FORMAT_DATE_HEURE);
        } catch (DateTimeParseException e) {
            throw new DonneeInvalideException("Date invalide (format jj/mm/aaaa hh:mm) : " + texte);
        }
    }
}
