package net.paoding.rose.jade.core;

import net.paoding.rose.jade.provider.Modifier;

import java.util.Map;

/**
 * 定义一组数据库操作。
 *
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author han.liao
 */
public interface JadeOperation {

    /**
     * @return
     */
    public Modifier getModifier();

    /**
     * 执行所需的数据库操作。
     *
     * @return
     */
    public Object execute(Map<String, Object> parameters);
}
