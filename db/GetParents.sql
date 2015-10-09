SELECT t.* 
FROM Tag t
JOIN ParentTagLink p ON p.ParentTagId = t.TagId
WHERE p.ChildTagId = ?;