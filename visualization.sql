-- post engagement export
-- run: db2 -f visualization.sql (while connected)
--
-- what it does:
--   counts likes and comments per post
--   exports to CSV for charting in Excel
--   ordered by total engagement descending

EXPORT TO result_engagement.csv OF DEL MODIFIED BY NOCHARDEL
SELECT p.postid,
       COALESCE(p.caption, '(no caption)')               AS caption,
       COUNT(DISTINCT l.userid)                           AS like_count,
       COUNT(DISTINCT c.commentid)                        AS comment_count,
       COUNT(DISTINCT l.userid) + COUNT(DISTINCT c.commentid) AS total_engagement
FROM   Posts p
LEFT JOIN Likes    l ON p.postid = l.postid
LEFT JOIN Comments c ON p.postid = c.postid
GROUP BY p.postid, p.caption
ORDER BY total_engagement DESC;
