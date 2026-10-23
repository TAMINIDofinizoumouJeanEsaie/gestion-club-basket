package club.exceptions;

/**
 * Levée lorsqu'un élément recherché n'existe pas
 * (joueur, équipe, séance, match ou cotisation introuvable).
 */
public class ElementIntrouvableException extends ClubException {

    /**
     * Crée une exception pour un élément absent.
     *
     * @param message le message indiquant quel élément est introuvable
     */
    public ElementIntrouvableException(String message) {
        super(message);
    }
}
