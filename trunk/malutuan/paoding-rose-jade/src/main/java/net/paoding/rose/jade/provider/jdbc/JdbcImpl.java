/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License i distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.rose.jade.provider.jdbc;

import net.paoding.rose.jade.core.Identity;
import net.paoding.rose.jade.provider.Modifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author 王泰 [tai.wang@opi-corp.com]
 * @author 王志亮 [qieqie.wang@gmail.com]
 */
public class JdbcImpl implements Jdbc {

    private final JdbcTemplate spring;

    public JdbcImpl(DataSource dataSource) {
        spring = new JdbcTemplate(dataSource);
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        spring.setDataSource(dataSource);
    }

    @Override
    public List<?> query(Modifier modifier, String sql, Object[] args, RowMapper rowMapper)
            throws DataAccessException {
        if (args != null && args.length > 0) {
            return spring.query(sql, args, rowMapper);
        } else {
            return spring.query(sql, rowMapper);
        }
    }

    @Override
    public int update(Modifier modifier, String sql, Object[] args) throws DataAccessException {
        if (args != null && args.length > 0) {
            return spring.update(sql, args);
        } else {
            return spring.update(sql);
        }
    }

    /**
     * 执行 INSERT 语句，并返回插入对象的 ID.
     *
     * @param sql  - 执行的语句
     * @param args - 参数
     * @return 插入对象的 ID
     */
    @SuppressWarnings("deprecation")
    @Override
    public Object insertAndReturnId(Modifier modifier, String sql, Object[] args) {
        Class<?> returnType = modifier.getReturnType();
        if (returnType == Identity.class) {
            returnType = Long.class;
        }
        ArgPreparedStatementSetter setter = null;
        if (args != null && args.length > 0) {
            setter = new ArgPreparedStatementSetter(args);
        }
        PreparedStatementCallbackReturnId callbackReturnId = new PreparedStatementCallbackReturnId(
                setter, returnType);
        Object keys = spring.execute(new GenerateKeysPreparedStatementCreator(sql),
                callbackReturnId);
        if (modifier.getReturnType() == Identity.class) {
            keys = new Identity((Long) keys);
        }
        return keys;
    }

    @Override
    public int[] batchUpdate(Modifier modifier, String sql, final List<Object[]> args)
            throws DataAccessException {
        return spring.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = args.get(i);
                for (int j = 0; j < values.length; j++) {
                    Object arg = values[j];
                    if (arg instanceof SqlParameterValue) {
                        SqlParameterValue paramValue = (SqlParameterValue) arg;
                        StatementCreatorUtils.setParameterValue(ps, j + 1, paramValue, paramValue
                                .getValue());
                    } else {
                        StatementCreatorUtils.setParameterValue(ps, j + 1,
                                SqlTypeValue.TYPE_UNKNOWN, arg);
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return args.size();
            }
        });
    }

    //-----------------------------------------------------------------------------

    // 创建 PreparedStatement 时指定 Statement.RETURN_GENERATED_KEYS 属性
    private static class GenerateKeysPreparedStatementCreator implements PreparedStatementCreator,
            SqlProvider {

        private final String sql;

        public GenerateKeysPreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
        }

        public String getSql() {
            return this.sql;
        }
    }

}
