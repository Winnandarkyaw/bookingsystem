![ERD]/assets/img.png


========================


Database Schema

======================

AvailableClass

id (PK) - Long
name - String
countryCode - String
requiredCredits - Integer
totalSlots - Integer
startTime - LocalDateTime
endTime - LocalDateTime
currentBookedSlots - Integer
Booking

id (PK) - Long
userId (FK) - Long (references User.id)
classId (FK) - Long (references AvailableClass.id)
bookingTime - LocalDateTime
isActive - Boolean
creditsUsed - Integer
isConfirmed - Boolean
classStartTime - LocalDateTime
classEndTime - LocalDateTime
CheckIn

id (PK) - Long
userId (FK) - Long (references User.id)
classId (FK) - Long (references AvailableClass.id)
checkInTime - LocalDateTime
Package

id (PK) - Long
user_id (FK) - Long (references User.id)
name - String
countryCode - String
credits - Integer
validUntil - LocalDateTime
expired - Boolean
User

id (PK) - Long
username - String
password - String
email - String
emailVerified - Boolean
credits - Integer
createdAt - LocalDateTime
updatedAt - LocalDateTime
Waitlist

id (PK) - Long
userId (FK) - Long (references User.id)
classId (FK) - Long (references AvailableClass.id)
addedTime - LocalDateTime


Sample Data
===============================
AvailableClass
id	name	countryCode	requiredCredits	totalSlots	startTime	endTime	currentBookedSlots
1	Yoga Class	US	5	20	2024-11-15 09:00:00	2024-11-15 10:00:00	15
2	Pilates	UK	4	15	2024-11-16 11:00:00	2024-11-16 12:00:00	5

Booking
id	userId	classId	bookingTime	isActive	creditsUsed	isConfirmed	classStartTime	classEndTime
1	1	1	2024-11-14 08:30:00	true	5	true	2024-11-15 09:00:00	2024-11-15 10:00:00

CheckIn
id	userId	classId	checkInTime
1	1	1	2024-11-15 08:50:00

Package
id	name	user_id	countryCode	credits	validUntil	expired
1	Yoga Package	1	US	10	2024-12-31 00:00:00	false


User
id	username	password	email	emailVerified	credits	createdAt	updatedAt
1	johndoe	hashed_pw	john@example.com	true	50	2024-01-10 12:00:00	2024-11-01 12:00:00

Waitlist
id	userId	classId	addedTime
1	2	1	2024-11-14 09:00:00