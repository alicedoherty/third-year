/*
	Code for creating database and tables (including constraints)
*/

DROP DATABASE IF EXISTS academy_awards;
CREATE DATABASE academy_awards;
USE academy_awards;

SET NAMES utf8;
SET character_set_client = utf8mb4;

CREATE TABLE academy_award_show (
	ceremony_year YEAR NOT NULL,
    venue VARCHAR(255) NOT NULL,
    broadcaster VARCHAR(255) NOT NULL,
    show_host VARCHAR(255),
    PRIMARY KEY (ceremony_year)
);

CREATE TABLE performer (
	performer_id INTEGER NOT NULL AUTO_INCREMENT,
	performer_name VARCHAR(255) NOT NULL,
	pay INTEGER NOT NULL,
    ceremony_year YEAR NOT NULL,
	PRIMARY KEY (performer_id),
    FOREIGN KEY (ceremony_year) REFERENCES academy_award_show(ceremony_year),
    CHECK (pay > 0)
);

CREATE TABLE song (
	performer_id INTEGER NOT NULL,
    song_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (performer_id, song_name),
    FOREIGN KEY (performer_id) REFERENCES performer(performer_id)
);

CREATE TABLE presenter (
	presenter_id INTEGER NOT NULL AUTO_INCREMENT,
    presenter_name VARCHAR(255) NOT NULL,
    pay INTEGER NOT NULL,
    award_category VARCHAR(255) NOT NULL,
    ceremony_year YEAR NOT NULL,
    PRIMARY KEY (presenter_id),
    FOREIGN KEY (ceremony_year) REFERENCES academy_award_show(ceremony_year),
    CHECK (pay > 0)
);

CREATE TABLE voter (
	voter_id INTEGER NOT NULL AUTO_INCREMENT,
    voter_name VARCHAR(255) NOT NULL,
    discipline VARCHAR(255) NOT NULL,
    gender VARCHAR(255),
    race VARCHAR(255),
    ceremony_year YEAR NOT NULL,
    PRIMARY KEY (voter_id),
    FOREIGN KEY (ceremony_year) REFERENCES academy_award_show(ceremony_year)
);

CREATE TABLE nominee (
	nominee_id INTEGER NOT NULL AUTO_INCREMENT,
    nominee_name VARCHAR(255) NOT NULL,
    profession VARCHAR(255),
    PRIMARY KEY (nominee_id)
);

CREATE TABLE vote (
	voter_id INTEGER NOT NULL,
    nominee_id INTEGER NOT NULL,
    category VARCHAR(255) NOT NULL,
    PRIMARY KEY (voter_id, nominee_id, category),
    FOREIGN KEY (voter_id) REFERENCES voter(voter_id),
    FOREIGN KEY (nominee_id) REFERENCES nominee(nominee_id)
);

CREATE TABLE film (
	film_id INTEGER NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    release_year YEAR,
    director VARCHAR(255),
    PRIMARY KEY (film_id)
);

CREATE TABLE genre (
	film_id INTEGER NOT NULL,
    genre VARCHAR(255) NOT NULL,
    PRIMARY KEY (film_id, genre),
    FOREIGN KEY (film_id) REFERENCES film(film_id)
);

CREATE TABLE nomination (
    nominee_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    category VARCHAR(255) NOT NULL,
    nomination_year YEAR NOT NULL,
    winner BOOL,
    PRIMARY KEY (nominee_id , film_id , category),
    FOREIGN KEY (nominee_id)
        REFERENCES nominee(nominee_id),
    FOREIGN KEY (film_id)
        REFERENCES film(film_id),
	FOREIGN KEY (nomination_year) 
		REFERENCES academy_award_show(ceremony_year)
);

/*
	Code for altering tables
*/

-- Add more constraints to the salaries (i.e an upper bound)
ALTER TABLE performer
ADD CONSTRAINT performer_pay_check CHECK (pay > 0 AND pay < 1000000);

ALTER TABLE presenter
ADD CONSTRAINT presenter_pay_check CHECK (pay > 0 AND pay < 1000000);

-- e.g If you decide you also want to store the producer of the film
ALTER TABLE film
ADD producer VARCHAR(255);

-- Dropping it, so it the tables/data align with ER model
ALTER TABLE film
DROP COLUMN producer;

/*
	Code for trigger operations
*/

-- An actor can't be nominated more than once in the SAME category
-- e.g actor can't be nominated twice for Best Actor for Film X and Film Y
DELIMITER $$

CREATE TRIGGER category_check
BEFORE INSERT
	ON nomination FOR EACH ROW
	BEGIN
		IF EXISTS (SELECT * FROM nomination WHERE (nominee_id = NEW.nominee_id) 
			AND (category = NEW.category) 
            AND (nomination_year = NEW.nomination_year)) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'The same nominee cannot be nominated more than once in the same category.';
		END IF;
	END $$
    
