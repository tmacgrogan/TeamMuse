SELECT ttt.TagId,
	ttt.Name
FROM Track t
JOIN TrackTag tt ON tt.TrackId = t.TrackId
JOIN Tag ttt ON ttt.TagId = tt.TagId
WHERE t.TrackId = ?;