package club.paiements;

import club.exceptions.DonneeInvalideException;

public class Especes implements ModePaiement {
    private static final double TAUX_FRAIS = 0.0;

    private double montantRemis;

    public Especes(double montantRemis) {
        if (montantRemis < 0) {
            throw new DonneeInvalideException("Le montant remis en espèces ne peut pas être négatif.");
        }
        this.montantRemis = montantRemis;
    }

    public double getMontantRemis() {
        return montantRemis;
    }

    @Override
    public boolean payer(double montant) {
        if (montant <= 0) {
            throw new DonneeInvalideException("Le montant à payer doit être supérieur à 0.");
        }
        double total = montant + frais(montant);
        if (total > montantRemis) {
            return false;
        }
        montantRemis -= total;
        return true;
    }

    @Override
    public String nom() {
        return "Espèces";
    }

    @Override
    public double frais(double montant) {
        return montant * TAUX_FRAIS;
    }

    @Override
    public String toString() {
        return "Especes{montantRemis=" + montantRemis + " FCFA}";
    }
}