-- An actor can't be nominated more than once (across different categories) for the SAME performance
-- e.g an actor can't be nominated for Best Actor and Best Supporting Actor for their performance in Film X
CREATE TRIGGER film_check
BEFORE INSERT
	ON nomination FOR EACH ROW
	BEGIN
		IF EXISTS (SELECT * FROM nomination WHERE (nominee_id = NEW.nominee_id) AND (film_id = NEW.film_id)) THEN
			SIGNAL SQLSTATE '45000'
				SET MESSAGE_TEXT = 'The same nominee cannot be nominated more than once (across different categories) for the same performance/film.';
		END IF;
	END $$

DELIMITER ;

/*
	Code for creation of views
*/

-- Displays all nominees, and corresponding film, for Best Actor 2021
CREATE VIEW best_actor_nominees (nominee, film, winner) AS
SELECT nominee.nominee_name, film.title, nomination.winner
FROM nominee, film, nomination
WHERE (nominee.nominee_id = nomination.nominee_id)
	AND (film.film_id = nomination.film_id)
    AND (nomination.category = "Best Actor")
    AND (nomination.nomination_year = "2021");

-- Displays all nominees, and corresponding film, for Best Actress 2021
CREATE VIEW best_actress_nominees (nominee, film, winner) AS
SELECT nominee.nominee_name, film.title, nomination.winner
FROM nominee, film, nomination
WHERE (nominee.nominee_id = nomination.nominee_id)
	AND (film.film_id = nomination.film_id)
    AND (nomination.category = "Best Actress")
    AND (nomination.nomination_year = "2021");

-- Displays all nominees, and corresponding film, for Best Director 2021
CREATE VIEW best_director_nominees (nominee, film, winner) AS
SELECT nominee.nominee_name, film.title, nomination.winner
FROM nominee, film, nomination
WHERE (nominee.nominee_id = nomination.nominee_id)
	AND (film.film_id = nomination.film_id)
    AND (nomination.category = "Best Director")
    AND (nomination.nomination_year = "2021");
    
-- Displays how much each performer is being paid for this year's Oscars (i.e the payroll)
CREATE VIEW performer_payroll (id, pay_amount) AS
SELECT performer.performer_id, performer.pay
FROM performer
WHERE (performer.ceremony_year = "2022");

-- Displays how much each presenter is being paid or this year's Oscars (i.e the payroll)
CREATE VIEW presenter_payroll (id, pay_amount) AS
SELECT presenter.presenter_id, presenter.pay
FROM presenter
WHERE (presenter.ceremony_year = "2022");

/*
	Code for populating tables
*/

INSERT INTO academy_award_show (ceremony_year, venue, broadcaster, show_host)
VALUES 
	(2022, "Dolby Theatre", "ABC", "Amy Schumer"),
	(2021, "Union Station", "ABC", null),
    (2020, "Dolby Theatre", "ABC", null),
    (2019, "Dolby Theatre", "ABC", null),
    (2018, "Dolby Theatre", "ABC", "Jimmy Kimmel"),
    (2017, "Dolby Theatre", "ABC", "Jimmy Kimmel"),
    (2016, "Dolby Theatre", "ABC", "Chris Rock");

-- Note: No values for performer_id are inserted because it is an auto-increment field
INSERT INTO performer (performer_name, pay, ceremony_year)
VALUES
	("Taylor Swift", "11111", 2022),
    ("Billie Eilish", "10000", 2022),
	("Beyonce", "12345", 2021),
    ("Harry Styles", "10000", 2021),
    ("Elton John", "23000", 2020),
    ("Eminem", "12900", 2020),
    ("Lady Gaga", "23000", 2019),
    ("Bradley Cooper", "23000", 2019),
    ("Sufjan Stevens", "12700", 2018),
    ("Van Morrison", "15700", 2018);
    
INSERT INTO song (performer_id, song_name)
VALUES
	(1, "All Too Well (10 Minute Version)"),
    (1, "Wildest Dreams"),
    (2, "Happier Than Ever"),
	(3, "Halo"),
    (4, "Golden"),
    (5, "Your Song"),
    (6, "Lose Yourself"),
    (7, "Shallow"),
    (8, "Shallow"),
    (9, "Mystery of Love"),
    (10, "Brown Eyed Girl");
    
-- Note: No values for presenter_id are inserted because it is an auto-increment field
INSERT INTO presenter (presenter_name, pay, award_category, ceremony_year)
VALUES
	("Matt Damon", 10000, "Best Actor", 2022),
    ("Jessica Laing", 14000, "Best Actress", 2022),
	("Angela Bassett", "12000", "Best Director", 2022),
    ("Zendaya", "12345", "Best Director", 2022),
	("Reese Witherspoon", "12200", "Best Actor", 2021),
    ("Brad Pitt", "28900", "Best Actress", 2021),
    ("Joaquin Phoenix", "13000", "Best Director", 2021),
    ("Harrison Ford", "13400", "Best Director", 2021);
    
