package com.kuma.cloud.uaa.mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import javax.sql.DataSource;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.delegatesTo;

class UaaUserMapperTest {
    @Test
    void usernameLookupReadsMfaAndLockStateFromDatabaseColumns() throws Exception {
        var rows = RowSetProvider.newFactory().createCachedRowSet();
        var metadata = new RowSetMetaDataImpl();
        String[] columns = {"id", "username", "status", "locked", "mfa_enabled", "mfa_secret"};
        metadata.setColumnCount(columns.length);
        for (int i = 0; i < columns.length; i++) {
            metadata.setColumnName(i + 1, columns[i]);
            metadata.setColumnLabel(i + 1, columns[i]);
            metadata.setColumnType(i + 1, i == 1 || i == 5 ? Types.VARCHAR : Types.INTEGER);
        }
        rows.setMetaData(metadata);
        rows.moveToInsertRow();
        rows.updateInt(1, 1); rows.updateString(2, "alice"); rows.updateInt(3, 1);
        rows.updateInt(4, 1); rows.updateInt(5, 1); rows.updateString(6, "test-secret");
        rows.insertRow(); rows.moveToCurrentRow(); rows.beforeFirst();

        var source = mock(DataSource.class);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        when(source.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.execute()).thenReturn(true);
        var resultSet = mock(ResultSet.class, delegatesTo(rows));
        doReturn(false).when(resultSet).isClosed();
        when(statement.getResultSet()).thenReturn(resultSet);
        when(statement.getUpdateCount()).thenReturn(-1);
        var configuration = new Configuration(new Environment("test", new JdbcTransactionFactory(), source));
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(UaaUserMapper.class);
        try (var session = new SqlSessionFactoryBuilder().build(configuration).openSession()) {
            var user = session.getMapper(UaaUserMapper.class).selectByUsername("alice");
            assertThat(user.getUsername()).isEqualTo("alice");
            assertThat(user.isMfaEnabled()).isTrue();
            assertThat(user.isLocked()).isTrue();
            assertThat(user.getMfaSecret()).isEqualTo("test-secret");
            verify(statement).setString(1, "alice");
        }
    }
}
