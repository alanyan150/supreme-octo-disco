-- ============================================================
-- COMP-421 Project 3 - Index: IDX_FOLLOWS_FOLLOWING
-- Table:  Follows(follower, following)
-- NOT on a primary key or unique constraint.
-- ============================================================

-- Run: db2 -f index.sql (after connecting)

CREATE INDEX IDX_FOLLOWS_FOLLOWING ON Follows(following);

-- Verify
SELECT indname, tabname, colnames
FROM   syscat.indexes
WHERE  indschema = 'CS421G115' AND tabname = 'FOLLOWS';