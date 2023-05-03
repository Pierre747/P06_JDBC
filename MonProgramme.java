/*
 * On commence par penser à lancer XAMPP avec le serveur Apache et MYSQL
 */

package fr.etudeJDBC;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class MonProgramme
{

	public static void main(String[] args)
	{
		
		// =============================================================================== //
		
		// 1 - Je dois me connecter à la base
		// com.mysql.cj.jdbc.Driver <= je me note le chemin du driver
		
		// Désormais, on va fouiller dans la bibliothèque afin de charger le pilote :
		// Le pilote est une classe qui assure les échanges entre l'application et la base.
		// Il transmet les ordres SQL te remonte les données ou les exceptions.
		// Le pilote MySQL se trouve dans la bibliothèque msql-connector-java-version-XXXX.jar
		// Cette archive .jar doit être intégrée dans le Build Path de l'application.
		// Clic droit sur le projet >> Build Path >> Configure Build Path >> Librairies >> Add external Jar 
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver"); // On lui passe le chemin du driver
		} catch (ClassNotFoundException ex)
		{
			System.out.println("Échec chargement pilote ❌ : ");
			System.out.println(ex);
			System.exit(5); // On met un chiffre random sauf 0 car 0 c'est pour dire que tout va bien
		}
		
		System.out.println("Pilote chargé ✔");
		
		
		// =============================================================================== //
		
		
		// On va désormais créer une connexion :
		// On va stocker la chaine de connexion dans une variable
		
		String chaine_de_connexion = "jdbc:mysql://localhost/etude_jdbc?user=stagiaire&password=pwdstagiaire&noAccessToProcedureBodies=true&useSSL=false"; 
		// Elle commence par le protocole : jdbc, puis on stipule le type de base de données : mysql, 
		// puis on lui donne l'ip : 127.0.0.1(soit localhost), désormais on lui précise le nom de la base de données : etude_jdbc,
		// on lui passe ensuite les paramètres(query string) que l'on note après un point d'interrogation : ?user=stagiaire&password=pwdstagiaire&noAccessToProcedureBodies=true&useSSL=false
		
		Connection cnx = null; // On pense à importer : import java.sql.Connection;
		
		try
		{
			cnx = DriverManager.getConnection(chaine_de_connexion); // On pense à importer : import java.sql.DriverManager;
		} catch (SQLException ex)
		{
			System.out.println("Échec création connexion ❌ : ");
			System.out.println(ex);
			System.exit(6);
		} 

		System.out.println("Connexion créée ✔");
		
		
		
		// =============================================================================== //

		
		
		// On doit désormais créer le cadre nécéssaire pour passer des ordres, des requêtes SQL.
		// On va stocker tout ça dans une variable stmt(Statement)
		
		Statement stmt = null; // On pense à importer : import java.sql.Statement;
		
		try
		{
			stmt = cnx.createStatement();
		} catch (SQLException ex) 
		{
			System.out.println("Échec création statement ❌ : ");
			System.out.println(ex);
			System.exit(7);
		}
		
		System.out.println("Statement créé ✔");
		
		
		// =============================================================================== //
		
		// On va utiliser le statement pour faire un SELECT de tous les articles de la table Personne
		// On va stocker tout ça dans une variable rs(ResultSet)
		
		ResultSet rs = null; // On sélectionne le ResultSet puis on importe la classe : import java.sql.ResultSet;
		
		try
		{
//			rs = stmt.executeQuery("select * from Personnes"); 
			rs = stmt.executeQuery("select IdPersonne, Nom as \"Nom personne\", Prenom as \"Prenom personne\", Adresse, " + 
									"CP, Ville, Telephone, Email, DateNaissance from Personnes");

		} catch (SQLException ex)
		{
			System.out.println("Échec création ResultSet ❌ : ");
			System.out.println(ex);
			System.exit(8);
		}
		
		// Dans le jeu de résultats retournés (dans le rs), on ne trouve pas que les données sélectionnées
		// mais aussi des informations sur ces données : des méta-données.
		
		// Récupérons le nom de la base d'où vient une certaine colonne (ici, la première)
		
		try
		{
			// Comme toutes les connexions viennent de la même base de donnéees, 
			// il suffit de trouver la première colonne pour savoir d'où viennent les autres.

			String nom_base = rs.getMetaData().getCatalogName(1);
			// On met getCatalogName pour trouver le nom de la base de données

			System.out.println("\nLe nom de la base de données : " + nom_base);

			//------------ >
			
			String nom_table = rs.getMetaData().getTableName(1); 
			// On récupère le nom de la table avec getTableName
			
			System.out.println("\nLe nom de la table : " + nom_table);
			System.out.println();
			
			//------------ >
			
			// On va boucler pour obtenir des informations sur notre base de données
			// LEXIQUE : tuple = ligne.
			
			for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
			{
				System.out.println("Colonne " + i + 
						", nom colonne : " + rs.getMetaData().getColumnName(i) +
						", alias colonne : '" + rs.getMetaData().getColumnLabel(i) + "'" +
						", type SQL : " + rs.getMetaData().getColumnTypeName(i) +
						", taille : " + rs.getMetaData().getColumnDisplaySize(i)
						);
			}
			
		} catch (SQLException ex)
		{
			System.out.println("Échec de lecture des méta-données ❌ : ");
			System.out.println(ex);
			System.exit(9);
		}
		
		
		// =============================================================================== //
		
		
		// On va désormais vouloir lire les donnéés
		
		System.out.println("\nLes données sélectionnées sont : ");
		
		// Je récupère les données avec la méthode next() du ResultSet.
		// Il faut savoir que l'objet rs de type ResultSet pointe juste avant la première ligne.
		// Donc, pour arriver à la première ligne, il faut commencer par un next() !
		// Le rs ne CONTIENT PAS toutes les lignes sélectionnées. Il se comporte comme un curseur et
		// ne contient que la ligne de données courante.
		// Pour passer d'une ligne à l'autre, il faut demander à RS la ligne suivante avec next().
		// Cette méthode retourne true si la ligne courante contient des lignes suivante et false
		// une fois que nous sommes arrivés au bout.
		
		try
		{
			while(rs.next())
			{
				// Il existe deux façons de récupérer les données d'une colonne :
				// 1 - Par la position de la colonne (1, 2, 3, 4, 5, etc...)
				// -=Rappel=- Avec SQL, les indexes de colonnes commencent à 1 ! 
				// 2 - Par le nom de la colonne.
				
				//------------ >
				
				// Exemple avec la première solution, la position de la colonne :
				
				int id = rs.getInt(1); // La première colonne de la table étant la colonne ID
				// Cependant, cette méthode est dangereuse car on peut altérer une table
				
				//------------ >
				
				// Exemple avec la deuxième solution, le nom de la colonne :
				String nom = rs.getString("Nom personne"); // Ici, on pense à noter l'alias
				String prenom = rs.getString("Prenom personne"); // Ici, on pense à noter l'alias
				String adresse = rs.getString("adresse");
				String cp = rs.getString("cp");
				String ville = rs.getString("ville");
				String telephone = rs.getString("telephone");
				String email = rs.getString("email");
				Date date = rs.getDate("DateNaissance"); // On pense à importer : import java.util.Date;
				
				//------------ >
				
				// Désormais on affiche les résultats
				
				System.out.println("ID : " + id);
				System.out.println("Nom : " + nom);
				System.out.println("Prénom : " + prenom);
				System.out.println("Adresse : " + adresse);
				System.out.println("Code Postal : " + cp);
				System.out.println("Ville : " + ville);
				System.out.println("Téléphone " + telephone);
				System.out.println("Email : " + email);
				System.out.println("Date de naissance : " + date);
				System.out.println();
			}
		}
		catch(SQLException ex)
		{
			System.out.println("Échec de lecture du ResultSet ❌ : ");
			System.out.println(ex);
			System.exit(10);
		}
		
		
		//------------ >
		
		// IMPORTANT : On pense à fermer le ResultSet
		try
		{
			if(rs != null) // On ne peut pas fermer rs si sa valeur est nulle
			{
				rs.close();
			}
			
		} catch (SQLException ex)
		{
			System.out.println("Échec de la fermeture du rs.close() ❌ : ");
			System.out.println(ex);
			System.exit(11);
		}
		
		
		//------------ >
		
		// GESTION DES REQUÊTES
		
		// Je commence par créer une variable dans laquelle je vais stocker mes requêtes
		String rqt;
		
		// DELETE
		
		rqt = "delete from Personnes where Nom like 'lenom%' and Prenom like 'leprenom%'";
							
		try
		{
		int nb = stmt.executeUpdate(rqt);
		System.out.println("Nombre de lignes supprimées : " + nb);
		}
		catch (SQLException ex)
		{
		System.out.println("Échec du delete ❌ : ");
		System.out.println(ex);
		System.exit(13);
		}
		

		// INSERT
		
		rqt = "insert into Personnes (Nom, Prenom) values ('LeNom', 'LePrénom'), ('LeNom2', 'LePrenom2')";
		
		try
		{
			int nb = stmt.executeUpdate(rqt);
			System.out.println("Nombre de lignes insérées : " + nb);
		} catch (SQLException ex)
		{
			System.out.println("Échec du create ❌ : ");
			System.out.println(ex);
			System.exit(12);
		}
		
		// UPDATE
		
		rqt = "update Personnes set Nom=concat(Nom, 'xx'), Prenom=concat(Prenom, 'yy') where Nom like 'lenom%' and Prenom like 'leprenom%'";
		
		try
		{
			int nb = stmt.executeUpdate(rqt);
			System.out.println("Nombre de lignes mises à jour : " + nb);
		} catch (SQLException ex)
		{
			System.out.println("Échec de l'update ❌ : ");
			System.out.println(ex);
			System.exit(13);
		}
		
		System.out.println();
		
		//------------ >
		
		// IMPORTANT : On pense à fermer le Statement
		
		try
		{
			if(stmt != null)// On ne peut pas fermer le Statement si sa valeur est nulle
			{
				stmt.close();
			}
		} catch (SQLException ex)
		{
			System.out.println("Échec de la fermeture du Statement ❌ : ");
			System.out.println(ex);
			System.exit(14);
		}
		
		
		//------------ >
		
		// CallableStatement
		
		// Il s'agit d'une interface utiliséé pour appeller les procédures stockées et les fonctions
		
		// On va commencer par utiliser CallableStatement avec une fonction :
		
		// Je commence par créer une variable dans laquelle je vais stocker mes statements
		
		 CallableStatement callableStmt = null; // On pense à importer : import java.sql.CallableStatement;
		 
		 int idInsertion = 0;
		
		try
		{
			callableStmt = cnx.prepareCall("{? = call get_id_insertion()}");
			// où ? représente un paramètre qui doit être exécuté avant l'exécution de la requête
			// "? = call get_id_insertion()" est une requête paramétrée
			System.out.println("---===>>>" + callableStmt);
			
			// Configurer la requête paramétrée en définissant le type du seul paramètre
			// qui doit être configuré, par sa position.
			
			callableStmt.registerOutParameter(1, Types.INTEGER); // Position : 1 car nous sommes en SQL
			
			// Il ne nous reste plus qu'à exécuter le callableStatement
			
			callableStmt.execute();
			
			// On récupère la valeur retournée à partir du callableStatement
			
			idInsertion = callableStmt.getInt(1); // On met 1 car on est en SQL
			
			// On affiche le résultat :
			
			System.out.println("L'ID de la dernière insertion est : " + idInsertion);
			
		}
		catch (SQLException ex)
		{
			System.out.println("Échec de la création du CallableStatement ❌ : ");
			System.out.println(ex);
			System.exit(15);
		}
		
		//------------ >
		
		// IMPORTANT : On pense à fermer le CallableStatement
		
		try
		{
			if(callableStmt != null)
			{
				callableStmt.close();
			}
		}
		catch (SQLException ex)
		{
			System.out.println("Échec de la fermeture du CallableStatement ❌ : ");
			System.out.println(ex);
			System.exit(15);
		}
		
		
		//------------ >
		
		// Utiliser CallableStatement avec une procédure stockée.
		
		try
		{
			callableStmt = cnx.prepareCall("call modifier_telephone_email(?, ?, ?)");
			// On va donc préciser les différents paramètres à exécuter pour ma procédure stockée.
			
			
			// Le premier ? de mes parenthèses :
			// Le type est précisé par la méthode set utilisée. setInt()
			// Le premier paramètre est sa position, 1. 
			// Le deuxième paramètre est la valeur du premier paramètre, .
			callableStmt.setInt(1, idInsertion);
			
			
			// Le deuxième ? de mes parenthèses :
			callableStmt.setString(2, "1111222233"); // On définit un nouveau numéro de téléphone
			
			// Le troisième ? de mes parenthèses :
			callableStmt.setString(3, "email" + idInsertion + "@gmail.com"); // On définit un nouveau mail
			
			
			callableStmt.execute();
			
			// On pense à fermer le callable statement
			
			try
			{
				if(callableStmt != null)
				{
					callableStmt.close();
				}
			}
			catch (SQLException ex)
			{
				System.out.println("Échec de la fermeture du CallableStatement ❌ : ");
				System.out.println(ex);
				System.exit(15);
			}
			
		}
		catch (SQLException ex)
		{
			System.out.println("Échec d'exécution de ma procédure stockée ❌ : ");
			System.out.println(ex);
			System.exit(16);
		}
		
		
		
		//------------ >
		
		
		// Interface Prepared Statement (qui étend Statement) : Ordres SQL paramétrés
		
		// Cette interface diffère de Statement sur 2 points principaux :
		// 1 - Les instances de PreparedStatement contiennent une instruction SQL déjà compilée : prepared.
		// 2 - Les instructions SQL de l'objet PreparedSattement contiennent un ou plusieurs paramètres d'entrée.
		//     Ces paramètres sont représentés par des ? et doivent être spécifié avant l'exécution de l'ordre SQL.
		
		
		// INSERT
		
		rqt = "insert into Personnes (Nom, Prenom) values (?, ?)";
		
		PreparedStatement prepStmt = null; // On importe : import java.sql.PreparedStatement;
		
		try
		{
			prepStmt = cnx.prepareStatement(rqt);
		}
		catch (SQLException ex)
		{
			System.out.println("Échec d'exécution du PreparedStatement INSERT ❌ : ");
			System.out.println(ex);
			System.exit(17);
		}
		
		// Passer les paramètres de l'ordre SQL paramétrés et exécuter le Prepared Statement(PS)
		// Vu que l'on veut modifier trois lignes, on va mettre ça dans une boucle for.
		
		try
		{
			for(int i = 1; i <= 3; i++)
			{
				
				prepStmt.setString(1, "lenom" + i); // Le premier paramètre est la position
				prepStmt.setString(2, "leprenom" + i); // Le deuxième paramètre, ce que l'on veut modifier
				prepStmt.executeUpdate(); // On pense à exécuter le PS pour chaque ligne
			}
			
			prepStmt.close(); // On pense à fermer le PS
			
		}
		catch(SQLException ex)
		{
			System.out.println("Échec d'exécution du PreparedStatement INSERT ❌ : ");
			System.out.println(ex);
			System.exit(18);
		}
		
		
		//------------ >
		
		// Prepared Statement (PS) V2
		
		// On va refaire la même chose (insérer plusieurs lignes) avec une seule requête SQL
		// RAPPEL : MYSQL accepte une requête d'instruction de ce type :
		// rqt = "insert into Personnes (Nom, Prenom, Email) values (?, ?, ?), (?, ?, ?), (?, ?, ?)";
		// où les points d'interrogations représentent les valeurs à insérer.
		
		int nbInsertions = 10;
		
		rqt = "insert into Personnes (Nom, Prenom, Email) values ";
		
		// Créer la partie contenant les valeurs paramétrées à l'aide d'une boucle.
		
		for(int i = 0; i < nbInsertions; i++)
		{
			if(i > 0)
			{
				rqt += ", (?, ?, ?)";
			}
			else
			{
				rqt += "(?, ?, ?)"; // On met ça pour débuter sans la virgule
			}
			
		}
		
		// Pour vérifier ma requête :
		 System.out.println("\nTest de ma requête : " + rqt);
		
		// On va injecter les valeurs des paramètres et exécuter la requête
		 
		 try
		 {
			// Pour cela, on crée un PS contenant la requête paramétrée :
			prepStmt = cnx.prepareStatement(rqt, nbInsertions);
			
			// On va injecter les valeurs des paramètres
			int noArgurment = 1; // Pour changer chacun des trois arguments dans nos parenthèses
			for(int i = 1; i <= nbInsertions; i++)
			{
				prepStmt.setString(noArgurment++, "lenom" + (100 + i)); // On pense à incrémenter le noArgurment
				prepStmt.setString(noArgurment++, "leprenom" + (100 + i));
				prepStmt.setString(noArgurment++, "email" + (100 + i));
			}
			
			// On va maintenant exécuter la requête
			int nbLignes = prepStmt.executeUpdate();
			
			// On affiche le nombre de lignes insérées
			System.out.println(nbLignes + " lignes ont été insérees ✔");
			
			// Et on ferme le PS
			prepStmt.close();
			
		 }
		 catch (SQLException ex)
		 {
		    System.out.println("Échec de l'insertion du PS V2 ❌ : ");
			System.out.println(ex);
			System.exit(19);
		 }
		 
		 
		 //------------ >
		 
		 
		 // Les transactions
		 
		 // Les connexions JDBC sont en mode auto-commit, par défaut.
		 // Chaque instruction SQL est considérée comme indépendante et est validée(commit) après son exécution.
		 // Il va donc falloir annuler ce comportement par défaut pour pouvoir intéragir avec cela.
		 
		 try
		 {
			cnx.setAutoCommit(false);
		 }
		 catch (SQLException ex)
		 {
			System.out.println("Échec set Auto Commit : FALSE ❌ : ");
			System.out.println(ex);
			System.exit(20);
		 }
		 
		 stmt = null; 
		 // RAPPEL : l'ancien statement dont l'adresse est encore contenue dans la variable
		 // stmt a été fermée. Pour ne pas travailler, par erreur, avec ce statement fermé, je 
		 // le passe à null.
		 
		 // On crée le statement
		 
		 try
		 {
			stmt = cnx.createStatement();
		 } 
		 catch (SQLException ex)
		 {
			System.out.println("Échec de création du statement ❌ : ");
			System.out.println(ex);
			System.exit(21);
		 }
		 
		 // Exécuter les 3 requêtes suivantes dont la dernière ne va pas réussir
		 
		 // DELETE : tous les articles de la table Personnes
		 
		 try
		 {
			rqt = "delete from Personnes";
			stmt.executeUpdate(rqt);
			
			// On va aussi insérer un article
			rqt = "insert into Personnes (Nom, Prenom) values ('lenom200', 'leprenom200')";
			stmt.executeUpdate(rqt);
			
			// On va insérer ce deuxième article une deuxième fois : erreur.
			// Cette deuxième insertion va être rejetée car on a une contrainte d'unicité
			// sur le binôme nom, prénom.
			rqt = "insert into Personnes (Nom, Prenom) values ('lenom200', 'leprenom200')";
			stmt.executeUpdate(rqt);
			
			//------------ >
			
			// Pour éviter l'erreur, on va ajouter un undersore _ pour que l'ajout soit faisable
			// rqt = "insert into Personnes (Nom, Prenom) values ('lenom200_', 'leprenom200_')";
			// Le doublon n'existera pas et l'insertion réussira et les modifications, y compris le delete,
			// seront validées, committées. Donc dans la table, je ne trouverai que les deux dernières insertions.
			
			//------------ >
			
			// Si tout va bien, que j'arrive ici sans ausune erreur qui m'aurait envoyé dans le 
			// catch n'a eu lieu, je commit (je valide) l'état final de la table ou des tables.
			
			cnx.commit();
			
			System.out.println("\nTransaction réussie et commitée ✔");
		 }
		 catch (SQLException ex)
		 {
			System.out.println("\nÉchec du DELETE de Personnes ❌ : ");
			System.out.println(ex.getMessage());
			
			// Si on a un soucis, on va rollbacker l'état initial des bases de données
			
			try
			{
				cnx.rollback();
				System.out.println("\nLa transaction a été annulée ✔");
			}
			catch (SQLException ex2) // On pense à modifier le nom de l'erreur pour éviter de choper le précédent ;)
			{
				System.out.println("\nÉchec du rollback ❌ : ");
				System.out.println(ex2);
				System.exit(22);
			}
		 }
		 
		 // Il ne nous reste plus qu'à tout fermer : statement et connexion
		 
		 try
		 {
			 if(stmt != null)
			 {
				 stmt.close();
			 }
			 System.out.println("\nStatement correctement fermé ✔");
		 }
		 catch (SQLException ex)
		 {
			System.out.println("\nÉchec de la fermeture du statement ❌ : ");
			System.out.println(ex);
			System.exit(22);
		 }
		 
		 try
		 {
			cnx.close();
			System.out.println("Connexion correctement fermée ✔");
		 }
		 catch (SQLException ex)
		 {
			System.out.println("Échec de la fermeture de la connexion ❌ : ");
			System.out.println(ex);
			System.exit(23);
		 }
		 
		 
		 /*
		  * EXERCICE
		  * Utiliser la base maBase sur Mysql et JDBC pour...
		  * - Afficher : tous les employés avec leur société, leur pays, le pays de leur société, etc...
		  * - Insérer : un nouveau pays, une nouvelle société, un nouvel employé dont le pays est le dernier inséré
		  *             et la société inséré plus haut
		  * - Modifier : cet employé (nom, prénom, société, etc...)
		  * - Supprimer : le dernier employé inséré, avant insertion, lors de la prochaine exécution
		  * - Définir : dans le script SQL une procédure stockée capable de modifier le salaire mensuel et l'ID du responsable
		  *             d'un employé.
		  * - Recréer : la base en lançant le script avec la nouvelle procédure stockée.
		  * - Insérer : 3 employés d'un seul coup avec une requête paramétrée.
		  * - Insérer : dans un environnement transactionnel, un nouveau pays, une nouvelle société et un nouvel employé.
		  * - Faire : une erreur de syntaxe ou de dépassement de la taille d'une colonne et gérer la transaction.
		  */
		 
	} // Fin du Main

} // Fin de MonProgramme

