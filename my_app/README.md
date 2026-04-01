# Supreme Octo Disco - JDBC Social App (Project 3)

## Overview
This application implements the project3 requirement: a Java JDBC menu-driven app with 5 actions + quit.

## Files
- `src/Main.java`: CLI menu logic with loop, options, submenu, error handling.
- `src/Connector.java`: JDBC operations for Users, Follows, Posts, Groups, PrivateMessages.
- `sql/schema.sql`: full relational schema for the social network.
- `sql/stored_procedure.sql`: DB2 stored procedure `mark_inactive_users( cutoff_date )` with cursor loop.
- `sql/indexing.sql`: index scripts for optimization.
- `sql/visualization.sql`: data export queries for charting.
- `project3-report.md`: project report outline with required sections.

## DB2 setup
1. Use `winter2026-comp421` DB2 server as required.
2. Configure your database user (example in code uses CS421 account in `Connector.java` by default):
   - `your_userid = cs421g115`
   - `your_password = dblovers21` (replace with your credentials or env vars `SOCSUSER`/`SOCSPASSWD`).
3. Apply schema:
   - `db2 -tvf sql/schema.sql`
4. Apply indexes:
   - `db2 -tvf sql/indexing.sql`
5. Apply stored procedure:
   - `db2 -tvf sql/stored_procedure.sql`

## Running the App
```
cd X:\Downloads\GitHub\supreme-octo-disco\my_app
mkdir out
javac -d out src\*.java
java -cp out;C:\path\to\db2jcc4.jar Main
```

> Note: the compile step can fail if DB2 driver classes are not on classpath (e.g., `com.ibm.db2.jcc.DB2Driver`). Add the correct JDBC driver jar to the classpath.

## Menu Actions (for demo)
1. Lookup user
2. Follow user
3. Create a post (inserts `Posts` + `Posted`)
4. Join group
5. Read private messages (submenu of conversation selection)
6. Quit

## Validation tests
- Login (username, e.g. `test`)
- Option 1: lookup human profile
- Option 2: follow existing user
- Option 3: create post and check inserted records
- Option 4: join group
- Option 5: read conversation between users
- Option 6: exit and verify connection closes

## Deliverables checklist
- [x] Relational schema included
- [x] Stored procedure with cursor + loop + parameter
- [x] Application program (5 tasks + submenu + error handling)
- [x] Index script (non-pk multi-column indexes)
- [x] Visualization export script
- [ ] Screenshot and PDF reporting (done manually outside code)
