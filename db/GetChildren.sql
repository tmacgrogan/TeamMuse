SELECT t.* 
FROM Tag t
JOIN ParentTagLink p ON p.ChildTagId = t.TagId
WHERE p.ParentTagId = ?;