-- Note: No values for voter_id are inserted because it is an auto-increment field
INSERT INTO voter (voter_name, discipline, gender, race, ceremony_year)
VALUES
	("Lisa Prieto", "Production", "Female", "White", 2022),
    ("William Mechanic", "Production", "Male", "Black", 2022),
    ("Dan Peters", "Production", "Male", "Asian", 2022),
    ("Nathan Smith", "Acting", "Male", "White", 2022),
    ("Lucy Jacobs", "Acting", "Female", "White", 2022),
    ("Larry Karasweksi", "Acting", "Male", "White", 2021),
    ("Robert Shapiro", "Acting", "Male", "White", 2021),
    ("Josh James", "Sound", "Male", "Black", 2021),
    ("Kelly Klack", "Sound", "Female", "White", 2021);
    
-- Note: No values for nominee_id are inserted because it is an auto-increment field
INSERT INTO nominee (nominee_name, profession)
VALUES
	("Javier Bardem", "Actor"),
    ("Benedict Cumberbatch", "Actor"),
    ("Andrew Garfield", "Actor"),
    ("Olivia Colman", "Actor"),
    ("Nicole Kidman", "Actor"),
    ("Kristen Steward", "Actor"),
    ("Steven Spielberg", "Director"),
    ("Jane Campion", "Director"),
    ("Kenneth Branagh", "Director"),
    ("Anthony Hopkins", "Actor"),
    ("Gary Oldman", "Actor"),
    ("Chadwick Boseman", "Actor"),
    ("Frances McDormand", "Actor"),
    ("Viola David", "Actor"),
    ("Carey Mulligan", "Actor"),
    ("Chloe Zhao", "Director"),
    ("Emerald Fennell", "Director"),
    ("David Fincher", "Director");
    
INSERT INTO vote (voter_id, nominee_id, category)
VALUES
	-- Votes for 2022
	(1, 1, "Best Actor"),
    (1, 4, "Best Actress"),
    (1, 7, "Best Director"),
    (2, 2, "Best Actor"),
    (2, 4, "Best Actress"),
    (2, 8, "Best Director"),
    (3, 3, "Best Actor"),
    (3, 4, "Best Actress"),
    (3, 7, "Best Director"),
    
    -- Votes for 2021
    (4, 10, "Best Actor"),
    (4, 13, "Best Actress"),
    (4, 16, "Best Director"),
    (5, 11, "Best Actor"),
    (5, 13, "Best Actress"),
    (5, 17, "Best Director"),
    (6, 12, "Best Actor"),
    (6, 13, "Best Actress"),
    (6, 16, "Best Director");
    
INSERT INTO film (title, release_year, director)
VALUES
	("Being the Ricardos", 2021, "Aaron Sorkin"),
    ("The Power of the Dog", 2021, "Jane Campion"),
    ("tick, tick… BOOM!", 2021, "Lin-Manuel Miranda"),
    ("The Lost Daughter", 2021, "Maggie Gyllenhaal"),
    ("Spencer", 2021, "Pablo Larrain"),
	("West Side Story", 2021, "Steven Spielberg"),
    ("Belfast", 2021, "Kenneth Branagh"),
    
    ("The Father", 2020, "Fiorian Zeller"),
    ("Mank", 2020, "David Fincher"),
    ("Ma Rainey's Black Bottom", 2020, "George C. Wolfe"),
    ("Nomadland", 2020, "Chloe Zhao"),
    ("Promising Young Woman", 2020, "Emerald Fennell");
    
INSERT INTO genre (film_id, genre)
VALUES
	(1, "Biographical"),
    (1, "Drama"),
    (2, "Western"),
    (3, "Musical"),
    (4, "Thriller"),
    (5, "Biographical"),
	(6, "Musical"),
    (6, "Romance"),
    (7, "Historical"),
    (8, "Drama"),
    (9, "Biographical"),
    (10, "Drama"),
    (11, "Drama"),
    (12, "Thriller");

