# Section Personne 1 : exceptions et interface console

Packages : `club.exceptions` (4 classes) et `club.ui` (`Main`, `Menu`).

## 1. Objectif

Ce module a deux rôles.

- **Les exceptions** donnent au projet un vocabulaire commun pour signaler une erreur : donnée invalide, élément introuvable, paiement refusé. Tous les modules les utilisent, ce qui rend les erreurs homogènes.
- **L'interface console** (`Main` et `Menu`) permet à l'utilisateur d'utiliser toutes les fonctionnalités du club sans jamais voir un plantage : une erreur est affichée, puis le menu continue.

Le menu ne connaît que la façade `Club`. Il ne touche jamais aux listes internes.

## 2. Fonctionnalités

| Id | Fonctionnalité | Où dans le menu |
| --- | --- | --- |
| F15 | Naviguer dans le menu console | Menu principal : 3 sections, rapports, statistiques |
| F01 à F04 | Inscrire un joueur, ajouter un entraîneur, créer une équipe, affecter un joueur | Administrateur, options 1 à 4 |
| F14 | Sauvegarder et charger les données | Administrateur, options 6 et 7 |
| F05 à F09 | Planifier une séance, marquer les présences, programmer un match, saisir score et statistiques, simuler | Entraîneur, options 1 à 6 |
| F11 et F12 | Enregistrer un paiement, consulter l'état des cotisations | Trésorier, options 1 et 2 |
| F13 | Générer les trois rapports | Menu principal, option 4 |
| F10 | Meilleurs marqueurs et bilan d'une équipe | Menu principal, option 5 |

Le menu fournit l'accès à ces fonctionnalités. Leur logique est dans les modules A, B, C et D.

## 3. Règles métier

Les classes de ce module n'implémentent pas de règle métier. Elles les rendent visibles :

| Règle | Rôle de ce module |
| --- | --- |
| RG09 | Le montant invalide lève une `DonneeInvalideException`, affichée par le menu. |
| RG11 | Le refus du mode de paiement lève une `PaiementRefuseException`, affichée par le menu. Rien n'est modifié. |
| RG01 | Le menu demande les identifiants à `Club.prochainId` : J, E, S, M. |
| RG02, RG03, RG04, RG07 | Une violation lève une `DonneeInvalideException` et le menu affiche son message. |

Règles propres à l'interface :