// SCRIPT A METTRE DANS MYSQL WORKBENCH :

//drop database if exists MaBase;
//
//CREATE DATABASE MaBase;
//
//USE Mabase;
//
//-- créer la table Pays
//
//drop table if exists Pays;
//
//CREATE TABLE Pays
//(
//	idPays integer not null auto_increment,
//   	Pays varchar(20) NOT NULL UNIQUE,
//
//	Primary Key (IdPays)
//);
//
//
//-- insérer des enregistrements
//insert into Pays values (1, 'Pays 1');
//
//-- ou, avec des insertions multiples:
//insert into Pays values 
//(2, 'Pays 2'),
//(3, 'Pays 3'),
//(4, 'Pays 4'),
//(8, 'Pays 8'),
//(9, 'Pays 9');
//
//
//-- créer la table Societes
//drop table if exists Societes;
//
//CREATE TABLE Societes
//(
//	idSociete varchar(10) Primary Key,
//   	Societe varchar(20) NOT NULL UNIQUE,
//	idPays int -- REFERENCES Pays(IdPays)
//);
//
//
//-- insérer des enregistrements
//insert Societes values 
//('soc1', 'Société 1', 1),
//('soc2', 'Société 2', 2),
//('soc3', 'Société 3', 2),
//('soc4', 'Société 4', 4),
//('soc5', 'Société 5', null),
//('soc6', 'Société 6', null),
//('soc7', 'Société 7', 10),	-- possible seulement si REFERENCES Pays(IdPays) est en commentaire plus haut	
//('soc8', 'Société 8', 12);	-- possible seulement si REFERENCES Pays(IdPays) est en commentaire plus haut
//
//-- créer la table Employes
//drop table if exists Employes;
//
//CREATE TABLE Employes
//(
//	idEmploye integer auto_increment Primary Key,
//		-- déclaration de la clé primaire au niveau table (ou 'inline')
//   	Nom varchar(20) NOT NULL,
//   	Prenom varchar(20),
//	idPays integer,
//   	idSociete varchar(10),
//	idResponsable integer,
//	SalaireMensuel float(7,2) DEFAULT 0,
//
//	-- déclaration de la clé primaire au niveau table
//	-- Primary Key(idPersonne),
//	FOREIGN KEY (idPays) REFERENCES Pays(IdPays),
//	FOREIGN KEY (idSociete) REFERENCES Societes(IdSociete) ON DELETE CASCADE,
//	FOREIGN KEY (idResponsable) REFERENCES Employes(IdEmploye),
//	-- exemple de contrainte sur 2 ou plusieurs champs
//	CONSTRAINT CoupleNomPrenomUnique UNIQUE (Nom, Prenom)
//);
//
//
//-- les responsables d'abord
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(3, 'Nom3', 'Prénom3', 1, 'soc1', NULL, 1900);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(2, 'Nom2', 'Prénom2', NULL, 'soc2', 3, DEFAULT);
//
//-- les autres après
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(1, 'Nom1', 'Prénom1', 3, 'soc1', 2, 1000);
//
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(4, 'Nom4', 'Prénom4', 1, 'soc4', 2, 800);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(5, 'Nom5', 'Prénom5', 1, 'soc5', 2, 1300);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(6, 'Nom6', 'Prénom6', 9, 'soc6', 2, 1700);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(7, 'Nom7', 'Prénom7', NULL, NULL, NULL, DEFAULT);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(8, 'Nom8', 'Prénom8', 8, NULL, NULL, 1500);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(9, 'Nom9', 'Prénom9', 9, NULL, NULL, 3000);
//
//insert into Employes (idEmploye, Nom, Prenom, idPays, idSociete, idResponsable, SalaireMensuel)
//values(10, 'Nom10', 'Prénom10', 8, 'soc4', NULL, 2100);
//
