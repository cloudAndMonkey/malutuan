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

import net.paoding.rose.jade.core.GenericUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 提供 Definition 包装对 DAO 的定义。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class Definition {

    private final Class<?> clazz;

    private final Map<String, ?> constants;

    public Definition(Class<?> clazz) {
        this.clazz = clazz;
        this.constants = Collections.unmodifiableMap( // NL
                GenericUtils.getConstantFrom(clazz, true, true));
    }

    public String getName() {
        return clazz.getName();
    }

    public Map<String, ?> getConstants() {
        return constants;
    }

    public Class<?> getDAOClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Definition) {
            Definition definition = (Definition) obj;
            return clazz.equals(definition.clazz);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clazz.hashCode() * 13;
    }

    @Override
    public String toString() {
        return clazz.getName();
    }
}
