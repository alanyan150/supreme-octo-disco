-- MARK_ACTIVE_USERS
-- run: db2 -td@ -f stored_procedure.sql
--
-- what it does:
--   loops over every user with a cursor
--   counts how many likes their posts got
--   marks them ACT if likes >= threshold, INA otherwise
--   only updates if status actually changed
--
-- IN  p_min_likes  - like threshold to be considered active
-- OUT p_updated    - how many users got updated

DROP PROCEDURE MARK_ACTIVE_USERS@

CREATE PROCEDURE MARK_ACTIVE_USERS (
    IN  p_min_likes  INT,
    OUT p_updated    INT
)
LANGUAGE SQL
BEGIN
    DECLARE v_userid      INT;
    DECLARE v_like_count  INT;
    DECLARE v_new_status  VARCHAR(3);
    DECLARE v_cur_status  VARCHAR(3);
    DECLARE v_not_found   INT DEFAULT 0;

    DECLARE c_users CURSOR FOR
        SELECT userid, status FROM Users;

    DECLARE CONTINUE HANDLER FOR NOT FOUND
        SET v_not_found = 1;

    SET p_updated = 0;
    OPEN c_users;

    user_loop: LOOP
        FETCH c_users INTO v_userid, v_cur_status;
        IF v_not_found = 1 THEN LEAVE user_loop; END IF;

        -- count total likes on this user's posts
        SELECT COUNT(*) INTO v_like_count
        FROM   Likes l
        JOIN   Posted pd ON l.postid = pd.postid
        WHERE  pd.posterid = v_userid;

        -- decide new status
        IF v_like_count >= p_min_likes THEN
            SET v_new_status = 'ACT';
        ELSE
            SET v_new_status = 'INA';
        END IF;

        -- only write if status changed
        IF v_cur_status IS NULL OR v_cur_status <> v_new_status THEN
            UPDATE Users SET status = v_new_status WHERE userid = v_userid;
            SET p_updated = p_updated + 1;
        END IF;

    END LOOP user_loop;

    CLOSE c_users;
END@
