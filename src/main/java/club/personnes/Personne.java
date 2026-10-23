package club.personnes;

import club.exceptions.DonneeInvalideException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Personne {
    private final String id;
    private String nom;
    private String prenom;
    private String telephone;
    private LocalDate dateNaissance;

    protected Personne(String id, String nom, String prenom, String telephone, LocalDate dateNaissance) {
        if (id == null || id.isBlank()) {
            throw new DonneeInvalideException("L'identifiant ne peut pas être vide.");
        }
        setNom(nom);
        setPrenom(prenom);
        setTelephone(telephone);
        setDateNaissance(dateNaissance);
        this.id = id;
    }

    public abstract String getRole();

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) return 0;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public LocalDate getDateNaissance() { return dateNaissance; }

    public void setNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom ne peut pas être vide.");
        }
        this.nom = nom.trim();
    }

    public void setPrenom(String prenom) {
        if (prenom == null || prenom.isBlank()) {
            throw new DonneeInvalideException("Le prénom ne peut pas être vide.");
        }
        this.prenom = prenom.trim();
    }

    public void setTelephone(String telephone) {
        if (telephone == null) {
            throw new DonneeInvalideException("Le téléphone ne peut pas être null.");
        }
        String cleanTel = telephone.replaceAll("\\s+", "");
        if (!cleanTel.matches("^\\d{8}$")) {
            throw new DonneeInvalideException("Le téléphone doit comporter exactement 8 chiffres.");
        }
        this.telephone = cleanTel;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        if (dateNaissance == null || dateNaissance.isAfter(LocalDate.now())) {
            throw new DonneeInvalideException("La date de naissance doit être dans le passé.");
        }
        if (Period.between(dateNaissance, LocalDate.now()).getYears() < 6) {
            throw new DonneeInvalideException("La personne doit avoir au moins 6 ans.");
        }
        this.dateNaissance = dateNaissance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Personne personne = (Personne) o;
        return id.equals(personne.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getRole() + "{" + "id='" + id + '\'' + ", nomComplet='" + getNomComplet() + '\'' + '}';
    }
}
