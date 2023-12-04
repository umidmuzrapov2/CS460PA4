DROP TABLE groupIBMU.Enrollment;
DROP TABLE groupIBMU.EquipmentLoan;
DROP TABLE groupIBMU.Purchase;
DROP TABLE groupIBMU.CoursePackage;
DROP TABLE groupIBMU.MembershipLevel;
DROP TABLE groupIBMU.Equipment;
DROP TABLE groupIBMU.Transaction;
DROP TABLE groupIBMU.Member;
DROP TABLE groupIBMU.Schedule;
DROP TABLE groupIBMU.Course;
DROP TABLE groupIBMU.Trainer;
DROP TABLE groupIBMU.Package;

CREATE DOMAIN umidmuzrapov.PossiblePrice AS DECIMAL(6,2)
	CHECK (VALUE BETWEEN 0 AND 9999.99);
	
CREATE DOMAIN umidmuzrapov.Discount AS INTEGER
	CHECK (VALUE BETWEEN 0 AND 50);

CREATE DOMAIN umidmuzrapov.ParticipantNumber AS INTEGER
	CHECK (VALUE BETWEEN 0 and 60);

CREATE DOMAIN umidmuzrapov.Day AS INTEGER
	CHECK (VALUE BETWEEN 0 and 8);

CREATE DOMAIN umidmuzrapov.Hour AS INTEGER
	CHECK (VALUE BETWEEN -1 AND 25);

CREATE DOMAIN umidmuzrapov.MinuteSecond AS INTEGER
	CHECK (VALUE BETWEEN -1 AND 61);

CREATE DOMAIN umidmuzrapov.Duration AS INTEGER
	CHECK (VALUE BETWEEN -1 and 120);

-- Trainer Table
CREATE TABLE groupIBMU.Trainer (
    	trainerNumber INTEGER NOT NULL,
    	fname VARCHAR(20) NOT NULL,
    	lname VARCHAR(20) NOT NULL,
    	phoneNumber CHAR(10),
	PRIMARY KEY (trainerNumber)	
);

-- Package Table
CREATE TABLE groupIBMU.Package (
    	packageName VARCHAR(120) NOT NULL,
    	cost umidmuzrapov.PossiblePrice NOT NULL,
	PRIMARY KEY (packageName)
);

-- Membership Level Table
CREATE TABLE groupIBMU.MembershipLevel (
    	levelId INTEGER NOT NULL,
    	minSpending umidmuzrapov.PossiblePrice NOT NULL,
    	discount umidmuzrapov.Discount NOT NULL,
	PRIMARY KEY (levelId)
);

-- Member Table
CREATE TABLE groupIBMU.Member (
    	memberNumber INTEGER NOT NULL,
    	fname VARCHAR(20) NOT NULL,
    	lname VARCHAR(20) NOT NULL,
    	phoneNumber CHAR(10),
    	levelId INTEGER,
	PRIMARY KEY (memberNumber),
	FOREIGN KEY (levelId) REFERENCES groupIBMU.MembershipLevel
		ON DELETE SET NULL
		ON UPDATE CASCADE
);

-- Course Table
CREATE TABLE groupIBMU.Course (
	className VARCHAR(120) NOT NULL,
	maxParticipant umidmuzrapov.ParticipantNumber,
    	currentParticipant INTEGER 
			CONSTRAINT CurrentParticipant
			CHECK (VALUE BETWEEN 0 AND maxParticipant),
    	startDate DATE NOT NULL,
    	endDate DATE NOT NULL,
    	trainerNumber INTEGER NOT NULL,
    	PRIMARY KEY (className, startDate),
	FOREIGN KEY (trainerNumber) REFERENCES groupIBMU.Trainer
					ON DELETE NO ACTION
					ON UPDATE CASCADE
 
);


