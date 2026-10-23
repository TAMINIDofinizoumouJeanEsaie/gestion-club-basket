# Présentation de groupe et checklist de répétition

Projet : gestion d'un club de basket (Java 17, Maven, JUnit 5). Équipe de 5 personnes.

## 1. Déroulé de la présentation (environ 13 minutes + questions)

| Heure | Qui | Durée | Contenu | Concepts à nommer | Démo (étape de la section 13) |
| --- | --- | --- | --- | --- | --- |
| 0:00 | Personne 1 | 1 min | Objectif, acteurs, architecture en packages, schéma des dépendances | Architecture en couches, aucune dépendance cyclique | Aucune (slide du diagramme UML) |
| 1:00 | Personne 2 | 2 min | Joueurs, entraîneurs, équipes, règles RG02 à RG07 | Encapsulation, héritage, classe abstraite, polymorphisme (`getRole`), association 1-N | Inscrire Issa, créer l'équipe Etalons, inscrire et affecter Awa, Moussa et Fanta |
| 3:00 | Personne 3 | 2 min | Cotisation de 25 000 FCFA, paiements partiels, frais, refus | Interface `ModePaiement`, polymorphisme, méthode `default`, programmer vers une interface | Awa paie 15 000 (Mobile Money) puis 10 000 (Carte) ; Moussa refusé |
| 5:00 | Personne 4 | 2 min | Séances, matchs, statistiques, simulateur | `Comparable`, composition (`Match` contient ses `StatJoueur`), méthodes statiques | Séance (taux 2/3), match simulé, marqueurs et bilan |
| 7:00 | Personne 5 | 2 min | Façade `Club`, rapports, sauvegarde CSV | Méthode modèle (`Rapport.generer`), interface `Stockage`, composition | Les 3 rapports, sauvegarde, chargement, rapports identiques |
| 9:00 | Personne 1 | 2 min | Exceptions et menu : robustesse | Héritage (`ClubException`), gestion des erreurs | Saisie invalide `abc` : l'erreur s'affiche et le menu continue |
| 11:00 | Personne 1 | 1 min | Conclusion : tests, ce qui marche, difficultés, limites | Tous les concepts du tableau de la section 11 | Aucune |
| 12:00 | Tous | 5 min | Questions | | |

Total : 12 minutes de présentation et 5 minutes de questions.

### Fil rouge de la démo

Un seul scénario traverse toutes les interventions : celui de la section 13. Chaque personne présente l'étape qui concerne son module et laisse la suite à la personne suivante. Ainsi, la démo est continue et personne ne rejoue le début.

### Phrases de transition (à dire à la fin de chaque intervention)

- Personne 1 vers Personne 2 : « Voyons d'abord comment les personnes sont modélisées. »
- Personne 2 vers Personne 3 : « Maintenant que les joueurs sont inscrits, passons à leurs cotisations. »
- Personne 3 vers Personne 4 : « Awa a payé. Passons au terrain : séances et matchs. »
- Personne 4 vers Personne 5 : « Les données existent : voyons comment le club les regroupe, les affiche et les sauvegarde. »
- Personne 5 vers Personne 1 : « Tout fonctionne : voyons ce qui se passe quand l'utilisateur se trompe. »

### Slides conseillées (7 maximum)

1. Titre, noms et rôles.
2. Objectif et acteurs (administrateur, entraîneur, trésorier).
3. Diagramme de classes par package (version simplifiée).
4. Tableau des concepts du cours avec la classe qui les illustre (section 11 du document de référence).
5. Extrait de code de la Personne 2 ou 3, au choix du groupe : `ModePaiement` et ses trois implémentations.
6. Résultats des tests (capture de `mvn test` en vert).
7. Conclusion et difficultés.

Moins il y a de texte sur les slides, mieux c'est : le code se montre dans l'éditeur ou le terminal.

## 2. Organisation de la démo

| Point | Décision |
| --- | --- |
| Ordinateur | Un seul ordinateur branché au projecteur, celui de la Personne 1. |
| Qui tape | Personne 1 au clavier. Chaque intervenant dicte les valeurs : « J-001, 15000, option 2... » |
| Éditeur | Ouvert à côté du terminal sur le fichier de la partie en cours (par exemple `ModePaiement.java`). |
| Terminal | Police agrandie, fond sombre, `java -cp target/classes club.ui.Main` déjà lancé sur le menu principal. |
| Plan B | Le fichier `docs/demo_saisie.txt` rejoue tout le scénario. Commande : `java -cp target/classes club.ui.Main < docs/demo_saisie.txt`. À utiliser si la saisie en direct tourne mal. |
| Plan C | Captures d'écran du déroulement correct, dans les slides cachées à la fin. |

Attention : si vous rejouez le fichier `demo_saisie.txt`, le dossier `donnees/` est créé dans le dossier courant. Supprimez-le avant chaque répétition (`rm -r donnees`).

### Le simulateur ne donne pas 42

Le menu utilise `new Random()` pour simuler, donc le score change à chaque exécution. La reproductibilité avec `new Random(42)` (RG16) se montre par le test JUnit `simulationReproductibleAvecLeMemeRandom`. Annoncez-le à l'oral : « Le score est aléatoire dans le menu, mais avec une graine fixe, il est reproductible. Voici le test. »

