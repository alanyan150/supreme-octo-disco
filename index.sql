-- IDX_FOLLOWS_FOLLOWING
-- run: db2 -f index.sql
--
-- why: PK covers (follower, following) so follower is already indexed
--      queries filtering on following ("who follows user X") do a full scan
--      this adds a seek on the following column -> much faster lookups
-- not on a PK or unique constraint

CREATE INDEX IDX_FOLLOWS_FOLLOWING ON Follows(following);

-- verify: db2 "SELECT indname, tabname, colnames FROM syscat.indexes WHERE indschema='CS421G115' AND tabname='FOLLOWS'"
