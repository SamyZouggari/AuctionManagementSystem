-- Code SQL qui va peupler la base de donnée

-- On desactive les autocommits
set autocommit off;

insert into Utilisateur VALUES('celinedion@gmail.com','Dion','Celine','10 Rue des Tournesols');
insert into SalleDeVente VALUES(0);
insert into CategorieProduit VALUES('Vetements', 'Tous types de vetements, toute saison');
insert into Produit VALUES(0,'Manteau',60.0, 1, 'Vetements','celinedion@gmail.com');
insert into CaracteristiqueProduit VALUES('Couleur', 0, 'Rouge');
-- On ajoute la vente 0, le prix de depart va etre de 30.0, elle va etre 
-- a duree illimitee, non revocable, Montante, multiple, et le produit sera 
-- la manteau ajoute au dessus et la salle sera la salle ajoutee au dessus
insert into Vente VALUES(0,30.0,0,1,1,0,0);
-- Comme on vient de creer une vente a duree illimite il faut
-- ajouter celle ci das la table, avec le meme idVente, et un delai de 5
insert into VenteDureeIllimitee VALUES(0,5);
insert into Propose VALUES('Vetements',0);
insert into DateHeureOffre VALUES('25-OCT-24 09:30:00');
insert into Offre VALUES(0,'celinedion@gmail.com', 61.0,1,'25-OCT-24 09:30:00');
commit;