package club.stockage;

import club.gestion.Club;

import java.nio.file.Path;

/**
 * Contrat de sauvegarde et de chargement des données du club.
 *
 * Concepts : interface et programmation vers une interface. Le menu ne connaît
 * que Stockage : demain on pourrait écrire StockageJson sans changer le menu.
 */
public interface Stockage {

    /**
     * Sauvegarde toutes les données du club dans un dossier (créé s'il n'existe pas).
     *
     * @param club    le club à sauvegarder
     * @param dossier le dossier de destination
     */
    void sauvegarder(Club club, Path dossier);

    /**
     * Recharge un club complet depuis un dossier de sauvegarde.
     *
     * @param dossier le dossier qui contient les fichiers
     * @return le club reconstruit
     */
    Club charger(Path dossier);
}
