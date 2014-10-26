package com.quietsage.db;

import com.mongodb.DB;

public interface ConnectionManager {
	public DB getConnection();
	public String getDatabaseUri();
}
