package club.gestion;

import club.exceptions.DonneeInvalideException;
import club.exceptions.ElementIntrouvableException;
import club.exceptions.PaiementRefuseException;
import club.matchs.Match;
import club.matchs.Seance;
import club.paiements.Cotisation;
import club.paiements.ModePaiement;
import club.paiements.Paiement;
import club.personnes.Categorie;
import club.personnes.Entraineur;
import club.personnes.Equipe;
import club.personnes.Joueur;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Façade du club : elle regroupe toutes les données (joueurs, entraîneurs, équipes,
 * séances, matchs, paiements, cotisations) et toutes les opérations de gestion.
 * Le menu ne manipule jamais les listes internes : il passe uniquement par cette classe.
 *
 * Concepts : composition (le Club contient ses listes), encapsulation (listes privées,
 * getters non modifiables) et programmation vers une interface (le Club ne connaît
 * que ModePaiement, jamais Especes, MobileMoney ou Carte).
 */
public class Club {

    /** Cotisation annuelle par joueur, en FCFA (RG08). */
    public static final double COTISATION_ANNUELLE = 25000.0;

    /** Saison courante (RG08). */
    public static final int SAISON_COURANTE = 2026;

    private String nom;
    private final List<Joueur> joueurs = new ArrayList<>();
    private final List<Entraineur> entraineurs = new ArrayList<>();
    private final List<Equipe> equipes = new ArrayList<>();
    private final List<Seance> seances = new ArrayList<>();
    private final List<Match> matchs = new ArrayList<>();
    private final List<Paiement> paiements = new ArrayList<>();
    private final List<Cotisation> cotisations = new ArrayList<>();

    /** Dernier numéro attribué pour chaque préfixe (J, E, EQ, S, M, P, C). */
    private final Map<String, Integer> compteurs = new HashMap<>();

