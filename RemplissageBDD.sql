-- Désactiver les autocommits
SET autocommit OFF;

-- ------------------------ Utilisateur --------------------
INSERT INTO Utilisateur VALUES ('celinedion@gmail.com', 'Dion', 'Celine', '10 Rue des Tournesols');
INSERT INTO Utilisateur (Email, NomUser, PrenomUser, AddrUser) VALUES ('admin@example.com', 'Admin', 'Alice', '15 Rue des Lilas');
INSERT INTO Utilisateur (Email, NomUser, PrenomUser, AddrUser) VALUES ('vendeur1@example.com', 'Dupont', 'Paul', '89 Boulevard Clemenceau');
INSERT INTO Utilisateur (Email, NomUser, PrenomUser, AddrUser) VALUES ('support@tech.com', 'Martin', 'Julie', '120 Avenue de la Technologie');
INSERT INTO Utilisateur (Email, NomUser, PrenomUser, AddrUser) VALUES ('artisan@meubles.com', 'Lemoine', 'Jean', '45 Rue des Artisans');
INSERT INTO Utilisateur (Email, NomUser, PrenomUser, AddrUser) VALUES ('contact@mobilier.com', 'Durand', 'Sophie', '32 Rue des Meubles');
-- ------------------------ Salle de Vente --------------------
INSERT INTO SalleDeVente (IdSalle) VALUES (0);
INSERT INTO SalleDeVente (IdSalle) VALUES (1);
INSERT INTO SalleDeVente (IdSalle) VALUES (2);
INSERT INTO SalleDeVente (IdSalle) VALUES (3);
INSERT INTO SalleDeVente (IdSalle) VALUES (4);
INSERT INTO SalleDeVente (IdSalle) VALUES (5);
INSERT INTO SalleDeVente (IdSalle) VALUES (6);
INSERT INTO SalleDeVente (IdSalle) VALUES (7);
INSERT INTO SalleDeVente (IdSalle) VALUES (8);

-- ------------------------ Categorie Produit --------------------
INSERT INTO CategorieProduit VALUES ('Vetements', 'Tous types de vetements, toute saison');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Informatique', 'Produits électroniques et informatiques tels que ordinateurs, accessoires, etc.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Electronique', 'Produits électroniques incluant smartphones, gadgets, etc.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Mobilier', 'Meubles pour la maison et le bureau, tels que chaises et tables.');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Jouets', 'Articles pour enfants et jeux divers');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Sports', 'Équipements et accessoires sportifs');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Électroménager', 'Appareils et équipements électroménagers');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Beauté', 'Produits de beauté et soins personnels');
INSERT INTO CategorieProduit (NomCategorie, Description) VALUES ('Livres', 'Ouvrages et romans de toutes sortes');

-- ------------------------ Produit --------------------
INSERT INTO Produit VALUES (0, 'Manteau', 60.0, 1, 'Vetements', 'celinedion@gmail.com');
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
INSERT INTO Produit (idProduit, NomProduit, PrixDeRevient, Stock, NomCategorie, Email) VALUES (901, 'Le Meilleur des Mondes', 13, 100, 'Livres', 'vendeur1@example.com');

-- ------------------------ Caracteristique Produit --------------------
INSERT INTO CaracteristiqueProduit VALUES ('Couleur', 0, 'Rouge');

-- ------------------------ Vente --------------------
INSERT INTO Vente VALUES (0, 30.0, 0, 1, 1, 0, 0, 5,TO_TIMESTAMP('2024-12-01 18:22:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO VenteDureeIllimitee VALUES (0, 5);

-- ------------------------ Propose --------------------
INSERT INTO Propose VALUES ('Vetements', 0);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Informatique', 1);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Electronique', 2);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Mobilier', 3);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Vetements', 4);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Jouets', 5);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Sports', 6);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Électroménager', 7);
INSERT INTO Propose (NomCategorie, IdSalle) VALUES ('Beauté', 8);

-- ------------------------ Offre --------------------
INSERT INTO DateHeureOffre VALUES (TO_TIMESTAMP('2024-12-01 18:25:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Offre VALUES (0, 'celinedion@gmail.com', 61.0, 1, TO_TIMESTAMP('2024-12-01 18:25:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Commit des transactions
COMMIT;
