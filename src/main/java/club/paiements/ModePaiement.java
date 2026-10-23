package club.paiements;

public interface ModePaiement {
    boolean payer(double montant);

    String nom();

    double frais(double montant);

    default String recu(double montant) {
        return nom() + " a payé " + montant + " FCFA";
    }
}