    /**
     * Crée un club vide.
     *
     * @param nom nom du club (non vide)
     * @throws DonneeInvalideException si le nom est vide
     */
    public Club(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom du club ne peut pas être vide.");
        }
        this.nom = nom.trim();
    }

    // ------------------------------------------------------------------
    // Identifiants (RG01)
    // ------------------------------------------------------------------

    /**
     * Génère le prochain identifiant unique pour un préfixe, par exemple J-001, J-002...
     *
     * @param prefixe J, E, EQ, S, M, P ou C
     * @return l'identifiant au format PREFIXE-NNN
     * @throws DonneeInvalideException si le préfixe est vide
     */
    public String prochainId(String prefixe) {
        if (prefixe == null || prefixe.isBlank()) {
            throw new DonneeInvalideException("Le préfixe d'identifiant ne peut pas être vide.");
        }
        int numero = compteurs.getOrDefault(prefixe, 0) + 1;
        compteurs.put(prefixe, numero);
        return String.format("%s-%03d", prefixe, numero);
    }

    // ------------------------------------------------------------------
    // Opérations de gestion
    // ------------------------------------------------------------------

    /**
     * Inscrit un joueur et crée automatiquement sa cotisation de la saison (RG08).
     *
     * @param j le joueur à inscrire
     * @throws DonneeInvalideException si le joueur est absent ou déjà inscrit
     */
    public void inscrireJoueur(Joueur j) {
        if (j == null) {
            throw new DonneeInvalideException("Le joueur est obligatoire.");
        }
        if (trouverJoueur(j.getId()).isPresent()) {
            throw new DonneeInvalideException("Le joueur " + j.getId() + " est déjà inscrit.");
        }
        joueurs.add(j);
        cotisations.add(new Cotisation(prochainId("C"), j, SAISON_COURANTE, COTISATION_ANNUELLE));
    }

    /**
     * Ajoute un entraîneur au club.
     *
     * @param e l'entraîneur à ajouter
     * @throws DonneeInvalideException si l'entraîneur est absent ou déjà enregistré
     */
    public void ajouterEntraineur(Entraineur e) {
        if (e == null) {
            throw new DonneeInvalideException("L'entraîneur est obligatoire.");
        }
        if (entraineurEstDansLeClub(e)) {
            throw new DonneeInvalideException("L'entraîneur " + e.getId() + " existe déjà.");
        }
        entraineurs.add(e);
    }

    /**
     * Crée une équipe dirigée par un entraîneur du club (RG07 : un entraîneur dirige au plus une équipe).
     *
     * @param nom        nom de l'équipe (unique dans le club)
     * @param categorie  catégorie de l'équipe
     * @param entraineur entraîneur déjà ajouté au club
     * @return l'équipe créée
     * @throws DonneeInvalideException     si une donnée est invalide, si le nom existe déjà
     *                                     ou si l'entraîneur dirige déjà une équipe
     * @throws ElementIntrouvableException si l'entraîneur n'est pas dans le club
     */
    public Equipe creerEquipe(String nom, Categorie categorie, Entraineur entraineur) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom de l'équipe ne peut pas être vide.");
        }
        if (categorie == null) {
            throw new DonneeInvalideException("La catégorie de l'équipe est obligatoire.");
        }
        if (entraineur == null) {
            throw new DonneeInvalideException("L'entraîneur de l'équipe est obligatoire.");
        }
        if (!entraineurEstDansLeClub(entraineur)) {
            throw new ElementIntrouvableException("Entraîneur introuvable : " + entraineur.getId());
        }
        if (trouverEquipe(nom).isPresent()) {
            throw new DonneeInvalideException("Une équipe nommée « " + nom + " » existe déjà.");
        }
        for (Equipe e : equipes) {
            if (e.getEntraineur() != null && e.getEntraineur().getId().equals(entraineur.getId())) {
                throw new DonneeInvalideException(
                        "L'entraîneur " + entraineur.getId() + " dirige déjà l'équipe " + e.getNom() + ".");
            }
        }
        Equipe equipe = new Equipe(nom.trim(), categorie, entraineur);
        // L'équipe n'a pas d'attribut id dans le contrat : on consomme quand même le
        // numéro EQ-NNN pour respecter RG01 et garder des compteurs cohérents.
        prochainId("EQ");
        equipes.add(equipe);
        return equipe;
    }

    /**
     * Affecte un joueur à une équipe (RG04, RG05, RG06 vérifiées par Equipe.ajouterJoueur).
     * Un joueur n'appartient qu'à une seule équipe : on le retire de l'équipe précédente,
     * mais seulement après que l'ajout dans la nouvelle équipe a réussi.
     *
     * @param idJoueur  identifiant du joueur
     * @param nomEquipe nom de l'équipe d'accueil
     * @throws ElementIntrouvableException si le joueur ou l'équipe n'existe pas
     * @throws DonneeInvalideException     si le joueur est déjà dans cette équipe ou si une règle d'équipe est violée
     */
    public void affecterJoueur(String idJoueur, String nomEquipe) {
        Joueur joueur = trouverJoueur(idJoueur)
                .orElseThrow(() -> new ElementIntrouvableException("Joueur introuvable : " + idJoueur));
        Equipe nouvelle = trouverEquipe(nomEquipe)
                .orElseThrow(() -> new ElementIntrouvableException("Équipe introuvable : " + nomEquipe));

        if (nouvelle.trouverJoueur(idJoueur).isPresent()) {
            throw new DonneeInvalideException(
                    "Le joueur " + idJoueur + " est déjà dans l'équipe " + nouvelle.getNom() + ".");
        }

        // Ajout d'abord : si une règle échoue, une exception est levée et l'ancienne équipe reste intacte.
        nouvelle.ajouterJoueur(joueur);

        for (Equipe autre : equipes) {
            if (autre != nouvelle && autre.trouverJoueur(idJoueur).isPresent()) {
                autre.retirerJoueur(idJoueur);
            }
        }
    }

    /**
     * Planifie une séance d'entraînement d'une équipe du club.
     *
     * @param s la séance
     * @throws DonneeInvalideException     si la séance est absente ou son identifiant existe déjà
     * @throws ElementIntrouvableException si l'équipe de la séance n'est pas dans le club
     */
    public void planifierSeance(Seance s) {
        if (s == null) {
            throw new DonneeInvalideException("La séance est obligatoire.");
        }
        if (trouverSeance(s.getId()).isPresent()) {
            throw new DonneeInvalideException("La séance " + s.getId() + " existe déjà.");
        }
        verifierEquipeDuClub(s.getEquipe());
        seances.add(s);
    }

    /**
     * Programme un match d'une équipe du club.
     *
     * @param m le match
     * @throws DonneeInvalideException     si le match est absent ou son identifiant existe déjà
     * @throws ElementIntrouvableException si l'équipe du match n'est pas dans le club
     */
    public void programmerMatch(Match m) {
        if (m == null) {
            throw new DonneeInvalideException("Le match est obligatoire.");
        }
        if (trouverMatch(m.getId()).isPresent()) {
            throw new DonneeInvalideException("Le match " + m.getId() + " existe déjà.");
        }
        verifierEquipeDuClub(m.getEquipe());
        matchs.add(m);
    }

    /**
     * Enregistre le paiement (partiel ou total) de la cotisation d'un joueur.
     * Ordre des vérifications (important) :
     * 1. le joueur et sa cotisation existent ;
     * 2. RG09 : montant supérieur à 0 et inférieur ou égal au reste à payer,
     *    AVANT de toucher au mode de paiement ;
     * 3. RG11 : le mode de paiement est débité ; s'il refuse, rien n'est modifié.
     * Seulement ensuite la cotisation et la liste des paiements sont mises à jour.
     *
     * @param idJoueur identifiant du joueur
     * @param montant  montant versé en FCFA (hors frais)
     * @param mode     mode de paiement (vu uniquement comme une interface)
     * @return le paiement créé
     * @throws ElementIntrouvableException si le joueur ou sa cotisation n'existe pas
     * @throws DonneeInvalideException     si le mode est absent ou si le montant est invalide
     * @throws PaiementRefuseException     si le mode de paiement refuse l'opération
     */
    public Paiement enregistrerPaiement(String idJoueur, double montant, ModePaiement mode) {
        Joueur joueur = trouverJoueur(idJoueur)
                .orElseThrow(() -> new ElementIntrouvableException("Joueur introuvable : " + idJoueur));
        Cotisation cotisation = trouverCotisation(idJoueur)
                .orElseThrow(() -> new ElementIntrouvableException("Cotisation introuvable pour : " + idJoueur));
        if (mode == null) {
            throw new DonneeInvalideException("Le mode de paiement est obligatoire.");
        }

        // RG09 : on vérifie le montant AVANT de débiter. La forme !(montant > 0) refuse aussi NaN.
        if (!(montant > 0) || montant > cotisation.getReste()) {
            throw new DonneeInvalideException("Montant invalide : " + montant
                    + " FCFA (doit être supérieur à 0 et au plus " + cotisation.getReste() + " FCFA).");
        }

        // RG11 : polymorphisme, on appelle payer() sans savoir si c'est Especes, MobileMoney ou Carte.
        if (!mode.payer(montant)) {
            throw new PaiementRefuseException("Paiement refusé par le mode « " + mode.nom() + " ».");
        }

        cotisation.ajouterVersement(montant);
        Paiement paiement = new Paiement(prochainId("P"), joueur, montant, mode, LocalDateTime.now());
        paiements.add(paiement);
        return paiement;
    }

    // ------------------------------------------------------------------
    // Recherches
    // ------------------------------------------------------------------

    /** Cherche un joueur par son identifiant. */
    public Optional<Joueur> trouverJoueur(String id) {
        for (Joueur j : joueurs) {
            if (j.getId().equals(id)) {
                return Optional.of(j);
            }
        }
        return Optional.empty();
    }

    /** Cherche une équipe par son nom (majuscules et minuscules ignorées). */
    public Optional<Equipe> trouverEquipe(String nom) {
        if (nom == null) {
            return Optional.empty();
        }
        for (Equipe e : equipes) {
            if (e.getNom().equalsIgnoreCase(nom.trim())) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    /** Cherche une séance par son identifiant. */
    public Optional<Seance> trouverSeance(String id) {
        for (Seance s : seances) {
            if (s.getId().equals(id)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    /** Cherche un match par son identifiant. */
    public Optional<Match> trouverMatch(String id) {
        for (Match m : matchs) {
            if (m.getId().equals(id)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    /** Cherche la cotisation d'un joueur à partir de l'identifiant du joueur. */
    public Optional<Cotisation> trouverCotisation(String idJoueur) {
        for (Cotisation c : cotisations) {
            if (c.getJoueur().getId().equals(idJoueur)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    // ------------------------------------------------------------------
    // Getters (listes non modifiables : encapsulation)
    // ------------------------------------------------------------------

    public String getNom() {
        return nom;
    }

    public List<Joueur> getJoueurs() {
        return List.copyOf(joueurs);
    }

    public List<Entraineur> getEntraineurs() {
        return List.copyOf(entraineurs);
    }

    public List<Equipe> getEquipes() {
        return List.copyOf(equipes);
    }

    public List<Seance> getSeances() {
        return List.copyOf(seances);
    }

    public List<Match> getMatchs() {
        return List.copyOf(matchs);
    }

    public List<Paiement> getPaiements() {
        return List.copyOf(paiements);
    }

    public List<Cotisation> getCotisations() {
        return List.copyOf(cotisations);
    }

    // ------------------------------------------------------------------
    // Restauration (utilisée par le chargement : pas de règle de création,
    // mais les compteurs d'identifiants sont mis à jour - RG18)
    // ------------------------------------------------------------------

    /** Remet un joueur chargé dans le club, sans créer de cotisation. */
    public void restaurerJoueur(Joueur j) {
        joueurs.add(j);
        mettreAJourCompteur("J", j.getId());
    }

    /** Remet un entraîneur chargé dans le club. */
    public void restaurerEntraineur(Entraineur e) {
        entraineurs.add(e);
        mettreAJourCompteur("E", e.getId());
    }

    /** Remet une équipe chargée dans le club (l'équipe n'a pas d'id : le compteur EQ suit leur nombre). */
    public void restaurerEquipe(Equipe e) {
        equipes.add(e);
        compteurs.put("EQ", Math.max(compteurs.getOrDefault("EQ", 0), equipes.size()));
    }

    /** Remet une séance chargée dans le club. */
    public void restaurerSeance(Seance s) {
        seances.add(s);
        mettreAJourCompteur("S", s.getId());
    }

    /** Remet un match chargé dans le club. */
    public void restaurerMatch(Match m) {
        matchs.add(m);
        mettreAJourCompteur("M", m.getId());
    }

    /** Remet une cotisation chargée dans le club. */
    public void restaurerCotisation(Cotisation c) {
        cotisations.add(c);
        mettreAJourCompteur("C", c.getId());
    }

    /** Remet un paiement chargé dans le club (sans modifier la cotisation, déjà restaurée). */
    public void restaurerPaiement(Paiement p) {
        paiements.add(p);
        mettreAJourCompteur("P", p.getId());
    }

    // ------------------------------------------------------------------
    // Méthodes privées
    // ------------------------------------------------------------------

    /**
     * Lit le numéro à la fin d'un identifiant (J-007 donne 7) et garde le plus grand,
     * pour que le prochain identifiant généré après un chargement ne crée pas de doublon.
     */
    private void mettreAJourCompteur(String prefixe, String id) {
        int tiret = id.lastIndexOf('-');
        if (tiret < 0) {
            return;
        }
        try {
            int numero = Integer.parseInt(id.substring(tiret + 1));
            if (numero > compteurs.getOrDefault(prefixe, 0)) {
                compteurs.put(prefixe, numero);
            }
        } catch (NumberFormatException e) {
            // identifiant d'un autre format : on ne touche pas au compteur
        }
    }

    private boolean entraineurEstDansLeClub(Entraineur entraineur) {
        for (Entraineur e : entraineurs) {
            if (e.getId().equals(entraineur.getId())) {
                return true;
            }
        }
        return false;
    }

    private void verifierEquipeDuClub(Equipe equipe) {
        if (equipe == null) {
            throw new DonneeInvalideException("L'équipe est obligatoire.");
        }
        if (trouverEquipe(equipe.getNom()).isEmpty()) {
            throw new ElementIntrouvableException("Équipe introuvable dans le club : " + equipe.getNom());
        }
    }

    @Override
    public String toString() {
        return "Club{nom='" + nom + "', joueurs=" + joueurs.size()
                + ", entraineurs=" + entraineurs.size()
                + ", equipes=" + equipes.size()
                + ", seances=" + seances.size()
                + ", matchs=" + matchs.size()
                + ", paiements=" + paiements.size()
                + ", cotisations=" + cotisations.size() + "}";
    }
}
