Client.java est le repartiteur
ServeurInterface est l'interface reseau
Server est le serveur de calcul

start.sh build les classes, lance rmiregistry, et pourra faire les expériences pour nous
(lancer les différents serveurs de calcul et le repariteur sur la meme machine est le plus simple quoique lent)

pour lancer un main à partir du jar, appeler :
java -jar <nom_du_jar>
mais pour que les permissions soient les bonnes, utiliser les fichiers client et server ; Ex ./client <params>
(ce sont des fichiers bash, édite-les pour voir les commandes utilisées)

Pour le repartiteur, voici les arguments :
[Nom_Fichier_Texte] [s pour securise, n pour non-securise] [adresseIP1 a adresseIP4] 

Pour le server, voici les arguments:
[serverName] [int port] [int workCapacity] [float maliciousness]