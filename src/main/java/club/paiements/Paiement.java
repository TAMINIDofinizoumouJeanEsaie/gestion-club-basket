package club.paiements;

import club.exceptions.DonneeInvalideException;
import club.personnes.Joueur;

import java.time.LocalDateTime;

public class Paiement {
    private final String id;
    private final Joueur joueur;
    private final double montant;
    private final ModePaiement mode;
    private final LocalDateTime date;

    public Paiement(String id, Joueur joueur, double montant, ModePaiement mode, LocalDateTime date) {
        if (id == null || id.isBlank()) {
            throw new DonneeInvalideException("L'identifiant du paiement est obligatoire.");
        }
        if (joueur == null) {
            throw new DonneeInvalideException("Le joueur du paiement est obligatoire.");
        }
        if (montant <= 0) {
            throw new DonneeInvalideException("Le montant du paiement doit être supérieur à 0.");
        }
        if (mode == null) {
            throw new DonneeInvalideException("Le mode de paiement est obligatoire.");
        }
        if (date == null) {
            throw new DonneeInvalideException("La date du paiement est obligatoire.");
        }
        this.id = id;
        this.joueur = joueur;
        this.montant = montant;
        this.mode = mode;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public double getMontant() {
        return montant;
    }

    public ModePaiement getMode() {
        return mode;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getFrais() {
        return mode.frais(montant);
    }

    public double getTotalDebite() {
        return montant + getFrais();
    }

    @Override
    public boolean equals(Object autre) {
        if (this == autre) {
            return true;
        }
        if (autre == null || getClass() != autre.getClass()) {
            return false;
        }
        Paiement p = (Paiement) autre;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Paiement " + id + " : " + String.format("%.0f", montant) + " FCFA (frais "
                + String.format("%.0f", getFrais()) + ") par " + joueur.getNomComplet()
                + " via " + mode.nom() + " le " + date;
    }
}
