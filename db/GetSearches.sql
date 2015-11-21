SELECT s.* FROM Search s WHERE 
	NOT EXISTS (SELECT ss.SearchId FROM Search ss WHERE ss.SubSearchId = s.SearchId);