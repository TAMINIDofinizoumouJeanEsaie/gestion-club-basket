package club.paiements;

import club.exceptions.DonneeInvalideException;
import club.personnes.Joueur;

public class Cotisation {
    private static final double EPSILON = 0.005;

    private final String id;
    private final Joueur joueur;
    private final int saison;
    private final double montantDu;
    private double montantPaye;

    public Cotisation(String id, Joueur joueur, int saison, double montantDu) {
        if (id == null || id.isBlank()) {
            throw new DonneeInvalideException("L'identifiant de la cotisation est obligatoire.");
        }
        if (joueur == null) {
            throw new DonneeInvalideException("Le joueur de la cotisation est obligatoire.");
        }
        if (saison <= 0) {
            throw new DonneeInvalideException("La saison doit être une année valide.");
        }
        if (montantDu <= 0) {
            throw new DonneeInvalideException("Le montant dû doit être supérieur à 0.");
        }
        this.id = id;
        this.joueur = joueur;
        this.saison = saison;
        this.montantDu = montantDu;
        this.montantPaye = 0.0;
    }

    public void ajouterVersement(double montant) {
        if (montant <= 0) {
            throw new DonneeInvalideException("Le versement doit être supérieur à 0.");
        }
        if (montant > getReste() + EPSILON) {
            throw new DonneeInvalideException("Le versement (" + montant
                    + " FCFA) dépasse le reste à payer (" + getReste() + " FCFA).");
        }
        montantPaye += montant;
    }

    public double getReste() {
        return Math.max(0.0, montantDu - montantPaye);
    }

    public double getMontantDu() {
        return montantDu;
    }

    public double getMontantPaye() {
        return montantPaye;
    }

    public boolean estSoldee() {
        return getReste() < EPSILON;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public int getSaison() {
        return saison;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (autre == null || getClass() != autre.getClass()) {
            return false;
        }
        Cotisation c = (Cotisation) autre;
        return id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Cotisation " + id + " [" + joueur.getNomComplet() + ", saison " + saison + "] : payé "
                + String.format("%.0f", montantPaye) + " / " + String.format("%.0f", montantDu) + " FCFA";
    }
}
