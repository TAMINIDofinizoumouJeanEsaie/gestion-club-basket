package club.paiements;

import club.exceptions.DonneeInvalideException;

public class MobileMoney implements ModePaiement {
    private static final double TAUX_FRAIS = 0.01;

    private final Operateur operateur;
    private final String telephone;
    private double solde;

    public MobileMoney(Operateur operateur, String telephone, double solde) {
        if (operateur == null) {
            throw new DonneeInvalideException("L'opérateur est obligatoire.");
        }
        if (telephone == null) {
            throw new DonneeInvalideException("Le téléphone est obligatoire.");
        }
        String propre = telephone.replace(" ", "");
        if (!propre.matches("\\d{8}")) {
            throw new DonneeInvalideException("Le téléphone doit contenir exactement 8 chiffres.");
        }
        if (solde < 0) {
            throw new DonneeInvalideException("Le solde ne peut pas être négatif.");
        }
        this.operateur = operateur;
        this.telephone = propre;
        this.solde = solde;
    }

    public Operateur getOperateur() {
        return operateur;
    }

    public String getTelephone() {
        return telephone;
    }

    public double getSolde() {
        return solde;
    }

    @Override
    public boolean payer(double montant) {
        if (montant <= 0) {
            throw new DonneeInvalideException("Le montant à payer doit être supérieur à 0.");
        }
        double total = montant + frais(montant);
        if (total > solde) {
            return false;
        }
        solde -= total;
        return true;
    }

    @Override
    public String nom() {
        if (operateur == Operateur.ORANGE_MONEY) {
            return "Orange Money";
        }
        return "Moov Money";
    }

    @Override
    public double frais(double montant) {
        return montant * TAUX_FRAIS;
    }

    @Override
    public String toString() {
        return "MobileMoney{" + nom() + ", telephone=" + telephone + ", solde=" + solde + " FCFA}";
    }
}
