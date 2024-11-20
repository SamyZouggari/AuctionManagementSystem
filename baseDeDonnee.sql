CREATE TABLE CategorieProduit(NomCategorie varchar(30) NOT NULL CONSTRAINT KCategorieProduit primary key,
                            Description varchar(30) NOT NULL);



CREATE TABLE Utilisateur(Email varchar(30) NOT NULL CONSTRAINT Kutilisateur primary key,
                        NomUser varchar(30) NOT NULL,
                        PrenomUser varchar(30) NOT NULL,
                        AddrUser varchar(30) NOT NULL);

CREATE TABLE CaracteristiqueProduit(NomCaracteristique varchar(30) NOT NULL CONSTRAINT KCaracteristique primary key,
            Valeur varchar(30) NOT NULL,
            NomCategorie varchar(30) NOT NULL,
            CONSTRAINT KFNomCategorieCaracteristique FOREIGN KEY(NomCategorie) REFERENCES CategorieProduit(NomCategorie) ON DELETE CASCADE);
            


CREATE TABLE Produit(idProduit int NOT NULL CONSTRAINT Kproduit primary key,
                NomProduit varchar(30) NOT NULL,
                PrixDeRevient float NOT NULL CONSTRAINT PrixDeRevientPositif CHECK(PrixDeRevient>0),
                Stock int NOT NULL CONSTRAINT StockPositif CHECK(Stock >0),
                NomCategorie varchar(30) NOT NULL,
                Email varchar(30) NOT NULL,
                CONSTRAINT KFNomCategorieProduit FOREIGN KEY(NomCategorie) REFERENCES CategorieProduit(NomCategorie) ON DELETE CASCADE,
                CONSTRAINT KFemailProduit FOREIGN KEY(Email) REFERENCES Utilisateur(Email) ON DELETE CASCADE);


CREATE TABLE CategorieProduit(NomCategorie varchar(30) NOT NULL CONSTRAINT Kcategorie primary key,
                                Description varchar(30) NOT NULL);

CREATE TABLE SalleDeVente(IdSalle int NOT NULL CONSTRAINT KSalle primary key);

CREATE TABLE Vente(IdVente int NOT NULL CONSTRAINT KVente primary key,
                    PrixDepart float NOT NULL CONSTRAINT PrixDepartPositif CHECK(PrixDepart>0.0),
                    DureeLimitee int NOT NULL CONSTRAINT DureeLimTrueFalse CHECK(DureeLimitee in (0,1)),
                    Revocable int NOT NULL CONSTRAINT RevocableTrueFalse CHECK(Revocable in (0,1)),
                    Montante int NOT NULL CONSTRAINT MontanteTrueFalse CHECK(Montante in (0,1)),
                    OffreMultiple int NOT NULL CONSTRAINT OffreMultipleTrueFalse CHECK(OffreMultiple in (0,1)),
                    IdProduit int NOT NULL CONSTRAINT FKIdProduit FOREIGN KEY REFERENCES Produit(IdProduit),
                    IdSalle int NOT NULL CONSTRAINT FKIdSalle FOREIGN KEY REFERENCES SalleDeVente(IdSalle));

CREATE TABLE VenteDureeLimitee(IdVente int REFERENCES Vente,
                                DateFin DATE NOT NULL,
                                HeureFin TIME NOT NULL);

CREATE TABLE VenteDureeIllimitee (
    IdVente INT,
    Delai INT NOT NULL,
    CONSTRAINT K_VenteDureeIllimitee PRIMARY KEY (IdVente),
    CONSTRAINT F_IdVente_VenteDureeIllimitee FOREIGN KEY (IdVente) REFERENCES Vente(IdVente), 
    CONSTRAINT chk_delai CHECK (Delai<=10)
);

CREATE TABLE Propose (
    NomCategorie VARCHAR(25) NOT NULL,
    IdSalle INT NOT NULL,
    CONSTRAINT K_Propose PRIMARY KEY (IdSalle),
    CONSTRAINT F_IdSalle_Propose FOREIGN KEY (IdSalle) REFERENCES Salle(IdSalle),
    CONSTRAINT F_NomCategorie_Propose FOREIGN KEY (NomCategorie) REFERENCES CategorieProduit(NomCategorie)
);

CREATE TABLE CaracterisePar (
    IdProduit INT NOT NULL,
    NomCaracteristique VARCHAR(25) NOT NULL,
    CONSTRAINT K_CaracterisePar PRIMARY KEY (IdProduit, NomCaracteristique),
    CONSTRAINT F_IdProduit_CaracterisePar FOREIGN KEY (IdProduit) REFERENCES Produit(IdProduit),
    CONSTRAINT F_NomCaracteristique_CaracterisePar FOREIGN KEY (NomCaracteristique) REFERENCES CaracteristiqueProduit(NomCaracteristique)
);

CREATE TABLE Offre (
    IdVente INT NOT NULL,
    Email VARCHAR(100) NOT NULL,
    PrixAchat DECIMAL(10000000, 2) NOT NULL, 
    DateOffre DATE NOT NULL,
    HeureOffre TIME NOT NULL,
    QuantiteProduit INT NOT NULL, 
    CONSTRAINT K_Offre PRIMARY KEY (IdVente, Email),
    CONSTRAINT F_IdVente_Offre FOREIGN KEY (IdVente) REFERENCES Vente(IdVente),
    CONSTRAINT F_Email_Offre FOREIGN KEY (Email) REFERENCES Utilisateur(Email),
    CONSTRAINT chk_quantiteproduit CHECK (QuantiteProduit>=0)
);


