package club.personnes;

import club.exceptions.DonneeInvalideException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModuleAPersonnesTest {

    private Entraineur entraineur;
    private Joueur joueur;
    private Equipe equipe;

    @BeforeEach
    void setUp() {
        entraineur = new Entraineur("E-001", "Traore", "Issa", "70000000", LocalDate.of(1980, 5, 12), "Tactique", 10);
        joueur = new Joueur("J-001", "Ouedraogo", "Awa", "71111111", LocalDate.of(2010, 3, 15), 7, Poste.MENEUR);
        equipe = new Equipe("Etalons", Categorie.SENIOR, entraineur);
    }

    @Test
    void testCreationPersonneValide() {
        assertEquals("Issa Traore", entraineur.getNomComplet());
        assertEquals("ENTRAINEUR", entraineur.getRole());
        assertEquals("70000000", entraineur.getTelephone());
    }

    @Test
    void testValidationTelephoneEchoue() {
        assertThrows(DonneeInvalideException.class, () -> {
            new Joueur("J-002", "Kone", "Fanta", "12345", LocalDate.of(2010, 1, 1), 4, Poste.AILIER);
        });
    }

    @Test
    void testValidationMaillotEchoue() {
        assertThrows(DonneeInvalideException.class, () -> {
            new Joueur("J-003", "Sawadogo", "Moussa", "72222222", LocalDate.of(2010, 1, 1), 150, Poste.PIVOT);
        });
    }

    @Test
    void testAjoutEtGestionJoueurEquipe() {
        equipe.ajouterJoueur(joueur);
        assertEquals(1, equipe.getEffectif());
        assertTrue(equipe.trouverJoueur("J-001").isPresent());

        assertThrows(DonneeInvalideException.class, () -> equipe.ajouterJoueur(joueur));
    }

    @Test
    void testDoublonMaillotEquipe() {
        equipe.ajouterJoueur(joueur);
        Joueur autreJoueur = new Joueur("J-002", "Kone", "Fanta", "73333333", LocalDate.of(2010, 5, 5), 7, Poste.AILIER);
        
        assertThrows(DonneeInvalideException.class, () -> equipe.ajouterJoueur(autreJoueur));
    }
}
