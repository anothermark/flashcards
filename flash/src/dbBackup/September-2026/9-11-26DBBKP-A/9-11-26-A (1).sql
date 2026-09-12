CREATE DATABASE  IF NOT EXISTS `restapiflashcards` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restapiflashcards`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: restapiflashcards
-- ------------------------------------------------------
-- Server version	9.7.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '792fde7d-9a5a-11f1-bff8-60ff9ed9d6c5:1-156';

--
-- Table structure for table `flashcards`
--

DROP TABLE IF EXISTS `flashcards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flashcards` (
  `id` bigint NOT NULL,
  `questionsandanswers` json DEFAULT NULL,
  `studysubject` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flashcards`
--

LOCK TABLES `flashcards` WRITE;
/*!40000 ALTER TABLE `flashcards` DISABLE KEYS */;
INSERT INTO `flashcards` VALUES (1,'[{\"id\": 1, \"answer\": \"RRR now is the time\", \"question\": \"www\"}, {\"id\": 2, \"answer\": \"mark\", \"question\": \"hellow mellow\"}, {\"id\": 3, \"answer\": \"Everything that begins must have a cause. And because logically all of the parts that comprise the universe had a beginning and are therefore caused, then the universe, made up of these contingent parts, must as a whole be contingent as well, with a beginning and a beginner, or a cause (God).  \\r\\n\\r\\nIn a nutshell, the powerful Kalam horizontal argument states that 1) everything that has a beginning has a cause, 2) the universe had a beginning and therefore, 3) the universe had a cause (Geisler 289). Because the universe could not cause itself (a logical impossibility) something beyond itself caused it to exist. And that cause could not itself be caused by something else but must be uncaused, eternal and self-existent. Otherwise, if that cause was also caused we would be trapped in that infinite regress and would not exist, with each cause waiting for the prior cause. \\r\\n\\r\\nStated another way, because all material existence (the universe) had a beginning it must have had a cause and that cause must be a self-existent, uncaused creator – YHWH. The theory argues from “creation to creator and from effect to cause.” (Geisler 98 – 100, 287 - 291). \", \"question\": \"Which best defines the Kalam horizontal cosmological argument?\"}]','Math'),(2,'[]','History'),(3,'[]','Science'),(4,'[]','Geography'),(5,'[{\"id\": 1, \"answer\": \"No, but micro evolution obviously is.\", \"question\": \"Is macro evolution valid science?\"}, {\"id\": 2, \"answer\": \"No, that\'s a supposed biology thing.\", \"question\": \"did the universe evolve all on its own?\"}]','Evolution'),(6,'[{\"id\": 1, \"answer\": \"It has something to do with dna and such.\", \"question\": \"What is micro-biology?\"}]','Micro-Biology'),(7,NULL,NULL),(8,'[]','Astrophysics');
/*!40000 ALTER TABLE `flashcards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `questionsandanswers` json DEFAULT NULL,
  `subject_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (1,'[{\"answer\": \"10\", \"question\": \"What is 5 + 5?\"}, {\"answer\": \"4\", \"question\": \"What is the square root of 16?\"}]','Mathematics'),(2,'[{\"answer\": \"10\", \"question\": \"What is 5 + 5?\"}, {\"answer\": \"4\", \"question\": \"What is the square root of 16?\"}]','Mathematics');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-11 14:55:51
