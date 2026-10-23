package club.stockage;

import club.exceptions.ClubException;
import club.exceptions.DonneeInvalideException;
import club.exceptions.ElementIntrouvableException;
import club.gestion.Club;
import club.matchs.Match;
import club.matchs.Seance;
import club.matchs.StatJoueur;
import club.paiements.Cotisation;
import club.paiements.ModePaiement;
import club.paiements.Paiement;
import club.personnes.Categorie;
import club.personnes.Entraineur;
import club.personnes.Equipe;
import club.personnes.Joueur;
import club.personnes.Poste;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sauvegarde et chargement du club dans des fichiers CSV (RG18) :
 * UTF-8, séparateur point-virgule, première ligne d'en-tête.
 *
 * Fichiers : club.csv (nom du club), joueurs.csv, entraineurs.csv, equipes.csv,
 * seances.csv, matchs.csv, paiements.csv, cotisations.csv.
 * Les listes (joueurs d'une équipe, présents d'une séance, statistiques d'un match)
 * sont rangées dans une seule colonne, séparées par « | ».
 *
 * Concepts : implémentation d'une interface (Stockage) et classe interne privée
 * ModeArchive qui implémente ModePaiement.
 */
public class StockageCsv implements Stockage {

    private static final char SEPARATEUR = ';';
    private static final String SEPARATEUR_LISTE = "|";

    private static final String FICHIER_CLUB = "club.csv";
    private static final String FICHIER_JOUEURS = "joueurs.csv";
    private static final String FICHIER_ENTRAINEURS = "entraineurs.csv";
    private static final String FICHIER_EQUIPES = "equipes.csv";
    private static final String FICHIER_SEANCES = "seances.csv";
    private static final String FICHIER_MATCHS = "matchs.csv";
    private static final String FICHIER_PAIEMENTS = "paiements.csv";
    private static final String FICHIER_COTISATIONS = "cotisations.csv";

    /**
     * Mode de paiement « archivé » : un Paiement chargé depuis un fichier garde son montant,
     * ses frais et le NOM de son mode, mais les soldes ne sont pas sauvegardés (RG18).
     * Il ne sert qu'à l'affichage : on ne peut plus payer avec lui.
     */
    private static class ModeArchive implements ModePaiement {
        private final String nom;
        private final double frais;

        ModeArchive(String nom, double frais) {
            this.nom = nom;
            this.frais = frais;
        }

        @Override
        public boolean payer(double montant) {
            return false;
        }

        @Override
        public String nom() {
            return nom;
        }

        @Override
        public double frais(double montant) {
            return frais;
        }
    }

    // ------------------------------------------------------------------
    // Sauvegarde
    // ------------------------------------------------------------------

    @Override
    public void sauvegarder(Club club, Path dossier) {
        if (club == null || dossier == null) {
            throw new DonneeInvalideException("Le club et le dossier sont obligatoires pour sauvegarder.");
        }
        try {
            Files.createDirectories(dossier);
            ecrireClub(club, dossier);
            ecrireJoueurs(club, dossier);
            ecrireEntraineurs(club, dossier);
            ecrireEquipes(club, dossier);
            ecrireSeances(club, dossier);
            ecrireMatchs(club, dossier);
            ecrirePaiements(club, dossier);
            ecrireCotisations(club, dossier);
        } catch (IOException e) {
            throw new ClubException("Erreur pendant la sauvegarde : " + e.getMessage());
        }
    }

    private void ecrireClub(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        lignes.add(creerLigne(club.getNom()));
        ecrireFichier(dossier, FICHIER_CLUB, "nom", lignes);
    }

    private void ecrireJoueurs(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Joueur j : club.getJoueurs()) {
            lignes.add(creerLigne(j.getId(), j.getNom(), j.getPrenom(), j.getTelephone(),
                    j.getDateNaissance().toString(), String.valueOf(j.getNumeroMaillot()), j.getPoste().name()));
        }
        ecrireFichier(dossier, FICHIER_JOUEURS, "id;nom;prenom;telephone;dateNaissance;numeroMaillot;poste", lignes);
    }

    private void ecrireEntraineurs(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Entraineur e : club.getEntraineurs()) {
            lignes.add(creerLigne(e.getId(), e.getNom(), e.getPrenom(), e.getTelephone(),
                    e.getDateNaissance().toString(), e.getSpecialite(), String.valueOf(e.getAnneesExperience())));
        }
        ecrireFichier(dossier, FICHIER_ENTRAINEURS,
                "id;nom;prenom;telephone;dateNaissance;specialite;anneesExperience", lignes);
    }

    private void ecrireEquipes(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Equipe eq : club.getEquipes()) {
            List<String> idsJoueurs = new ArrayList<>();
            for (Joueur j : eq.getJoueurs()) {
                idsJoueurs.add(j.getId());
            }
            lignes.add(creerLigne(eq.getNom(), eq.getCategorie().name(), eq.getEntraineur().getId(),
                    String.join(SEPARATEUR_LISTE, idsJoueurs)));
        }
        ecrireFichier(dossier, FICHIER_EQUIPES, "nom;categorie;idEntraineur;idsJoueurs", lignes);
    }

    private void ecrireSeances(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Seance s : club.getSeances()) {
            List<String> idsPresents = new ArrayList<>();
            for (Joueur j : s.getPresents()) {
                idsPresents.add(j.getId());
            }
            lignes.add(creerLigne(s.getId(), s.getDate().toString(), s.getLieu(), s.getEquipe().getNom(),
                    String.join(SEPARATEUR_LISTE, idsPresents)));
        }
        ecrireFichier(dossier, FICHIER_SEANCES, "id;dateHeure;lieu;nomEquipe;idsPresents", lignes);
    }

    private void ecrireMatchs(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Match m : club.getMatchs()) {
            // Une statistique = idJoueur:points:rebonds:passes
            List<String> stats = new ArrayList<>();
            for (StatJoueur s : m.getStats()) {
                stats.add(s.getJoueur().getId() + ":" + s.getPoints() + ":" + s.getRebonds() + ":" + s.getPasses());
            }
            lignes.add(creerLigne(m.getId(), m.getEquipe().getNom(), m.getAdversaire(), m.getDate().toString(),
                    String.valueOf(m.isDomicile()), String.valueOf(m.isJoue()),
                    String.valueOf(m.getScoreEquipe()), String.valueOf(m.getScoreAdversaire()),
                    String.join(SEPARATEUR_LISTE, stats)));
        }
        ecrireFichier(dossier, FICHIER_MATCHS,
                "id;nomEquipe;adversaire;dateHeure;domicile;joue;scoreEquipe;scoreAdversaire;stats", lignes);
    }

    private void ecrirePaiements(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Paiement p : club.getPaiements()) {
            lignes.add(creerLigne(p.getId(), p.getJoueur().getId(), String.valueOf(p.getMontant()),
                    String.valueOf(p.getFrais()), p.getMode().nom(), p.getDate().toString()));
        }
        ecrireFichier(dossier, FICHIER_PAIEMENTS, "id;idJoueur;montant;frais;nomMode;date", lignes);
    }

    private void ecrireCotisations(Club club, Path dossier) throws IOException {
        List<String> lignes = new ArrayList<>();
        for (Cotisation c : club.getCotisations()) {
            lignes.add(creerLigne(c.getId(), c.getJoueur().getId(), String.valueOf(c.getSaison()),
                    String.valueOf(c.getMontantDu()), String.valueOf(c.getMontantPaye())));
        }
        ecrireFichier(dossier, FICHIER_COTISATIONS, "id;idJoueur;saison;montantDu;montantPaye", lignes);
    }

    // ------------------------------------------------------------------
    // Chargement : les liens entre objets sont reconstruits avec les identifiants,
    // donc l'ordre de lecture compte (joueurs et entraîneurs avant les équipes, etc.)
    // ------------------------------------------------------------------

    @Override
    public Club charger(Path dossier) {
        if (dossier == null || !Files.isDirectory(dossier)) {
            throw new ElementIntrouvableException("Dossier de sauvegarde introuvable : " + dossier);
        }
        try {
            Club club = new Club(lireNomClub(dossier));
            lireJoueurs(club, dossier);
            lireEntraineurs(club, dossier);
            lireEquipes(club, dossier);
            lireSeances(club, dossier);
            lireMatchs(club, dossier);
            lireCotisations(club, dossier);
            lirePaiements(club, dossier);
            return club;
        } catch (IOException e) {
            throw new ClubException("Erreur pendant le chargement : " + e.getMessage());
        } catch (IllegalArgumentException | DateTimeException e) {
            // nombre mal écrit, poste ou catégorie inconnus, date incorrecte...
            throw new DonneeInvalideException("Fichier de sauvegarde incorrect : " + e.getMessage());
        }
    }

    private String lireNomClub(Path dossier) throws IOException {
        List<List<String>> lignes = lireFichier(dossier, FICHIER_CLUB, 1);
        if (lignes.isEmpty()) {
            throw new DonneeInvalideException("Le fichier " + FICHIER_CLUB + " ne contient pas le nom du club.");
        }
        return lignes.get(0).get(0);
    }

    private void lireJoueurs(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_JOUEURS, 7)) {
            Joueur j = new Joueur(c.get(0), c.get(1), c.get(2), c.get(3), LocalDate.parse(c.get(4)),
                    Integer.parseInt(c.get(5)), Poste.valueOf(c.get(6)));
            club.restaurerJoueur(j);
        }
    }

    private void lireEntraineurs(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_ENTRAINEURS, 7)) {
            Entraineur e = new Entraineur(c.get(0), c.get(1), c.get(2), c.get(3), LocalDate.parse(c.get(4)),
                    c.get(5), Integer.parseInt(c.get(6)));
            club.restaurerEntraineur(e);
        }
    }

    private void lireEquipes(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_EQUIPES, 4)) {
            Entraineur entraineur = trouverEntraineur(club, c.get(2));
            Equipe equipe = new Equipe(c.get(0), Categorie.valueOf(c.get(1)), entraineur);
            for (String idJoueur : separerListe(c.get(3))) {
                equipe.ajouterJoueur(trouverJoueur(club, idJoueur));
            }
            club.restaurerEquipe(equipe);
        }
    }

    private void lireSeances(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_SEANCES, 5)) {
            Seance seance = new Seance(c.get(0), LocalDateTime.parse(c.get(1)), c.get(2),
                    trouverEquipe(club, c.get(3)));
            for (String idJoueur : separerListe(c.get(4))) {
                seance.marquerPresent(trouverJoueur(club, idJoueur));
            }
            club.restaurerSeance(seance);
        }
    }

    private void lireMatchs(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_MATCHS, 9)) {
            Match match = new Match(c.get(0), trouverEquipe(club, c.get(1)), c.get(2),
                    LocalDateTime.parse(c.get(3)), Boolean.parseBoolean(c.get(4)));
            // Le score d'abord, puis les statistiques (RG13 et RG14 exigent cet ordre).
            if (Boolean.parseBoolean(c.get(5))) {
                match.enregistrerScore(Integer.parseInt(c.get(6)), Integer.parseInt(c.get(7)));
            }
            for (String stat : separerListe(c.get(8))) {
                String[] morceaux = stat.split(":");
                if (morceaux.length != 4) {
                    throw new DonneeInvalideException("Statistique incorrecte dans " + FICHIER_MATCHS + " : " + stat);
                }
                match.ajouterStat(new StatJoueur(trouverJoueur(club, morceaux[0]),
                        Integer.parseInt(morceaux[1]), Integer.parseInt(morceaux[2]),
                        Integer.parseInt(morceaux[3])));
            }
            club.restaurerMatch(match);
        }
    }

    private void lireCotisations(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_COTISATIONS, 5)) {
            Cotisation cotisation = new Cotisation(c.get(0), trouverJoueur(club, c.get(1)),
                    Integer.parseInt(c.get(2)), Double.parseDouble(c.get(3)));
            double paye = Double.parseDouble(c.get(4));
            if (paye > 0) {
                cotisation.ajouterVersement(paye);
            }
            club.restaurerCotisation(cotisation);
        }
    }

    private void lirePaiements(Club club, Path dossier) throws IOException {
        for (List<String> c : lireFichier(dossier, FICHIER_PAIEMENTS, 6)) {
            // Polymorphisme : un Paiement ne connaît que l'interface ModePaiement.
            ModePaiement mode = new ModeArchive(c.get(4), Double.parseDouble(c.get(3)));
            Paiement paiement = new Paiement(c.get(0), trouverJoueur(club, c.get(1)),
                    Double.parseDouble(c.get(2)), mode, LocalDateTime.parse(c.get(5)));
            club.restaurerPaiement(paiement);
        }
    }

    // ------------------------------------------------------------------
    // Recherches par identifiant (reconstruction des liens)
    // ------------------------------------------------------------------

    private Joueur trouverJoueur(Club club, String id) {
        return club.trouverJoueur(id)
                .orElseThrow(() -> new ElementIntrouvableException("Joueur introuvable dans la sauvegarde : " + id));
    }

    private Equipe trouverEquipe(Club club, String nom) {
        return club.trouverEquipe(nom)
                .orElseThrow(() -> new ElementIntrouvableException("Équipe introuvable dans la sauvegarde : " + nom));
    }

    private Entraineur trouverEntraineur(Club club, String id) {
        for (Entraineur e : club.getEntraineurs()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        throw new ElementIntrouvableException("Entraîneur introuvable dans la sauvegarde : " + id);
    }

    // ------------------------------------------------------------------
    // Outils CSV
    // ------------------------------------------------------------------

    private void ecrireFichier(Path dossier, String nomFichier, String entete, List<String> lignes)
            throws IOException {
        List<String> contenu = new ArrayList<>();
        contenu.add(entete);
        contenu.addAll(lignes);
        Files.write(dossier.resolve(nomFichier), contenu, StandardCharsets.UTF_8);
    }

    /** Lit un fichier, saute l'en-tête et les lignes vides, et vérifie le nombre de colonnes. */
    private List<List<String>> lireFichier(Path dossier, String nomFichier, int nombreColonnes)
            throws IOException {
        Path fichier = dossier.resolve(nomFichier);
        if (!Files.exists(fichier)) {
            throw new ElementIntrouvableException("Fichier introuvable : " + fichier);
        }
        List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
        List<List<String>> resultat = new ArrayList<>();
        for (int i = 1; i < lignes.size(); i++) { // i = 0 est l'en-tête
            if (lignes.get(i).isBlank()) {
                continue;
            }
            List<String> champs = decouper(lignes.get(i));
            if (champs.size() != nombreColonnes) {
                throw new DonneeInvalideException(
                        "Ligne " + (i + 1) + " incorrecte dans " + nomFichier + " : "
                                + nombreColonnes + " colonnes attendues, " + champs.size() + " trouvées.");
            }
            resultat.add(champs);
        }
        return resultat;
    }

    /** Assemble des champs en une ligne CSV, en protégeant ceux qui contiennent un « ; » ou un guillemet. */
    private String creerLigne(String... champs) {
        StringBuilder ligne = new StringBuilder();
        for (int i = 0; i < champs.length; i++) {
            if (i > 0) {
                ligne.append(SEPARATEUR);
            }
            ligne.append(echapper(champs[i]));
        }
        return ligne.toString();
    }

    private String echapper(String valeur) {
        String texte = (valeur == null) ? "" : valeur.replace('\r', ' ').replace('\n', ' ');
        if (texte.indexOf(SEPARATEUR) >= 0 || texte.indexOf('"') >= 0) {
            return "\"" + texte.replace("\"", "\"\"") + "\"";
        }
        return texte;
    }

    /** Découpe une ligne CSV en champs, en comprenant les champs entre guillemets. */
    private List<String> decouper(String ligne) {
        List<String> champs = new ArrayList<>();
        StringBuilder courant = new StringBuilder();
        boolean entreGuillemets = false;
        for (int i = 0; i < ligne.length(); i++) {
            char c = ligne.charAt(i);
            if (entreGuillemets) {
                if (c == '"') {
                    if (i + 1 < ligne.length() && ligne.charAt(i + 1) == '"') {
                        courant.append('"'); // deux guillemets de suite = un guillemet
                        i++;
                    } else {
                        entreGuillemets = false;
                    }
                } else {
                    courant.append(c);
                }
            } else if (c == '"') {
                entreGuillemets = true;
            } else if (c == SEPARATEUR) {
                champs.add(courant.toString());
                courant.setLength(0);
            } else {
                courant.append(c);
            }
        }
        champs.add(courant.toString());
        return champs;
    }

    /** Sépare une colonne qui contient une liste (ids séparés par « | »). Texte vide = liste vide. */
    private List<String> separerListe(String texte) {
        if (texte.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(texte.split("\\" + SEPARATEUR_LISTE));
    }

    @Override
    public String toString() {
        return "StockageCsv{format=CSV UTF-8, séparateur=';'}";
    }
}