- Une entrée incorrecte (texte au lieu d'un nombre, mauvaise date) lève une `DonneeInvalideException`, jamais un plantage.
- Une erreur interrompt l'action en cours, mais pas le programme.

## 4. Classes

| Classe | Type | Rôle |
| --- | --- | --- |
| `ClubException` | classe, hérite de `RuntimeException` | Racine des erreurs du projet |
| `DonneeInvalideException` | hérite de `ClubException` | Donnée qui viole une règle métier |
| `ElementIntrouvableException` | hérite de `ClubException` | Joueur, équipe, séance, match ou cotisation absent |
| `PaiementRefuseException` | hérite de `ClubException` | Mode de paiement qui refuse |
| `Main` | classe | Crée le `Club` « Basket Club Bobo » et lance le `Menu` |
| `Menu` | classe | Menu console : une méthode privée par section (administrateur, entraîneur, trésorier) |

`Menu` a deux attributs privés : `club` (non final, car le chargement d'une sauvegarde le remplace) et `scanner`.

## 5. Concepts POO illustrés

### Héritage (exceptions)

`ClubException` est la classe mère des trois exceptions métier.

```java
public class ClubException extends RuntimeException {
    public ClubException(String message) {
        super(message);
    }
}

public class PaiementRefuseException extends ClubException {
    public PaiementRefuseException(String message) {
        super(message);
    }
}
```

Un seul `catch (ClubException e)` attrape les trois types. Le choix de `RuntimeException` évite d'écrire `throws` dans toutes les méthodes du projet.

### Encapsulation

Les attributs de `Menu` sont `private`. Le menu lit les listes du club par des getters non modifiables et ne peut pas les altérer :

```java
for (Cotisation c : club.getCotisations()) {
    String statut = c.estSoldee() ? "SOLDEE" : "EN COURS";
    System.out.println(c.getJoueur().getNomComplet() + " | reste : " + c.getReste() + " | " + statut);
}
```

### Programmer vers une interface

`Menu` utilise le type `Stockage` (interface), pas `StockageCsv`, et le type `ModePaiement` (interface) pour le paiement :

```java
Stockage stockage = new StockageCsv();
stockage.sauvegarder(club, dossier);
```

Pour changer de format de sauvegarde, une seule ligne du menu change.

### Polymorphisme et classe abstraite

Le menu génère les trois rapports sans connaître le type exact de chacun :

```java
Rapport[] rapports = {new RapportCotisations(), new RapportPresences(), new RapportMatchs()};
for (Rapport rapport : rapports) {
    System.out.println(rapport.generer(club));
}
```

Pour le paiement, `mode.recu(montant)` appelle la méthode `default` de `ModePaiement`, quel que soit le mode choisi.

### Gestion des erreurs

Toutes les actions du menu sont protégées par le même schéma :

```java
try {
    int choix = lireEntier("Votre choix");
    // ... traitement du choix ...
} catch (ClubException e) {
    System.out.println("Erreur : " + e.getMessage());
}
```

Les erreurs de saisie sont converties en `DonneeInvalideException`, ce qui évite de traiter deux types d'erreurs différents :

```java
private int lireEntier(String invite) {
    String texte = lireTexte(invite);
    try {
        return Integer.parseInt(texte);
    } catch (NumberFormatException e) {
        throw new DonneeInvalideException("Nombre entier attendu : " + texte);
    }
}
```

### Méthodes privées et composition d'appels

Chaque section du menu est une méthode privée (`menuAdministrateur`, `menuEntraineur`, `menuTresorier`). Des méthodes de recherche privées (`chercherJoueur`, `chercherEquipe`...) transforment un `Optional` vide en `ElementIntrouvableException` :

```java
private Joueur chercherJoueur(String id) {
    Optional<Joueur> resultat = club.trouverJoueur(id);
    if (resultat.isEmpty()) {
        throw new ElementIntrouvableException("Joueur introuvable : " + id);
    }
    return resultat.get();
}
```

## 6. Difficultés

1. **Dépendre de classes pas encore écrites.** `Menu` utilise presque tous les modules. Le contrat v1.0 a permis de l'écrire sans attendre les autres : chaque appel respecte une signature de la section 9. Il reste des points à vérifier à la fusion : préfixes de `prochainId`, getter `getId()` de `Personne`, constructeurs des rapports.
2. **Un trou possible dans les identifiants.** Le menu demande l'identifiant avant de créer l'objet. Si le constructeur refuse une donnée, l'identifiant est déjà consommé et le suivant saute un numéro. Nous l'acceptons : l'identifiant doit être unique, pas continu.
3. **`Equipe` n'a pas d'identifiant** dans le contrat, alors que RG01 prévoit EQ-001. Le menu retrouve donc les équipes par leur nom.
4. **Pas de `trouverEntraineur` dans `Club`.** Le menu parcourt la liste non modifiable des entraîneurs. Une méthode dédiée pourrait être ajoutée dans une version 1.1 du contrat.
5. **Erreurs de fichier.** `Stockage` ne déclare pas de `throws`. Les erreurs d'entrée-sortie doivent donc être encapsulées dans une `ClubException` par `StockageCsv`, sinon le menu ne peut pas les afficher proprement.
6. **Tester un menu.** Un menu lit le clavier. Pour le tester, on remplace `System.in` par un texte préparé et on capture `System.out`. Le `Menu` doit être créé après ce remplacement, car son `Scanner` est créé dans son constructeur.
