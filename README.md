+------------------+       +---------------------+      +----------------+
|      User        |<------|       Booking        |----->| AvailableClass |
+------------------+       +---------------------+      +----------------+
| id (PK)          |       | id (PK)             |      | id (PK)        |
| username         |       | user_id (FK)        |      | name           |
| password         |       | class_id (FK)       |      | country_code   |
| email            |       | booking_time        |      | required_credits|
| email_verified   |       | is_active           |      | total_slots    |
| credits          |       | credits_used        |      | start_time     |
| created_at       |       | is_confirmed        |      | end_time       |
| updated_at       |       | class_start_time    |      | current_booked_slots |
+------------------+       | class_end_time      |      +----------------+
+---------------------+           |
^                           |
|                           |
+-------------------+       +------------------+
|      CheckIn      |<------|     Waitlist     |
+-------------------+       +------------------+
| id (PK)           |       | id (PK)          |
| user_id (FK)      |       | user_id (FK)     |
| class_id (FK)     |       | class_id (FK)    |
| check_in_time     |       | added_time       |
+-------------------+       +------------------+
^                               ^
|                               |
+---------------------------+
|
+------------------+
|     Package      |
+------------------+
| id (PK)          |
| name             |
| country_code     |
| credits          |
| valid_until      |
| expired          |
| user_id (FK)     |
+------------------+

