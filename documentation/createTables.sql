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
    cost DECIMAL(6,2) NOT NULL,
    CONSTRAINT PackageCourse CHECK (cost BETWEEN 0 AND 9999.99),
    PRIMARY KEY (packageName)
);


-- Membership Level Table
CREATE TABLE umidmuzrapov.MembershipLevel (
    	levelId INTEGER NOT NULL,
    	minSpending DECIMAL(6,2) NOT NULL,
		CONSTRAINT PossibleCost CHECK (minSpending BETWEEN 0 AND 9999.99),
    	discount INTEGER NOT NULL,
		CONSTRAINT Discount CHECK (discount BETWEEN 0 AND 50),
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
    FOREIGN KEY (levelId) REFERENCES umidmuzrapov.MembershipLevel ON DELETE SET NULL
);


-- Course Table
CREATE TABLE umidmuzrapov.Course (
		className VARCHAR(120) NOT NULL,
		maxParticipant INTEGER,
		CONSTRAINT ParticipantMax CHECK (maxParticipant BETWEEN 0 and 60),
    	currentParticipant INTEGER, 
			CONSTRAINT CurrentParticipant
			CHECK (currentParticipant BETWEEN 0 AND maxParticipant),
    	startDate DATE NOT NULL,
    	endDate DATE NOT NULL,
    	trainerNumber INTEGER NOT NULL,
    	PRIMARY KEY (className, startDate),
		FOREIGN KEY (trainerNumber) REFERENCES umidmuzrapov.Trainer
);


-- Schedule Table
CREATE TABLE umidmuzrapov.Schedule (
	className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	day INTEGER NOT NULL,
		CONSTRAINT Day CHECK (day BETWEEN 0 and 8) ,
    	hour INTEGER NOT NULL,
		CONSTRAINT Hour CHECK (hour BETWEEN -1 AND 25) ,
    	minute INTEGER NOT NULL,
		CONSTRAINT Minute CHECK (minute BETWEEN -1 AND 61) ,
    	duration INTEGER NOT NULL,
		CONSTRAINT Duration CHECK (duration BETWEEN -1 and 120) ,
    	PRIMARY KEY (className, startDate, day),
		FOREIGN KEY (className, startDate) REFERENCES umidmuzrapov.Course
);

-- Equipment Table
CREATE TABLE umidmuzrapov.Equipment (
		itemNumber INTEGER NOT NULL,
    	descriptionComment VARCHAR(120),
    	quantity INTEGER NOT NULL,
		PRIMARY KEY (itemNumber)
);

-- CoursePackage Table
CREATE TABLE umidmuzrapov.CoursePackage (
    	packageName VARCHAR(120) NOT NULL,
   		className VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
    	PRIMARY KEY (packageName, className, startDate),
		FOREIGN KEY (packageName) REFERENCES umidmuzrapov.Package,
		FOREIGN KEY (className, startDate) REFERENCES umidmuzrapov.Course
);


-- Transaction Table
CREATE TABLE umidmuzrapov.Transaction (
    	transactionNumber INTEGER NOT NULL,
    	total DECIMAL(6,2) NOT NULL,
		CONSTRAINT Cost CHECK (total BETWEEN 0 AND 9999.99),
    	transactionDate DATE NOT NULL,
    	type VARCHAR(50),
    	memberNumber INTEGER NOT NULL,
		PRIMARY KEY (transactionNumber),
		FOREIGN KEY (memberNumber) REFERENCES umidmuzrapov.Member
);

-- Purchase Table
CREATE TABLE umidmuzrapov.Purchase (
    packageName VARCHAR(120) NOT NULL,
    transactionNumber INTEGER NOT NULL,
    PRIMARY KEY (packageName, transactionNumber),
    FOREIGN KEY (packageName) REFERENCES umidmuzrapov.Package(packageName),
    FOREIGN KEY (transactionNumber) REFERENCES umidmuzrapov.Transaction(transactionNumber)
);

-- EquipmentLoan Table
CREATE TABLE umidmuzrapov.EquipmentLoan (
    	loanNumber INTEGER NOT NULL,
    	itemNumber INTEGER NOT NULL,
    	memberItem INTEGER NOT NULL,
    	quantity INTEGER,
		CONSTRAINT Quantity CHECK (quantity > 0 OR quantity=0),
    	borrowedDate DATE,
    	returnedDate DATE,
		PRIMARY KEY (loanNumber),
		FOREIGN KEY (itemNumber) REFERENCES umidmuzrapov.Equipment,
		FOREIGN KEY (memberItem) REFERENCES umidmuzrapov.Member
);

-- Enrollment Table
CREATE TABLE umidmuzrapov.Enrollment (
    	memberNumber INTEGER NOT NULL,
    	courseName VARCHAR(120) NOT NULL,
    	startDate DATE NOT NULL,
     	PRIMARY KEY (memberNumber, courseName, startDate),
     	FOREIGN KEY (memberNumber) REFERENCES umidmuzrapov.Member,
		FOREIGN KEY (courseName, startDate) REFERENCES umidmuzrapov.Course
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
