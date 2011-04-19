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
package net.paoding.rose.jade.provider;

import net.paoding.rose.jade.datasource.DataSourceFactory;

import javax.sql.DataSource;

/**
 * 基本的 {@link DataAccessProvider} 实现, 子类可以实现以下两个抽象方法提供定制的
 * {@link DataAccess} 与 {@link DataSourceFactory} 实现。
 * <p/>
 * <ul>
 * <li>
 * {@link AbstractDataAccessProvider#createDataAccess(javax.sql.DataSource)}
 * <li> {@link AbstractDataAccessProvider#createDataSourceFactory()}
 * </ul>
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public abstract class AbstractDataAccessProvider implements DataAccessProvider {

    protected DataSourceFactory dataSourceFactory;

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    public DataAccess createDataAccess(Class<?> daoClass) {

        if (dataSourceFactory == null) {
            dataSourceFactory = createDataSourceFactory();
        }

        DataSource dataSource = dataSourceFactory.getDataSource(daoClass);
        if (dataSource == null) {
            throw new NullPointerException("not found dataSource for DAO: '" + daoClass.getName()
                    + "'.");
        }

        return createDataAccess(dataSource);
    }

    /**
     * 重载方法创建自己的 {@link DataAccess} 实现。
     *
     * @param dataSource - 数据源
     * @return {@link DataAccess} 实现
     */
    protected abstract DataAccess createDataAccess(DataSource dataSource);

    /**
     * 重载方法创建自己的 {@link DataSourceFactory} 实现。
     *
     * @return {@link DataSourceFactory} 实现
     */
    protected abstract DataSourceFactory createDataSourceFactory();
}
