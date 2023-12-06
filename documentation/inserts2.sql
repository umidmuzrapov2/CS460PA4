-- Inserting data into MembershipLevel table
INSERT INTO umidmuzrapov.MembershipLevel (levelId, minSpending, discount) VALUES (1, 0, 0);
INSERT INTO umidmuzrapov.MembershipLevel (levelId, minSpending, discount) VALUES (2, 500, 10);
INSERT INTO umidmuzrapov.MembershipLevel (levelId, minSpending, discount) VALUES (3, 1000, 20);


-- Inserting data into Package table
INSERT INTO umidmuzrapov.Package (packageName, cost) VALUES ('Junior', 400.00);
INSERT INTO umidmuzrapov.Package (packageName, cost) VALUES ('Senior', 700.00);
INSERT INTO umidmuzrapov.Package (packageName, cost) VALUES ('Strength', 1000.00);


-- Inserting data into Member table
INSERT INTO umidmuzrapov.Member (memberNumber, fname, lname, phoneNumber, levelId) VALUES (1, 'Alice', 'Johnson', '111111111', 3);
INSERT INTO umidmuzrapov.Member (memberNumber, fname, lname, phoneNumber, levelId) VALUES (2, 'Bob', 'Williams', '222222222', 1);
INSERT INTO umidmuzrapov.Member (memberNumber, fname, lname, phoneNumber, levelId) VALUES (3, 'Charlie', 'Brown', '333333333', 1);
INSERT INTO umidmuzrapov.Member (memberNumber, fname, lname, phoneNumber, levelId) VALUES (4, 'David', 'Smith', '444444444', 3);
INSERT INTO umidmuzrapov.Member (memberNumber, fname, lname, phoneNumber, levelId) VALUES (5, 'Emma', 'Jones', '555555555', 1);


-- Inserting data into Trainer table
INSERT INTO umidmuzrapov.Trainer (trainerNumber, fname, lname, phoneNumber) VALUES (1, 'Jack', 'Anderson', '1010101010');
INSERT INTO umidmuzrapov.Trainer (trainerNumber, fname, lname, phoneNumber) VALUES (2, 'Lily', 'Thompson', '1212121212');


-- Inserting data into Course table
INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength001', 60, 3, TO_DATE('2023-11-01', 'YYYY-MM-DD'), TO_DATE('2023-11-14', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength002', 60, 1, TO_DATE('2023-11-15', 'YYYY-MM-DD'), TO_DATE('2023-11-30', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength001', 40, 2, TO_DATE('2023-12-01', 'YYYY-MM-DD'), TO_DATE('2023-12-14', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength002', 40, 1, TO_DATE('2023-12-15', 'YYYY-MM-DD'), TO_DATE('2023-12-31', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength001', 40, 0, TO_DATE('2024-01-01', 'YYYY-MM-DD'), TO_DATE('2024-01-14', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Strength002', 40, 0, TO_DATE('2024-01-15', 'YYYY-MM-DD'), TO_DATE('2024-01-31', 'YYYY-MM-DD'), 1);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga001', 40, 3, TO_DATE('2023-11-01', 'YYYY-MM-DD'), TO_DATE('2023-11-14', 'YYYY-MM-DD'), 2);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga002', 40, 1, TO_DATE('2023-11-15', 'YYYY-MM-DD'), TO_DATE('2023-11-30', 'YYYY-MM-DD'), 2);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga001', 20, 1, TO_DATE('2023-12-01', 'YYYY-MM-DD'), TO_DATE('2023-12-14', 'YYYY-MM-DD'), 2);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga002', 20, 0, TO_DATE('2023-12-15', 'YYYY-MM-DD'), TO_DATE('2023-12-31', 'YYYY-MM-DD'), 2);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga001', 20, 0, TO_DATE('2024-01-01', 'YYYY-MM-DD'), TO_DATE('2024-01-14', 'YYYY-MM-DD'), 2);

INSERT INTO umidmuzrapov.Course (className, maxParticipant, currentParticipant, startDate, endDate, trainerNumber)
VALUES ('Yoga002', 20, 0, TO_DATE('2024-01-15', 'YYYY-MM-DD'), TO_DATE('2024-01-31', 'YYYY-MM-DD'), 2);


-- Inserting data into CoursePackage table
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Junior', 'Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Junior', 'Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Junior', 'Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Junior', 'Yoga001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Senior', 'Strength002', TO_DATE('2023-11-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Senior', 'Yoga002', TO_DATE('2023-11-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Senior', 'Strength002', TO_DATE('2023-12-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Senior', 'Yoga002', TO_DATE('2023-12-15', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Strength', 'Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Strength', 'Strength002', TO_DATE('2023-11-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Strength', 'Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.CoursePackage (packageName, className, startDate) VALUES ('Strength', 'Strength002', TO_DATE('2023-12-15', 'YYYY-MM-DD'));


-- Inserting data into Schedule table
INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'), 1, 15, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'), 3, 15, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'), 2, 15, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'), 4, 15, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength002', TO_DATE('2023-11-15', 'YYYY-MM-DD'), 2, 15, 0, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength002', TO_DATE('2023-11-15', 'YYYY-MM-DD'), 4, 15, 0, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength002', TO_DATE('2023-12-15', 'YYYY-MM-DD'), 1, 15, 0, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Strength002', TO_DATE('2023-12-15', 'YYYY-MM-DD'), 3, 15, 0, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'), 1, 16, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'), 3, 16, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga001', TO_DATE('2023-12-01', 'YYYY-MM-DD'), 2, 16, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga001', TO_DATE('2023-12-01', 'YYYY-MM-DD'), 4, 16, 0, 45);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga002', TO_DATE('2023-11-15', 'YYYY-MM-DD'), 2, 16, 30, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga002', TO_DATE('2023-11-15', 'YYYY-MM-DD'), 4, 16, 30, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga002', TO_DATE('2023-12-15', 'YYYY-MM-DD'), 1, 16, 30, 60);

