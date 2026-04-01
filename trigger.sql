-- ============================================================
-- COMP-421 Project 3 - Creativity Trigger: NO_SELF_LIKE
-- Prevents a user from liking their own post.
-- BEFORE INSERT on Likes, checks Posted table.
-- If the inserting user is the post author, raises SQLSTATE 75001.
-- ============================================================

-- Use @ as delimiter with: db2 -td@ -f trigger.sql

DROP TRIGGER NO_SELF_LIKE@

CREATE TRIGGER NO_SELF_LIKE
NO CASCADE BEFORE INSERT ON Likes
REFERENCING NEW AS N
FOR EACH ROW
WHEN ((SELECT COUNT(*) FROM Posted
       WHERE postid = N.postid AND posterid = N.userid) > 0)
SIGNAL SQLSTATE '75001'
    SET MESSAGE_TEXT = 'Users cannot like their own posts.'@