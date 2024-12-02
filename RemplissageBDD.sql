-- Code SQL qui va peupler la base de donnée

-- On desactive les autocommits
set autocommit off;

------------------------ Utilisateur --------------------

INSERT INTO Utilisateur VALUES('celinedion@gmail.com','Dion','Celine','10 Rue des Tournesols');
INSERT INTO Utilisateur (Email, Nom, Role) VALUES ('admin@example.com', 'Admin', 'Administrateur');
INSERT INTO Utilisateur (Email, Nom, Role) VALUES ('vendeur1@example.com', 'Vendeur1', 'Vendeur');
INSERT INTO Utilisateur (Email, Nom, Role) VALUES ('contact@mobilier.com', 'Contact Mobilier', 'Artisan');
INSERT INTO Utilisateur (Email, Nom, Role) VALUES ('support@tech.com', 'Support Tech', 'Technicien');
INSERT INTO Utilisateur (Email, Nom, Role) VALUES ('artisan@meubles.com', 'Artisan Meubles', 'Artisan');



------------------------ Salle de Vente --------------------
insert into SalleDeVente VALUES(0);
INSERT INTO SalleDeVente (IdSalle) VALUES (1);
INSERT INTO SalleDeVente (IdSalle) VALUES (2);
INSERT INTO SalleDeVente (IdSalle) VALUES (3);
INSERT INTO SalleDeVente (IdSalle) VALUES (4);
INSERT INTO SalleDeVente (IdSalle) VALUES (5);
INSERT INTO SalleDeVente (IdSalle) VALUES (6);
INSERT INTO SalleDeVente (IdSalle) VALUES (7);
INSERT INTO SalleDeVente (IdSalle) VALUES (8);

------------------------ Categorie Produit --------------------
insert into CategorieProduit VALUES('Vetements', 'Tous types de vetements, toute saison');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Informatique', 'Produits électroniques et informatiques tels que ordinateurs, accessoires, etc.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Electronique', 'Produits électroniques incluant smartphones, gadgets, etc.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Mobilier', 'Meubles pour la maison et le bureau, tels que chaises et tables.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Jouets', 'Articles pour enfants et jeux divers');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Sports', 'Équipements et accessoires sportifs');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Électroménager', 'Appareils et équipements électroménagers');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Beauté', 'Produits de beauté et soins personnels');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Livres', 'Ouvrages et romans de toutes sortes');

------------------------ Produit --------------------
insert into Produit VALUES(0,'Manteau',60.0, 1, 'Vetements','celinedion@gmail.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (1, 'Laptop Gamer', 850.75, 15, 'Informatique', 'admin@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (2, 'Smartphone Pro', 499.99, 50, 'Electronique', 'vendeur1@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (3, 'Chaise Ergonomique', 120.30, 25, 'Mobilier', 'contact@mobilier.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (4, 'Casque Bluetooth', 75.49, 40, 'Electronique', 'support@tech.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (5, 'Table en bois massif', 300.99, 10, 'Mobilier', 'artisan@meubles.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (10, 'T-shirt en coton', 10.99, 50, 'Vetements', 'admin@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (11, 'Jeans bleu', 25.49, 30, 'Vetements', 'vendeur1@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (12, 'Peluche ours', 15.99, 100, 'Jouets', 'contact@mobilier.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (13, 'Puzzle 500 pièces', 12.49, 40, 'Jouets', 'support@tech.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (14, 'Ballon de football', 9.99, 70, 'Sports', 'artisan@meubles.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (15, 'Raquette de tennis', 49.99, 20, 'Sports', 'admin@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (16, 'Machine à café', 89.99, 15, 'Électroménager', 'vendeur1@example.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (17, 'Aspirateur sans fil', 129.49, 10, 'Électroménager', 'contact@mobilier.com');
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (901, 'Le Meilleur des Mondes', 13, 100, 'Livres', 'vendeur1s@example.com');


------------------------ Caracteristique Produit --------------------
insert into CaracteristiqueProduit VALUES('Couleur', 0, 'Rouge');

------------------------ Vente --------------------
-- On ajoute la vente 0, le prix de depart va etre de 30.0, elle va etre 
-- a duree illimitee, non revocable, Montante, multiple, et le produit sera 
-- la manteau ajoute au dessus et la salle sera la salle ajoutee au dessus
insert into Vente VALUES(0,30.0,0,1,1,0,0);
-- Comme on vient de creer une vente a duree illimite il faut
-- ajouter celle ci das la table, avec le meme idVente, et un delai de 5
insert into VenteDureeIllimitee VALUES(0,5);

------------------------ Propose (lien salle et catégorie) --------------------
insert into Propose VALUES('Vetements',0);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Informatique', 1);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Electronique', 2);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Mobilier', 3);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Vêtements', 4);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Jouets', 5);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Sports', 6);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Électroménager', 7);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Beauté', 8);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Jouets', 5);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Sports', 6);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Électroménager', 7);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Beauté', 8);

insert into DateHeureOffre VALUES('25-OCT-24 09:30:00');
insert into Offre VALUES(0,'celinedion@gmail.com', 61.0,1,'25-OCT-24 09:30:00');
commit;