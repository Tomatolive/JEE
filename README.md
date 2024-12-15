# JEE

YourDiet est une application web de suivi de régime alimentaire. Elle permet à l'utilisateur de calculer son apport calorique journalier et de suivre son évolution au fil du temps, ainsi que de renseigner ses repas quotidiennement.  

## Installation

Pour installer l'application, il suffit de cloner le dépôt git (ou dézipper l'archive), se rendre dans le dossier de l'application et lancer : 
    
```bash
mvn install
```
Ensuite, pour lancer l'application, il suffit de lancer la commande :

```bash
mvn exec:java -Dexec.mainClass="yourdiet.YourDietApplication"
```

## Description

Avec YourDiet, vous pouvez vous inscrire pour indiquer votre poids cible. Ensuite, l'application vous attribuera un objectif calorique à suivre pour atteindre ce poids.  

Pour vérifier si vos repas respectent votre objectif, vous avez la possibilité d'ajouter des repas dans une liste en précisant leurs attributs nutritionnels et, si vous le souhaitez, en ajoutant des tags. Une fois un repas créé, vous pouvez le modifier ou le supprimer.  

Une page dédiée vous permet de visualiser tous les repas que vous avez enregistrés dans la base de données et de les chercher. Un agenda des repas est également disponible pour noter les repas que vous avez consommés ou prévus.  

La page d'accueil vous montre le nombre total de calories consommées dans la journée par rapport à votre objectif calorique. Une barre de progression vous indique le pourcentage de votre consommation de calories par rapport à votre objectif.  

La page Objectif vous permet de modifier votre poids cible et de consulter votre objectif en termes de calories et de macronutriments.  

Enfin, dans le menu en haut à droite, vous pouvez modifier les informations de votre compte, ce qui recalculera automatiquement votre objectif. Vous pouvez également changer votre nom, votre mot de passe ou vous déconnecter.  

## Logique métier

La logique métier de l'application réside dans le calcul de l'apport calorique journalier et de son équivalent en répartition des macronutriments.  
Tous les calculs ont lieu dans la classe `ObjectiveService` et sont concentrés dans la méthode `calculateObjective`.  
Le déroulement de la logique est la suivante :

- Calcul du métabolisme de base en utilisant la formule de Miffin-St Jeor, qui s'est révélé être une formule plus précise que celle de Harris-Benedict (voir [ici](https://www.jandonline.org/article/S0002-8223(05)00149-5/abstract)), auquel on a ajouté le facteur d'activité.  
- Ensuite, le programme calcule les calories supplémentaires/déficitaires nécessaires pour atteindre l'objectif de l'utilisateur (en prenant en compte la thermogénèse des aliments), en sachant qu'on reste dans un objectif sain de 0,5 kg gagné/perdu par semaine.  
- Enfin, on calcule l'équivalent en macronutriments de ces calories, en utilisant les recommandations de l'[ANSES](https://www.anses.fr/fr/system/files/NUT2012SA0103Ra-2.pdf) (Agence Nationale de Sécurité Sanitaire de l'Alimentation, de l'Environnement et du Travail) pour les macronutriments.  
- Une vérification finale s'assure que les valeurs caloriques restent dans des limites raisonnables (pas de régime en dessous de 1200 kcal/jour, pas de prise de poids au-dessus de 4000 kcal/jour).

La valeur de l'apport calorique journalier est calculée pour un mois entier (en fonction des mensurations de l'utilisateur), et est ensuite divisée par 30 pour obtenir la valeur journalière. Ainsi, si l'utilisateur souhaite obtenir un suivi évolutif sur l'évolution de son apport/déficite calorique, il lui est recommandé d'ajuster régulièrement son poids actuel dans la section `Paramètres` du site.  
Aussi, le code de la classe `ObjectiveService` est plus abondamment commenté que le reste des classes pour expliquer le déroulement de la logique métier.

## Auto-évaluation

L'application gère 5 entités : `User`, `Tags`, `Objective`, `FoodEntry`, et `FoodAgenda` (`User` et `Objective` ont une relation one-to-one, `FoodEntry` et `FoodAgenda` ont une relation one-to-many, et la relation entre `Tags` et `FoodEntry` est many-to-many).  
L'application dispose de fonctions CRUD et intègre également une logique métier pour calculer les calories que l'utilisateur doit consommer chaque jour afin d'atteindre son poids cible.  

L'application suit la consommation de calories et d'autres macronutriments afin que l'utilisateur puisse savoir s'il suit son objectif, ce qui lui permettra d'atteindre son poids cible.  
L'application effectue des opérations CRUD sur l'entité `FoodEntry` (modification, ajout, suppression) et affiche la liste des repas sur la page dédiée.  
L'application établit un lien entre les objectifs et les utilisateurs en base de données.  
Elle permet de retrouver l'utilisateur associé à un objectif grâce aux clés étrangères.  

L'application suit le modèle MVC, avec des contrôleurs, des vues et des modèles, comme pour le `ObjectiveService`, `ObjectiveController` et la page `objectives.html`.  
L'application utilise les méthodes HTTP GET, POST, PUT, DELETE de manière appropriée.  
La vue manipule bien les données fournies par le contrôleur associé, comme c'est le cas pour `objectives.html`.  


L'application est minimaliste et utilise le framework CSS Bootstrap.  
Le code source est disponible sur un dépôt GitHub et est commenté.  
Le dépôt bénéficie de commits réguliers de la part de tous les membres, bien qu'une pause collective ait eu lieu pour les révisions et les partiels.  