### Ajustement du script oral de la Personne 1 (L4)

La démo du paiement refusé de Moussa est présentée par la Personne 3. Dans la partie finale, la Personne 1 ne montre plus que la saisie invalide : **Trésorier, option 1, joueur J-001, montant `abc`** vers « Erreur : Nombre attendu : abc », puis retour au menu.

## 3. Questions probables et qui répond

| Question | Répond |
| --- | --- |
| Pourquoi `Personne` est abstraite ? | Personne 2 |
| Pourquoi un paiement refusé ne modifie rien (RG11) ? | Personne 3 |
| Pourquoi vérifier le montant avant de débiter (RG09) ? | Personne 3 |
| Qu'apporte `Comparable` sur `StatJoueur` ? | Personne 4 |
| Comment fonctionne la méthode modèle ? | Personne 5 |
| Comment ajouter un format de sauvegarde (JSON) ? | Personne 5 |
| Pourquoi `RuntimeException` ? | Personne 1 |
| Comment avez-vous évité les conflits Git ? | Personne 1 |
| Comment avez-vous testé ? | Chacun pour son module, Personne 1 pour l'intégration |

Règle : celui à qui on pose la question répond en premier. Les autres complètent seulement s'il manque quelque chose.

## 4. Checklist de répétition

### A. Une semaine avant (ou dès la fusion)

- [ ] Toutes les branches fusionnées dans `main`.
- [ ] `mvn clean test` : tous les tests passent, y compris `ScenarioSection13Test` et `MenuTest`.
- [ ] Les points à valider du plan de fusion (section 6) sont réglés.
- [ ] Le diagramme PlantUML correspond au code final.
- [ ] Le rapport final est assemblé et relu.
- [ ] Chaque personne a son script oral (L4) à jour.

### B. Chaque personne (module)

- [ ] Je connais mon texte sans lire.
- [ ] Je tiens dans mon temps (chronomètre) : 2 minutes au maximum.
- [ ] Je sais citer la classe et la ligne de code pour chaque concept que je nomme.
- [ ] Je sais expliquer chaque règle RG de mon module avec un exemple.
- [ ] Je connais les valeurs à dicter pour ma partie de la démo.
- [ ] Je sais répondre aux questions du tableau de la section 3.

### C. Technique (Personne 1)

- [ ] `mvn clean package -DskipTests` donne `target/classes` à jour.
- [ ] `java -cp target/classes club.ui.Main` démarre sur ordinateur propre.
- [ ] Dossier `donnees/` supprimé avant la démo.
- [ ] Le terminal est lisible de loin (police grande).
- [ ] Les fichiers à montrer sont ouverts : `ClubException.java`, `ModePaiement.java`, `Rapport.java`, `StatJoueur.java`, `Personne.java`.
- [ ] `docs/demo_saisie.txt` testé : il rejoue le scénario sans erreur.
- [ ] Captures d'écran de secours enregistrées.
- [ ] Adaptateur HDMI, chargeur, projecteur testés.
- [ ] Copie du projet sur clé USB et sur le dépôt Git.

### D. Répétitions générales

| Répétition | Objectif | Fait |
| --- | --- | --- |
| 1 | Chacun dit son texte seul, chronométré | [ ] |
| 2 | Enchaînement complet sans démo (transitions, temps global) | [ ] |
| 3 | Enchaînement complet avec la démo en direct | [ ] |
| 4 | Enchaînement complet avec une panne simulée (plan B) | [ ] |
| 5 | Séance de questions : on se pose des questions piégeuses | [ ] |

Critères de réussite de la répétition 3 : moins de 12 minutes, aucune hésitation sur la saisie, tous les résultats de la section 13 apparaissent.

### E. Valeurs à retenir pour la démo

- [ ] Premier paiement d'Awa : frais **150**, total débité **15 150**, reste à payer **10 000**.
- [ ] Second paiement : frais **200**, total débité **10 200**, cotisation **soldée**.
- [ ] Paiement de 5 000 pour Awa : refusé par `DonneeInvalideException`.
- [ ] Moussa avec 5 000 en espèces pour 20 000 : refusé par `PaiementRefuseException`, rien modifié.
- [ ] Séance : taux de présence **2/3, soit 0,67**.
- [ ] Rapports identiques avant sauvegarde et après chargement.

### F. Le jour J

- [ ] Arriver 20 minutes avant.
- [ ] Brancher l'ordinateur et tester l'affichage.
- [ ] Lancer le programme une fois, puis le quitter et supprimer `donnees/`.
- [ ] Désactiver les notifications.
- [ ] Définir qui prend la parole si quelqu'un est absent (Personne 1 remplace par défaut).
- [ ] Se rappeler : parler lentement, regarder le jury et non l'écran.

### G. Plan en cas de problème

| Problème | Réaction |
| --- | --- |
| Erreur de saisie dans la démo | Dire « c'est justement le rôle de notre gestion d'erreurs », continuer. |
| Le programme plante | Lancer `demo_saisie.txt` (plan B). |
| Le plan B échoue | Montrer les captures et les tests JUnit (plan C). |
| Panne de projecteur | Présentation sur l'ordinateur d'une personne avec le jury autour. |
| Question sans réponse | Dire ce qu'on sait, puis « nous n'avons pas traité ce cas » et passer à la suite. |