INSERT INTO umidmuzrapov.Schedule (className, startDate, day, hour, minute, duration)
VALUES ('Yoga002', TO_DATE('2023-12-15', 'YYYY-MM-DD'), 3, 16, 30, 60);


-- Inserting data into Transaction table
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (1, 400.00, TO_DATE('2023-10-17', 'YYYY-MM-DD'), 'charge', 1);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (2, 400.00, TO_DATE('2023-10-18', 'YYYY-MM-DD'), 'deposit', 1);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (3, 400.00, TO_DATE('2023-10-19', 'YYYY-MM-DD'), 'charge', 2);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (4, 400.00, TO_DATE('2023-10-30', 'YYYY-MM-DD'), 'deposit', 2);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (5, 400.00, TO_DATE('2023-10-30', 'YYYY-MM-DD'), 'charge', 3);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (6, 700.00, TO_DATE('2023-10-30', 'YYYY-MM-DD'), 'charge', 1);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (7, 400.00, TO_DATE('2023-11-13', 'YYYY-MM-DD'), 'charge', 5);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (8, 700.00, TO_DATE('2023-11-14', 'YYYY-MM-DD'), 'deposit', 1);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (9, 400.00, TO_DATE('2023-11-26', 'YYYY-MM-DD'), 'deposit', 3);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (10, 1000.00, TO_DATE('2023-11-28', 'YYYY-MM-DD'), 'charge', 4);
INSERT INTO umidmuzrapov.Transaction (transactionNumber, total, transactionDate, type, memberNumber) VALUES (11, 1000.00, TO_DATE('2023-12-08', 'YYYY-MM-DD'), 'deposit', 4);

-- Inserting data into Purchase table
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Junior', 1);
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Senior', 6);
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Junior', 3);
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Junior', 5);
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Strength', 10);
INSERT INTO umidmuzrapov.Purchase (packageName, transactionNumber) VALUES ('Junior', 7);



-- Inserting data into Enrollment table
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (1, 'Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (1, 'Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (1, 'Strength002', TO_DATE('2023-11-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (1, 'Yoga002', TO_DATE('2023-11-15', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (2, 'Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (2, 'Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (3, 'Strength001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (3, 'Yoga001', TO_DATE('2023-11-01', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (4, 'Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (4, 'Strength002', TO_DATE('2023-12-15', 'YYYY-MM-DD'));

INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (5, 'Strength001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.Enrollment (memberNumber, courseName, startDate) VALUES (5, 'Yoga001', TO_DATE('2023-12-01', 'YYYY-MM-DD'));




-- Inserting data into Equipment table
INSERT INTO umidmuzrapov.Equipment (itemNumber, descriptionComment, quantity) VALUES (1, 'Dumbbells', 20);
INSERT INTO umidmuzrapov.Equipment (itemNumber, descriptionComment, quantity) VALUES (2, 'Yoga Mats', 50);
INSERT INTO umidmuzrapov.Equipment (itemNumber, descriptionComment, quantity) VALUES (3, 'volleyball', 4);
INSERT INTO umidmuzrapov.Equipment (itemNumber, descriptionComment, quantity) VALUES (4, 'basketball', 5);
INSERT INTO umidmuzrapov.Equipment (itemNumber, descriptionComment, quantity) VALUES (5, 'football', 2);



-- Inserting more data into EquipmentLoan table
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (2, 2, 2, 3, TO_DATE('2023-11-11', 'YYYY-MM-DD'), TO_DATE('2023-11-06', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (3, 2, 1, 2, TO_DATE('2023-11-10', 'YYYY-MM-DD'), TO_DATE('2023-11-20', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (4, 3, 4, 1, TO_DATE('2023-11-15', 'YYYY-MM-DD'), TO_DATE('2023-11-25', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (5, 4, 5, 2, TO_DATE('2023-11-20', 'YYYY-MM-DD'), TO_DATE('2023-11-30', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (6, 1, 4, 3, TO_DATE('2023-12-01', 'YYYY-MM-DD'),  TO_DATE('2023-12-11', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (7, 2, 1, 2, TO_DATE('2023-12-05', 'YYYY-MM-DD'), TO_DATE('2023-12-15', 'YYYY-MM-DD'));
INSERT INTO umidmuzrapov.EquipmentLoan (loanNumber, itemNumber, memberItem, quantity, borrowedDate, returnedDate) VALUES (8, 3, 2, 1, TO_DATE('2023-12-10', 'YYYY-MM-DD'),  TO_DATE('2023-12-20', 'YYYY-MM-DD'));