-- Schedule Table
CREATE TABLE groupIBMU.Schedule (
	className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	day VARCHAR(10) NOT NULL,
    	hour umidmuzrapov.Hour NOT NULL,
    	minute umidmuzrapov.MinuteSecond NOT NULL,
    	duration umidmuzrapov.Duration NOT NULL,
    	PRIMARY KEY (className, startDate, day),
	FOREIGN KEY (className) REFERENCES groupIBMU.Course
					ON UPDATE CASCADE,

	FOREIGN KEY (startDate) REFERENCES groupIBMU.Course
					ON UPDATE CASCADE

);

-- Equipment Table
CREATE TABLE groupIBMU.Equipment (
	itemNumber INTEGER NOT NULL,
    	description VARCHAR(120),
    	quantity INTEGER NOT NULL,
	PRIMARY KEY (itemNumber)
);

-- CoursePackage Table
CREATE TABLE groupIBMU.CoursePackage (
    	packageName VARCHAR(120) NOT NULL,
   	className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	PRIMARY KEY (packageName, className, startDate),
	FOREIGN KEY (packageName) REFERENCES groupIBMU.Package
	    				ON UDPATE CASCADE
					ON DELETE NO ACTION
	FOREIGN KEY (className) REFERENCES groupIBMU.Course
					ON UPDATE CASCADE
					ON DELETE NO ACTION
	FOREIGN KEY (startDate) REFERENCES groupIBMU.Course
					ON UPDATE CASCADE
					ON DELETE NO ACTION
	
);


-- Transaction Table
CREATE TABLE groupIBMU.Transaction (
    	transactionNumber INTEGER NOT NULL,
    	total PossiblePrice NOT NULL,
    	transactionDate DATE NOT NULL,
    	type VARCHAR(50),
    	memberNumber INTEGER NOT NULL,
	PRIMARY KEY (transactionNumber),
	FOREIGN KEY (memberNumber) REFERENCES groupIBMU.Course
					ON UDPATE CASCADE
					ON DELETE ON ACTION
);

-- Purchase Table
CREATE TABLE groupIBMU.Purchase (
    	packageName VARCHAR(255) NOT NULL,
    	transactionNumber INTEGER NOT NULL,
    	PRIMARY KEY (packageName, transactionNumber),
	FOREIGN KEY (packageName) REFERENCES groupIBMU.Package
	    				ON UDPATE CASCADE
					ON DELETE NO ACTIOM,
	FOREIGN KEY (transactionNumber) REFERENCES groupIBMU.Transaction
					ON UPDATE CASCADE
					ON DELETE NO ACTION
);

-- EquipmentLoan Table
CREATE TABLE groupIBMU.EquipmentLoan (
    	loanNumber INTEGER NOT NULL,
    	itemNumber INTEGER NOT NULL,
    	memberItem INTEGER NOT NULL,
    	quantity INTEGER CONTRAINT CHECK (VALUE >= 0),
    	borrowedDate DATE,
    	returnedDate DATE,
	PRIMARY KEY (loanNumber),
	FOREIGN KEY (item) REFERENCES groupIBMU.Equipment
				ON UPDATE CASCADE
				ON DELETE NO ACTION
	FOREIGN KEY (memberItem) REFERENCES groupIBMU.Member
				ON UPDATE CASCADE
				ON DELETE NO ACTION
);

-- Enrollment Table
CREATE TABLE groupIBMU.Enrollment (
    	memberNumber INTEGER NOT NULL,
    	courseName VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
     	PRIMARY KEY (memberNumber, courseName, startDate),
     	FOREIGN KEY (memberNumber) REFERENCES groupIBMU.Member
				ON UPDATE CASCADE
				ON DELETE NO ACTION,
	FOREIGN KEY (memberNumebr) REFERENCES groupIBMU.Course
				ON UPDATE CASCADE
);

-- Grant permissions to group members and TAs
GRANT ALL PRIVILEGES ON groupIBMU.Package TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Trainer TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Course TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Schedule TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Member TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Transaction TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Equipment TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.MembershipLevel TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.CoursePackage TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Purchase TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.EquipmentLoan TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON groupIBMU.Enrollment TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
