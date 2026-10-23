package club.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import club.gestion.Club;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests du menu console (test d'intégration léger).
 * On remplace le clavier (System.in) par un texte préparé et on lit ce que le menu affiche.
 * Ces tests ne s'exécutent qu'après la fusion avec le module Club (Personne 5).
 */
class MenuTest {

    /**
     * Lance un menu avec la saisie donnée et renvoie tout ce qu'il a affiché.
     * Le Menu doit être créé APRÈS le changement de System.in (son Scanner lit System.in).
     */
    private String lancer(String saisie) {
        InputStream ancienneEntree = System.in;
        PrintStream ancienneSortie = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(saisie.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capture, true, StandardCharsets.UTF_8));
            Menu menu = new Menu(new Club("Basket Club Bobo"));
            menu.demarrer();
        } finally {
            System.setIn(ancienneEntree);
            System.setOut(ancienneSortie);
        }
        return capture.toString(StandardCharsets.UTF_8);
    }

    @Test
    void quitterDirectementAfficheAuRevoir() {
        String sortie = lancer("0\n");
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void menuAfficheLeNomDuClub() {
        String sortie = lancer("0\n");
        assertTrue(sortie.contains("Basket Club Bobo"));
    }

    @Test
    void menuPrincipalProposeLesTroisSections() {
        String sortie = lancer("0\n");
        assertTrue(sortie.contains("Administrateur"));
        assertTrue(sortie.contains("Entraîneur"));
        assertTrue(sortie.contains("Trésorier"));
    }

    @Test
    void choixInconnuAfficheUnMessageEtContinue() {
        String sortie = lancer("9\n0\n");
        assertTrue(sortie.contains("Choix inconnu."));
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void saisieNonNumeriqueAfficheUneErreurSansPlanter() {
        String sortie = lancer("abc\n0\n");
        assertTrue(sortie.contains("Erreur : Nombre entier attendu : abc"));
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void sectionAdministrateurPuisRetour() {
        String sortie = lancer("1\n0\n0\n");
        assertTrue(sortie.contains("--- Administrateur ---"));
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void sectionEntraineurPuisRetour() {
        String sortie = lancer("2\n0\n0\n");
        assertTrue(sortie.contains("--- Entraîneur ---"));
    }

    @Test
    void sectionTresorierPuisRetour() {
        String sortie = lancer("3\n0\n0\n");
        assertTrue(sortie.contains("--- Trésorier ---"));
    }

    @Test
    void creerEquipeAvecEntraineurInconnuAfficheElementIntrouvable() {
        // Administrateur > Créer une équipe : nom, catégorie 1 (U13), entraîneur E-999 inexistant.
        String sortie = lancer("1\n3\nEtalons\n1\nE-999\n0\n0\n");
        assertTrue(sortie.contains("Erreur : Entraîneur introuvable : E-999"));
    }

    @Test
    void modePaiementInconnuEstRefuse() {
        // Trésorier > Enregistrer un paiement : mode 9 n'existe pas.
        String sortie = lancer("3\n1\nJ-001\n5000\n9\n0\n0\n");
        assertTrue(sortie.contains("Erreur : Mode de paiement inconnu : 9"));
    }

    @Test
    void paiementPourJoueurInexistantAfficheUneErreur() {
        // Trésorier > paiement de 5000 en espèces pour un joueur qui n'existe pas.
        String sortie = lancer("3\n1\nJ-999\n5000\n1\n5000\n0\n0\n");
        assertTrue(sortie.contains("Erreur :"));
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void montantNonNumeriqueEstRefuse() {
        String sortie = lancer("3\n1\nJ-001\nabc\n0\n0\n");
        assertTrue(sortie.contains("Erreur : Nombre attendu : abc"));
    }

    @Test
    void dateDeNaissanceInvalideEstRefusee() {
        // Inscrire un joueur : nom, prénom, téléphone, puis une date au mauvais format.
        String sortie = lancer("1\n1\nOuedraogo\nAwa\n70000000\n2000-01-01\n0\n0\n");
        assertTrue(sortie.contains("Erreur : Date invalide (format jj/mm/aaaa) : 2000-01-01"));
    }

    @Test
    void rapportsSurClubVideNePlantentPas() {
        String sortie = lancer("4\n0\n");
        assertTrue(sortie.contains("Au revoir."));
    }

    @Test
    void statistiquesAvecEquipeInconnueAfficheUneErreur() {
        String sortie = lancer("5\nAigles\n0\n");
        assertTrue(sortie.contains("Erreur : Equipe introuvable : Aigles"));
    }
}
