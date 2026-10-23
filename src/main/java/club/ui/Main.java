package club.ui;

import club.gestion.Club;

/**
 * Point d'entrée de l'application console.
 * Crée le club puis lance le menu.
 */
public class Main {

    /**
     * Démarre l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Club club = new Club("BIT Basket Club");
        Menu menu = new Menu(club);
        menu.demarrer();
    }
}
