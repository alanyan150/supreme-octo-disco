-- NO_SELF_LIKE
-- run: db2 -td@ -f trigger.sql
--
-- what it does:
--   fires before every INSERT on Likes
--   checks if the user is liking their own post
--   if yes -> blocks the insert with SQLSTATE 75001

DROP TRIGGER NO_SELF_LIKE@

CREATE TRIGGER NO_SELF_LIKE
NO CASCADE BEFORE INSERT ON Likes
REFERENCING NEW AS N
FOR EACH ROW
WHEN ((SELECT COUNT(*) FROM Posted
       WHERE postid = N.postid AND posterid = N.userid) > 0)
SIGNAL SQLSTATE '75001'
    SET MESSAGE_TEXT = 'Users cannot like their own posts.'@
