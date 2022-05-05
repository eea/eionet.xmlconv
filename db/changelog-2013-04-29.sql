-- MySQL dump 10.13  Distrib 5.5.30, for Linux (x86_64)
--
-- Host: localhost    Database: converters
-- ------------------------------------------------------
-- Server version	5.5.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `databasechangelog`
--

DROP TABLE IF EXISTS `databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangelog` (
  `ID` varchar(63) NOT NULL,
  `AUTHOR` varchar(63) NOT NULL,
  `FILENAME` varchar(200) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`,`AUTHOR`,`FILENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangelog`
--

LOCK TABLES `databasechangelog` WRITE;
/*!40000 ALTER TABLE `databasechangelog` DISABLE KEYS */;
INSERT INTO `databasechangelog` VALUES ('rev-1','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',1,'EXECUTED','3:89e4d38dcb134386ebb03750c545078e','Create Table','',NULL,'2.0.5'),('rev-10','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',10,'EXECUTED','3:3610b74c589d3c188e6ad672e17b2841','Create Table','',NULL,'2.0.5'),('rev-11','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',11,'EXECUTED','3:54a10b8a2e3575e28c8bf8f57b920bce','Create Table','',NULL,'2.0.5'),('rev-12','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',12,'EXECUTED','3:690d79689c1e889de17038ab6873cb49','Create Index','',NULL,'2.0.5'),('rev-13','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',13,'EXECUTED','3:beab1d5f3136203424c348b1f7104421','Create Index','',NULL,'2.0.5'),('rev-14','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',14,'EXECUTED','3:8d1456f7a0c25cb24a29ae2b0a1dbb42','Create Index','',NULL,'2.0.5'),('rev-15','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',15,'EXECUTED','3:8911a87cc9c5c21e9882e162beb373a1','Create Index','',NULL,'2.0.5'),('rev-16','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',16,'EXECUTED','3:b07e80890230ec60a31800d669798d32','Create Index','',NULL,'2.0.5'),('rev-17','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',17,'EXECUTED','3:b14949443acb47739a6c6766a5a72a63','Create Index','',NULL,'2.0.5'),('rev-18','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',18,'EXECUTED','3:5e9ddc4195a8f5da9eaf7643f1aca130','Create Index','',NULL,'2.0.5'),('rev-19','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',19,'EXECUTED','3:28e8eec3c989d75b0c8723632b1ebc90','Create Index','',NULL,'2.0.5'),('rev-2','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',2,'EXECUTED','3:b656eb7183b174591d539fae06bf4884','Create Table','',NULL,'2.0.5'),('rev-20','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',20,'EXECUTED','3:cd36afafdf6fccfb357d4c35de4642d3','Custom SQL','',NULL,'2.0.5'),('rev-3','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',3,'EXECUTED','3:5b9610db3595876766e90c04624ca855','Create Table','',NULL,'2.0.5'),('rev-4','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',4,'EXECUTED','3:e60a59913b96edfbb42c0bf0ad6695eb','Create Table','',NULL,'2.0.5'),('rev-5','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',5,'EXECUTED','3:6debd40a6abca78d05fc01e3ae2fa847','Create Table','',NULL,'2.0.5'),('rev-6','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',6,'EXECUTED','3:694cb8be268f03434d9f708520babd46','Create Table','',NULL,'2.0.5'),('rev-7','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',7,'EXECUTED','3:a13f4596376a9a11abbdfd82ee875195','Create Table','',NULL,'2.0.5'),('rev-8','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',8,'EXECUTED','3:ad9e5732c69366393602a2b49ca82db4','Create Table','',NULL,'2.0.5'),('rev-9','kasperen','liquibase/xmlconv-struct.xml','2013-04-29 10:50:47',9,'EXECUTED','3:8206e89689f0d7912242147c552547e0','Create Table','',NULL,'2.0.5');
/*!40000 ALTER TABLE `databasechangelog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `databasechangeloglock`
--

DROP TABLE IF EXISTS `databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` tinyint(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangeloglock`
--

LOCK TABLES `databasechangeloglock` WRITE;
/*!40000 ALTER TABLE `databasechangeloglock` DISABLE KEYS */;
INSERT INTO `databasechangeloglock` VALUES (1,0,NULL,NULL);
/*!40000 ALTER TABLE `databasechangeloglock` ENABLE KEYS */;
UNLOCK TABLES;


/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-03-29  9:52:41
