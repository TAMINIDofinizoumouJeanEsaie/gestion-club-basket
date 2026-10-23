# Plan de fusion Git

## 1. Branches (une par personne)

| Branche | Propriétaire | Dossiers autorisés |
| --- | --- | --- |
| `main` | Personne 1 (protégée) | Code fusionné et validé uniquement |
| `p1-exceptions-ui` | Personne 1 | `club/exceptions`, `club/ui`, `pom.xml`, `docs` |
| `p2-personnes` | Personne 2 | `club/personnes` |
| `p3-paiements` | Personne 3 | `club/paiements` |
| `p4-matchs` | Personne 4 | `club/matchs` |
| `p5-gestion` | Personne 5 | `club/gestion`, `club/rapports`, `club/stockage` |

Règle d'or : chacun ne modifie que ses packages (code dans `src/main/java/club/...` et tests dans `src/test/java/club/...`). Seule la Personne 1 touche `pom.xml`. Ainsi, aucun conflit de fichier n'est possible.

## 2. Mise en place (Personne 1, une fois)

```bash
git init gestion-club-basket
cd gestion-club-basket
# copier pom.xml, .gitignore, src/ (exceptions) puis :
git add .
git commit -m "Initialisation : pom, exceptions, contrat v1.0"
git branch -M main
git remote add origin <URL_DU_DEPOT>
git push -u origin main
git tag contrat-v1.0
git push --tags
```

Chaque personne ensuite :

```bash
git clone <URL_DU_DEPOT>
cd gestion-club-basket
git checkout -b p2-personnes      # son nom de branche
```

## 3. Travail quotidien (tout le monde)

```bash
git add src/
git commit -m "Joueur : validation du maillot (RG04)"
git pull --rebase origin main     # récupérer main avant de pousser
git push origin p2-personnes
```

- Un commit par petite tâche, message clair en français.
- Ne jamais pousser directement sur `main`.
- Les bouchons « BOUCHON À SUPPRIMER » restent dans la branche de leur auteur et ne sont jamais fusionnés.

## 4. Ordre de fusion dans `main` (Personne 1)

L'ordre suit les dépendances : on fusionne ce dont les autres ont besoin d'abord.

| Étape | Branche | Condition | Vérification |
| --- | --- | --- | --- |
| 0 | `p1-exceptions-ui` (exceptions seulement) | Déjà dans `main` | `mvn -q compile` |
| 1 | `p2-personnes` | Aucun bouchon restant | `mvn -q test` |
| 2 | `p3-paiements` | Compile avec `Personne` et `Joueur` réels | `mvn -q test` |
| 3 | `p4-matchs` | Compile avec `Equipe` et `Joueur` réels | `mvn -q test` |
| 4 | `p5-gestion` | Compile avec les 3 modules réels | `mvn -q test` |
| 5 | `p1-exceptions-ui` (Menu) | Compile avec tout | `mvn -q test` |
| 6 | Test d'intégration | `ScenarioSection13Test` et `MenuTest` verts | `mvn test` |

Commandes pour une fusion :

```bash
git checkout main
git pull origin main
git merge --no-ff origin/p2-personnes -m "Fusion module A : personnes"
mvn -q test          # si rouge : ne pas pousser, voir section 5
git push origin main
git tag etape-1-personnes
```

Après chaque fusion, tous les autres font `git pull --rebase origin main` sur leur branche.

## 5. En cas d'échec

| Problème | Action |
| --- | --- |
| Erreur de compilation à cause d'une signature | Comparer au contrat (section 9). Celui qui s'écarte du contrat corrige. |
| Test rouge | Ouvrir un ticket avec le nom du test et la sortie. Le propriétaire du package corrige dans sa branche. |
| Conflit de fusion | Ne doit pas arriver (packages séparés). Sinon, vérifier qui a modifié un fichier qui n'est pas à lui. |
| Fusion à annuler | `git reset --hard HEAD~1` avant le `push` uniquement. |

## 6. Points à valider à la première fusion

1. Préfixes de `Club.prochainId` : `"J"`, `"E"`, `"S"`, `"M"` (utilisés par `Menu`).
2. `Personne.getId()` existe.
3. Les 3 `Rapport` ont un constructeur sans paramètre.
4. `StockageCsv` convertit les `IOException` en `ClubException`.
5. Noms de fichiers CSV : `joueurs.csv`, `entraineurs.csv`, `equipes.csv`, `seances.csv`, `matchs.csv`, `paiements.csv`, `cotisations.csv`.

## 7. Commandes utiles

```bash
mvn clean test                      # compiler et lancer tous les tests
mvn -q -Dtest=ClubExceptionsTest test   # un seul test
mvn package -DskipTests             # fabriquer le jar
java -cp target/classes club.ui.Main    # lancer l'application
```
