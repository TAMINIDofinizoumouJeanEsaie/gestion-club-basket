package club.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests des 4 exceptions du package club.exceptions.
 * Les règles RG09 (montant invalide) et RG11 (paiement refusé) utilisent ces types :
 * leur logique est testée dans les modules paiements et gestion,
 * ici on vérifie que les types, la hiérarchie et les messages sont corrects.
 */
class ClubExceptionsTest {

    // ----- Cas normaux : le message est conservé -----

    @Test
    void clubExceptionConserveSonMessage() {
        ClubException e = new ClubException("erreur du club");
        assertEquals("erreur du club", e.getMessage());
    }

    @Test
    void donneeInvalideConserveSonMessage() {
        DonneeInvalideException e = new DonneeInvalideException("Téléphone invalide");
        assertEquals("Téléphone invalide", e.getMessage());
    }

    @Test
    void elementIntrouvableConserveSonMessage() {
        ElementIntrouvableException e = new ElementIntrouvableException("Joueur introuvable : J-999");
        assertEquals("Joueur introuvable : J-999", e.getMessage());
    }

    @Test
    void paiementRefuseConserveSonMessage() {
        PaiementRefuseException e = new PaiementRefuseException("Solde insuffisant");
        assertEquals("Solde insuffisant", e.getMessage());
    }

    // ----- Héritage -----

    @Test
    void clubExceptionEstUneRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(ClubException.class));
    }

    @Test
    void lesTroisExceptionsMetierHeritentDeClubException() {
        assertTrue(ClubException.class.isAssignableFrom(DonneeInvalideException.class));
        assertTrue(ClubException.class.isAssignableFrom(ElementIntrouvableException.class));
        assertTrue(ClubException.class.isAssignableFrom(PaiementRefuseException.class));
    }

    @Test
    void lesTroisExceptionsMetierSontIndependantesEntreElles() {
        assertFalse(DonneeInvalideException.class.isAssignableFrom(PaiementRefuseException.class));
        assertFalse(PaiementRefuseException.class.isAssignableFrom(DonneeInvalideException.class));
        assertFalse(ElementIntrouvableException.class.isAssignableFrom(DonneeInvalideException.class));
        assertFalse(ElementIntrouvableException.class.isAssignableFrom(PaiementRefuseException.class));
    }

    // ----- Polymorphisme : un catch de ClubException attrape les trois -----

    @Test
    void clubExceptionAttrapeDonneeInvalide() {
        assertThrows(ClubException.class, () -> {
            throw new DonneeInvalideException("x");
        });
    }

    @Test
    void clubExceptionAttrapeElementIntrouvable() {
        assertThrows(ClubException.class, () -> {
            throw new ElementIntrouvableException("x");
        });
    }

    @Test
    void clubExceptionAttrapePaiementRefuse() {
        assertThrows(ClubException.class, () -> {
            throw new PaiementRefuseException("x");
        });
    }

    @Test
    void uneExceptionPrecisePeutEtreAttrapeeParSonType() {
        PaiementRefuseException e = assertThrows(PaiementRefuseException.class, () -> {
            throw new PaiementRefuseException("Plafond dépassé");
        });
        assertEquals("Plafond dépassé", e.getMessage());
    }

    @Test
    void donneeInvalideNEstPasAttrapeeCommePaiementRefuse() {
        // Le type levé par RG09 (DonneeInvalideException) ne doit pas être confondu avec RG11.
        try {
            throw new DonneeInvalideException("Montant supérieur au reste");
        } catch (ClubException e) {
            assertTrue(e instanceof DonneeInvalideException);
            assertFalse(e instanceof PaiementRefuseException);
        }
    }

    // ----- Cas limites -----

    @Test
    void messageNullEstAccepte() {
        ClubException e = new ClubException(null);
        assertNull(e.getMessage());
    }

    @Test
    void messageVideEstConserve() {
        DonneeInvalideException e = new DonneeInvalideException("");
        assertEquals("", e.getMessage());
    }

    @Test
    void exceptionNeNecessitePasDeThrows() {
        // Ce code compile sans "throws" ni try/catch : les exceptions sont non vérifiées.
        Runnable action = () -> {
            throw new ElementIntrouvableException("Equipe introuvable : Aigles");
        };
        assertThrows(ElementIntrouvableException.class, action::run);
    }
}
