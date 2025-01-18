# SAE Réseau/système

Lucas THOMAS
Ugo DOMINGUEZ
Antony PERDIEUS
Yasin KESKIN

## Manuel d’utilisation

Lors de l’exécution du programme, il faut d'abord choisir notre nom de joueur. Ensuite, deux options s’offrent à nous : soit consulter nos résultats dans la BD avec `career`, les résultats étant associés au nom du joueur, soit utiliser `connect <pseudo>` pour défier un adversaire, la partie se lançant alors. Lors du choix du pseudo, nous sommes placés dans un salon d’attente où nous pouvons soit attendre un défi, soit provoquer un adversaire.

## Choix technique

Concernant l’historique des joueurs, il est stocké dans une BD à partir du nom du joueur, ce qui permet de récupérer ses informations depuis n’importe quelle session, plutôt que de stocker ces données à partir de l’IP, ce qui restreindrait l’utilisation du compte à une seule machine. Pour la gestion de la base de données, nous avons utilisé SQLite et JDBC. Nous avons également utilisé Gradle pour gérer les dépendances entre JDBC et SQLite.

## Diagramme de classe 

## Protocole

Lors de l’exécution du programme, le serveur demande son nom à l’utilisateur. Ensuite, lorsque l’utilisateur utilise `connect <pseudo>`, le serveur lance la partie sur le client cible et sur le client ayant initié le défi. La partie se déroule de cette manière : le serveur envoie l’information de la grille aux deux joueurs et demande au joueur dont c’est le tour de choisir une colonne. Tant que ce joueur ne renvoie pas de choix valide, le serveur attend sa réponse. Ainsi de suite, jusqu’à ce qu’un des joueurs gagne, ou qu'une égalité se produise. Pour l’affichage de l’historique des parties, le serveur reçoit la requête du client (`career`), interroge la base de données puis envoi les informations au client.