INSERT INTO nomination (nominee_id, film_id, category, nomination_year, winner)
VALUES
	(1, 1, "Best Actor", 2022, null),
    (2, 2, "Best Actor", 2022, null),
    (3, 3, "Best Actor", 2022, null),
    (4, 4, "Best Actress", 2022, null),
    (5, 1, "Best Actress", 2022, null),
    (6, 5, "Best Actress", 2022, null),
    (7, 6, "Best Director", 2022, null),
    (8, 2, "Best Director", 2022, null),
	(9, 7, "Best Director", 2022, null),
    
    (10, 8, "Best Actor", 2021, 1),
    (11, 9, "Best Actor", 2021, 0),
    (12, 10, "Best Actor", 2021, 0),
    (13, 11, "Best Actress", 2021, 0),
    (14, 10, "Best Actress", 2021, 1),
    (15, 12, "Best Actress", 2021, 0),
    (4, 8, "Best Supporting Actress", 2021, 0),
    (16, 11, "Best Director", 2021, 0),
    (17, 12, "Best Director", 2021, 1),
	(18, 9, "Best Director", 2021, 0);

/*
	Code for retrieving information (including joins and functions)
*/

-- Retrieves all the nominations in the format: (NomineeName, FilmTitle, Category, Year)
-- See report for sample output
SELECT nomination.nomination_year, nomination.category, nominee.nominee_name, film.title, nomination.winner 
FROM nomination
INNER JOIN film 
	ON nomination.film_id = film.film_id
INNER JOIN nominee 
	ON nomination.nominee_id = nominee.nominee_id;
 
-- Retrieves all the nominations in the format for a specific year
SELECT nomination.category, nominee.nominee_name, film.title, nomination.winner 
FROM nomination
INNER JOIN film 
	ON nomination.film_id = film.film_id
INNER JOIN nominee 
	ON nomination.nominee_id = nominee.nominee_id
WHERE nomination.nomination_year = "2021";
    
-- All nominations for a particular actor
SELECT nomination.nomination_year, nomination.category, film.title, nomination.winner 
FROM nomination
INNER JOIN film 
	ON nomination.film_id = film.film_id
INNER JOIN nominee 
	ON nomination.nominee_id = nominee.nominee_id
WHERE nominee.nominee_name = "Olivia Colman";

-- All nominations for a particular film
SELECT nomination.category, nominee.nominee_name
FROM nomination
INNER JOIN film 
	ON nomination.film_id = film.film_id
INNER JOIN nominee 
	ON nomination.nominee_id = nominee.nominee_id
WHERE film.title="Ma Rainey's Black Bottom";

-- Number of nominations for each genre of film, sorted from most number of nominations to least
SELECT genre.genre, COUNT(*) AS count
FROM genre
GROUP BY genre
ORDER BY count DESC;

-- Breakdown of voters by gender
SELECT voter.discipline,
	SUM(CASE WHEN voter.gender = "Male" THEN 1 ELSE 0 END)/COUNT(*)*100 male_percentage, 
	SUM(CASE WHEN voter.gender = "Female" THEN 1 ELSE 0 END)/COUNT(*)*100 female_percentage
FROM voter
GROUP BY voter.discipline;

-- Breakdown of voters by race
SELECT voter.discipline,
	SUM(CASE WHEN voter.race = "White" THEN 1 ELSE 0 END)/COUNT(*)*100 white, 
	SUM(CASE WHEN voter.race = "Black" THEN 1 ELSE 0 END)/COUNT(*)*100 black,
    SUM(CASE WHEN voter.race = "Asian" THEN 1 ELSE 0 END)/COUNT(*)*100 asian
FROM voter
GROUP BY voter.discipline;

/*
	Code for security commands (roles and permissions)
*/

-- Create roles
DROP ROLE IF EXISTS 'awards_admin', 'awards_read', 'awards_write', 'public';
CREATE ROLE 'awards_admin', 'awards_read', 'awards_write', 'public';

-- Grant roles relevant privileges
GRANT ALL ON academy_awards.* TO 'awards_admin';
GRANT SELECT ON academy_awards.* TO 'awards_read';
GRANT INSERT, UPDATE, DELETE ON academy_awards.* TO 'awards_write';
GRANT SELECT ON academy_award_show TO 'public';
GRANT SELECT ON film TO 'public';
GRANT SELECT ON genre TO 'public';
GRANT SELECT ON nomination TO 'public';
GRANT SELECT ON nominee TO 'public';

-- Create sample users
DROP ROLE IF EXISTS 'jdoe'@'localhost', 'adoherty'@'localhost', 'mconnor'@'localhost', 'dsmith'@'localhost';
CREATE USER 'jdoe'@'localhost' IDENTIFIED BY 'password123';
CREATE USER 'adoherty'@'localhost' IDENTIFIED BY 'my_password';
CREATE USER 'mconnor'@'localhost' IDENTIFIED BY 'Password!';
CREATE USER 'dsmith'@'localhost' IDENTIFIED BY 'pASSWORD';

-- Assign users roles/privileges
GRANT 'awards_admin' TO 'jdoe'@'localhost';
GRANT 'awards_read' TO 'adoherty'@'localhost';
GRANT 'awards_read', 'awards_write' TO 'mconnor'@'localhost';
GRANT 'public' TO 'dsmith'@'localhost';

