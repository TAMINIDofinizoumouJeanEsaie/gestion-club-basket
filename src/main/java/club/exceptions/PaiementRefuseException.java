package club.exceptions;

/**
 * Levée lorsque le mode de paiement refuse l'opération
 * (solde insuffisant, plafond dépassé, espèces insuffisantes).
 * Dans ce cas, aucun Paiement n'est créé et rien n'est modifié (RG11).
 */
public class PaiementRefuseException extends ClubException {

    /**
     * Crée une exception pour un paiement refusé.
     *
     * @param message le message expliquant le refus
     */
    public PaiementRefuseException(String message) {
        super(message);
    }
}
