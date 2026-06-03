package com.kuma.boot.idempotent.idempotentenhance.adapter.mysql;

import com.google.common.collect.Lists;
import com.kuma.boot.data.datasource.init.StandardDatabaseScript;

import java.util.List;

public class IdempotentSqlScripter extends StandardDatabaseScript {

	@Override
	public String getEvaluateTable() {
		return "undo_log";
	}

	@Override
	public String getComponentName() {
		return "idempotentenhance";
	}

	@Override
	public List<String> getInitSqlFile() {
		return Lists.newArrayList("idempotent.ddl.sql");
	}
}
