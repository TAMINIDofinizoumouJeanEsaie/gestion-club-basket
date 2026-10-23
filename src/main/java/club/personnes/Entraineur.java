package club.personnes;

import club.exceptions.DonneeInvalideException;
import java.time.LocalDate;

public class Entraineur extends Personne {
    private String specialite;
    private int anneesExperience;

    public Entraineur(String id, String nom, String prenom, String telephone, LocalDate dateNaissance, String specialite, int anneesExperience) {
        super(id, nom, prenom, telephone, dateNaissance);
        setSpecialite(specialite);
        setAnneesExperience(anneesExperience);
    }

    @Override
    public String getRole() {
        return "ENTRAINEUR";
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        if (specialite == null || specialite.isBlank()) {
            throw new DonneeInvalideException("La spécialité ne peut pas être vide.");
        }
        this.specialite = specialite.trim();
    }

    public int getAnneesExperience() {
        return anneesExperience;
    }

    public void setAnneesExperience(int anneesExperience) {
        if (anneesExperience < 0) {
            throw new DonneeInvalideException("Les années d'expérience ne peuvent pas être négatives.");
        }
        this.anneesExperience = anneesExperience;
    }

    @Override
    public String toString() {
        return "Entraineur{" + "id='" + getId() + '\'' + ", nomComplet='" + getNomComplet() + '\'' + ", specialite='" + specialite + '\'' + '}';
    }
}
