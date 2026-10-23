package club.paiements;

import club.exceptions.DonneeInvalideException;

public class Carte implements ModePaiement {
    private static final double TAUX_FRAIS = 0.02;

    private final String numero;
    private double plafond;

    public Carte(String numero, double plafond) {
        if (numero == null) {
            throw new DonneeInvalideException("Le numéro de carte est obligatoire.");
        }
        String propre = numero.replace(" ", "");
        if (!propre.matches("\\d{4,}")) {
            throw new DonneeInvalideException("Le numéro de carte doit contenir au moins 4 chiffres.");
        }
        if (plafond < 0) {
            throw new DonneeInvalideException("Le plafond ne peut pas être négatif.");
        }
        this.numero = propre;
        this.plafond = plafond;
    }

    public double getPlafond() {
        return plafond;
    }

    @Override
    public boolean payer(double montant) {
        if (montant <= 0) {
            throw new DonneeInvalideException("Le montant à payer doit être supérieur à 0.");
        }
        double total = montant + frais(montant);
        if (total > plafond) {
            return false;
        }
        plafond -= total;
        return true;
    }

    @Override
    public String nom() {
        return "Carte ****" + numero.substring(numero.length() - 4);
    }

    @Override
    public double frais(double montant) {
        return montant * TAUX_FRAIS;
    }

    @Override
    public String toString() {
        return "Carte{" + nom() + ", plafond=" + plafond + " FCFA}";
    }
}
