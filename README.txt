Client.java est le repartiteur
ServeurInterface est l'interface reseau
Server est le serveur de calcul

start.sh build les classes, lance rmiregistry, et pourra faire les exp�riences pour nous
(lancer les diff�rents serveurs de calcul et le repariteur sur la meme machine est le plus simple quoique lent)

pour lancer un main � partir du jar, utiliser les fichiers client et server ;
Ex ./client <params> (ce sont des fichiers bash, ouvrez-les dans un  �diteur de texte pour voir les commandes utilis�es)

Pour le repartiteur, voici les arguments :
[Nom_Fichier_Texte] [s pour securise, n pour non-securise] [adresseIP1 a adresseIP4] 

Pour le server, voici les arguments:
[serverName] [int port] [int workCapacity] [float maliciousness]