package club.exceptions;

/**
 * Exception de base de l'application du club.
 * Elle hérite de RuntimeException : les erreurs du club sont "non vérifiées",
 * on n'est donc pas obligé d'écrire "throws" partout.
 * Les trois exceptions métier du projet héritent de cette classe.
 */
public class ClubException extends RuntimeException {

    /**
     * Crée une exception du club.
     *
     * @param message le message expliquant l'erreur
     */
    public ClubException(String message) {
        super(message);
    }
}
