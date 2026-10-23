package club.exceptions;

/**
 * Levée lorsqu'une donnée ne respecte pas une règle métier
 * (nom vide, téléphone incorrect, montant supérieur au reste à payer, etc.).
 */
public class DonneeInvalideException extends ClubException {

    /**
     * Crée une exception pour une donnée invalide.
     *
     * @param message le message expliquant quelle donnée est invalide
     */
    public DonneeInvalideException(String message) {
        super(message);
    }
}
