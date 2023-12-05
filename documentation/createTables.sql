DROP TABLE umidmuzrapov.Enrollment;
DROP TABLE umidmuzrapov.EquipmentLoan;
DROP TABLE umidmuzrapov.Purchase;
DROP TABLE umidmuzrapov.CoursePackage;
DROP TABLE umidmuzrapov.MembershipLevel;
DROP TABLE umidmuzrapov.Equipment;
DROP TABLE umidmuzrapov.Transaction;
DROP TABLE umidmuzrapov.Member;
DROP TABLE umidmuzrapov.Schedule;
DROP TABLE umidmuzrapov.Course;
DROP TABLE umidmuzrapov.Trainer;
DROP TABLE umidmuzrapov.Package;

-- Trainer Table
CREATE TABLE umidmuzrapov.Trainer (
    	trainerNumber INTEGER NOT NULL,
    	fname VARCHAR(20) NOT NULL,
    	lname VARCHAR(20) NOT NULL,
    	phoneNumber CHAR(10),
	PRIMARY KEY (trainerNumber)	
);

-- Package Table
CREATE TABLE umidmuzrapov.Package (
    	packageName VARCHAR(120) NOT NULL,
    	cost DECIMAL(6,2) CHECK (VALUE BETWEEN 0 AND 9999.99),
	PRIMARY KEY (packageName)
);

-- Membership Level Table
CREATE TABLE umidmuzrapov.MembershipLevel (
    	levelId INTEGER NOT NULL,
    	minSpending DECIMAL(6,2) CHECK (VALUE BETWEEN 0 AND 9999.99) NOT NULL,
    	discount INTEGER CHECK (VALUE BETWEEN 0 AND 50) NOT NULL,
	PRIMARY KEY (levelId)
);

-- Member Table
CREATE TABLE umidmuzrapov.Member (
    	memberNumber INTEGER NOT NULL,
    	fname VARCHAR(20) NOT NULL,
    	lname VARCHAR(20) NOT NULL,
    	phoneNumber CHAR(10),
    	levelId INTEGER,
	PRIMARY KEY (memberNumber),
	FOREIGN KEY (levelId) REFERENCES umidmuzrapov.MembershipLevel
		ON DELETE SET NULL
		ON UPDATE CASCADE
);

-- Course Table
CREATE TABLE umidmuzrapov.Course (
	className VARCHAR(120) NOT NULL,
	maxParticipant INTEGER CHECK (VALUE BETWEEN 0 and 60),
    	currentParticipant INTEGER 
			CONSTRAINT CurrentParticipant
			CHECK (VALUE BETWEEN 0 AND maxParticipant),
    	startDate DATE NOT NULL,
    	endDate DATE NOT NULL,
    	trainerNumber INTEGER NOT NULL,
    	PRIMARY KEY (className, startDate),
	FOREIGN KEY (trainerNumber) REFERENCES umidmuzrapov.Trainer
					ON DELETE NO ACTION
					ON UPDATE CASCADE
 
);


-- Schedule Table
CREATE TABLE umidmuzrapov.Schedule (
	className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	day INTEGER CHECK (VALUE BETWEEN 0 and 8) NOT NULL,
    	hour INTEGER CHECK (VALUE BETWEEN -1 AND 25) NOT NULL,
    	minute INTEGER CHECK (VALUE BETWEEN -1 AND 61) NOT NULL,
    	duration INTEGER CHECK (VALUE BETWEEN -1 and 120) NOT NULL,
    	PRIMARY KEY (className, startDate, day),
	FOREIGN KEY (className) REFERENCES umidmuzrapov.Course
					ON UPDATE CASCADE,

	FOREIGN KEY (startDate) REFERENCES umidmuzrapov.Course
					ON UPDATE CASCADE

);

-- Equipment Table
CREATE TABLE umidmuzrapov.Equipment (
	itemNumber INTEGER NOT NULL,
    	description VARCHAR(120),
    	quantity INTEGER NOT NULL,
	PRIMARY KEY (itemNumber)
);

-- CoursePackage Table
CREATE TABLE umidmuzrapov.CoursePackage (
    	packageName VARCHAR(120) NOT NULL,
   	className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	PRIMARY KEY (packageName, className, startDate),
	FOREIGN KEY (packageName) REFERENCES umidmuzrapov.Package
	    				ON UDPATE CASCADE
					ON DELETE NO ACTION
	FOREIGN KEY (className) REFERENCES umidmuzrapov.Course
					ON UPDATE CASCADE
					ON DELETE NO ACTION
	FOREIGN KEY (startDate) REFERENCES umidmuzrapov.Course
					ON UPDATE CASCADE
					ON DELETE NO ACTION
	
);


-- Transaction Table
CREATE TABLE umidmuzrapov.Transaction (
    	transactionNumber INTEGER NOT NULL,
    	total DECIMAL(6,2) CHECK (VALUE BETWEEN 0 AND 9999.99) NOT NULL,
    	transactionDate DATE NOT NULL,
    	type VARCHAR(50),
    	memberNumber INTEGER NOT NULL,
	PRIMARY KEY (transactionNumber),
	FOREIGN KEY (memberNumber) REFERENCES umidmuzrapov.Course
					ON UDPATE CASCADE
					ON DELETE ON ACTION
);

-- Purchase Table
CREATE TABLE umidmuzrapov.Purchase (
    	packageName VARCHAR(255) NOT NULL,
    	transactionNumber INTEGER NOT NULL,
    	PRIMARY KEY (packageName, transactionNumber),
	FOREIGN KEY (packageName) REFERENCES umidmuzrapov.Package
	    				ON UDPATE CASCADE
					ON DELETE NO ACTIOM,
	FOREIGN KEY (transactionNumber) REFERENCES umidmuzrapov.Transaction
					ON UPDATE CASCADE
					ON DELETE NO ACTION
);

-- EquipmentLoan Table
CREATE TABLE umidmuzrapov.EquipmentLoan (
    	loanNumber INTEGER NOT NULL,
    	itemNumber INTEGER NOT NULL,
    	memberItem INTEGER NOT NULL,
    	quantity INTEGER CONTRAINT CHECK (VALUE >= 0),
    	borrowedDate DATE,
    	returnedDate DATE,
	PRIMARY KEY (loanNumber),
	FOREIGN KEY (item) REFERENCES umidmuzrapov.Equipment
				ON UPDATE CASCADE
				ON DELETE NO ACTION
	FOREIGN KEY (memberItem) REFERENCES umidmuzrapov.Member
				ON UPDATE CASCADE
				ON DELETE NO ACTION
);

-- Enrollment Table
CREATE TABLE umidmuzrapov.Enrollment (
    	memberNumber INTEGER NOT NULL,
    	courseName VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
     	PRIMARY KEY (memberNumber, courseName, startDate),
     	FOREIGN KEY (memberNumber) REFERENCES umidmuzrapov.Member
				ON UPDATE CASCADE
				ON DELETE NO ACTION,
	FOREIGN KEY (memberNumebr) REFERENCES umidmuzrapov.Course
				ON UPDATE CASCADE
);

-- Grant permissions to group members and TAs
GRANT ALL PRIVILEGES ON umidmuzrapov.Package TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Trainer TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Course TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Schedule TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Member TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Transaction TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Equipment TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.MembershipLevel TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.CoursePackage TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Purchase TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.EquipmentLoan TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
GRANT ALL PRIVILEGES ON umidmuzrapov.Enrollment TO marhoon, umidmuzrapov, yahyaalmalallah, danialb, qzydustin;
