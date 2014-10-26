package com.quietsageguice;

import com.mongodb.DB;

public interface ConnectionManager {
	public DB getConnection();
}
