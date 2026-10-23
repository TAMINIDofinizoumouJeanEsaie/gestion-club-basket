package club.personnes;

public enum Categorie {
    U13(12),
    U15(14),
    U18(17),
    SENIOR(99);

    private final int ageMax;

    Categorie(int ageMax) {
        this.ageMax = ageMax;
    }

    public int getAgeMax() {
        return ageMax;
    }
}
