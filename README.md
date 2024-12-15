# JEE

YourDiet est une application web de suivi de régime alimentaire. Elle permet à l'utilisateur de calculer son apport calorique journalier et de suivre son évolution au fil du temps, ainsi que de renseigner ses repas quotidiennement.  

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
