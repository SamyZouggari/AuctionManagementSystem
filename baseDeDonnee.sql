DROP TABLE Offre;
DROP TABLE Propose;
DROP TABLE VenteDureeIllimitee;
DROP TABLE VenteDureeLimitee;
DROP TABLE Vente;
DROP TABLE CaracteristiqueProduit;
DROP TABLE Produit;
DROP TABLE CategorieProduit;
DROP TABLE Utilisateur;
DROP TABLE SalleDeVente;
DROP TABLE DateHeureOffre;

CREATE TABLE SalleDeVente(IdSalle int NOT NULL CONSTRAINT KSalle primary key);

CREATE TABLE CategorieProduit(NomCategorie varchar(100) NOT NULL CONSTRAINT KCategorieProduit primary key,
                            Description varchar(100) NOT NULL);

CREATE TABLE Utilisateur(Email varchar(100) NOT NULL CONSTRAINT Kutilisateur primary key,
                        NomUser varchar(100) NOT NULL,
                        PrenomUser varchar(100) NOT NULL,
                        AddrUser varchar(100) NOT NULL);

CREATE TABLE Produit(idProduit int NOT NULL CONSTRAINT Kproduit primary key,
                NomProduit varchar(100) NOT NULL,
                PrixDeRevient float NOT NULL CONSTRAINT PrixDeRevientPositif CHECK(PrixDeRevient>0),
                Stock int NOT NULL CONSTRAINT StockPositif CHECK(Stock >=0),
                NomCategorie varchar(100) NOT NULL,
                Email varchar(100) NOT NULL,
                CONSTRAINT KFNomCategorieProduit FOREIGN KEY(NomCategorie) REFERENCES CategorieProduit(NomCategorie),
                CONSTRAINT KFemailProduit FOREIGN KEY(Email) REFERENCES Utilisateur(Email));

CREATE TABLE CaracteristiqueProduit(NomCaracteristique varchar(100) NOT NULL CONSTRAINT KCaracteristique primary key,
            IdProduit int NOT NULL CONSTRAINT KFIdProduitCaracteristique REFERENCES Produit(idProduit) ON DELETE CASCADE,
            Valeur varchar(100) NOT NULL);


CREATE TABLE Vente(IdVente int NOT NULL CONSTRAINT KVente primary key,
                    PrixDepart float NOT NULL CONSTRAINT PrixDepartPositif CHECK(PrixDepart>0.0),
                    Revocable int NOT NULL CONSTRAINT RevocableTrueFalse CHECK(Revocable in (0,1)),
                    Montante int NOT NULL CONSTRAINT MontanteTrueFalse CHECK(Montante in (0,1)),
                    OffreMultiple int NOT NULL CONSTRAINT OffreMultipleTrueFalse CHECK(OffreMultiple in (0,1)),
                    idProduit int NOT NULL,
                    Quantite int NOT NULL CONSTRAINT QuantitePositive CHECK(Quantite >= 0),
                    CONSTRAINT FKIdProduit FOREIGN KEY(idProduit) REFERENCES Produit(idProduit),
                    IdSalle int NOT NULL,
                    CONSTRAINT FKIdSalle FOREIGN KEY(IdSalle) REFERENCES SalleDeVente(IdSalle),
                    DateHeureVente TIMESTAMP NOT NULL);

CREATE TABLE VenteDureeLimitee(IdVente int REFERENCES Vente CONSTRAINT KVenteDL primary key,
                                DateHeureFin TIMESTAMP NOT NULL
                                );

CREATE TABLE DateHeureOffre(DateHeure TIMESTAMP NOT NULL CONSTRAINT KDate PRIMARY KEY);

CREATE TABLE VenteDureeIllimitee (
    IdVente INT,
    Delai INT NOT NULL,
    CONSTRAINT K_VenteDureeIllimitee PRIMARY KEY (IdVente),
    CONSTRAINT F_IdVente_VenteDureeIllimitee FOREIGN KEY (IdVente) REFERENCES Vente(IdVente),
    CONSTRAINT chk_delai CHECK (Delai<=10)
);

CREATE TABLE Propose (
    NomCategorie VARCHAR(100) NOT NULL,
    IdSalle INT NOT NULL,
    CONSTRAINT K_Propose PRIMARY KEY (NomCategorie, IdSalle),
    CONSTRAINT F_IdSalle_Propose FOREIGN KEY (IdSalle) REFERENCES SalleDeVente(IdSalle),
    CONSTRAINT F_NomCategorie_Propose FOREIGN KEY (NomCategorie) REFERENCES CategorieProduit(NomCategorie)
);

CREATE TABLE Offre (
    IdVente INT NOT NULL,
    Email VARCHAR(100) NOT NULL,
    PrixAchat float NOT NULL,
    QuantiteProduit INT NOT NULL,
    DateHeure TIMESTAMP NOT NULL,
    CONSTRAINT K_Offre PRIMARY KEY (IdVente, Email, DateHeure),
    CONSTRAINT F_IdVente_Offre FOREIGN KEY (IdVente) REFERENCES Vente(IdVente),
    CONSTRAINT F_Email_Offre FOREIGN KEY (Email) REFERENCES Utilisateur(Email),
    CONSTRAINT KFDateHeure FOREIGN KEY (DateHeure) REFERENCES DateHeureOffre(DateHeure),
    CONSTRAINT chk_quantiteproduit CHECK (QuantiteProduit>0)
);