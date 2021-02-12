package org.dreamcat.jwrap.jpa.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.x.asm.BeanCopierUtil;
import org.dreamcat.common.x.asm.MakeClass;

/**
 * Create by tuke on 2020/5/30
 */
@RequiredArgsConstructor
public class DynamicJpaEntity<E> {

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final Map<String, E> instances = new ConcurrentHashMap<>();

    private final Class<E> entityClass;
    private final Class<?> annotationClass;
    private final String annotationMemberName;
    private final UnaryOperator<String> tableNameSupplier;

    public DynamicJpaEntity(
            Class<E> entityClass, Class<?> annotationClass,
            UnaryOperator<String> tableNameSupplier) {
        this(entityClass, annotationClass, "value", tableNameSupplier);
    }

    public E newInstance(String id) {
        E instance = instances.computeIfAbsent(id, key -> {
            Class<? extends E> clazz = newClass(id);
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "cannot invoke <init>() of class " + clazz.getCanonicalName());
            }
        });
        return BeanCopierUtil.copy(instance);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends E> newClass(String id) {
        return (Class<? extends E>) classes.computeIfAbsent(id, it -> {
            String entityClassName = this.entityClass.getName();
            String className = String.format("%s$%s", entityClassName, it);
            String tableName = tableNameSupplier.apply(it);
            try {
                return MakeClass.make(className)
                        .superClass(this.entityClass)
                        .annotation(annotationClass, annotationMemberName, tableName)
                        .toClass();
            } catch (Exception e) {
                throw new RuntimeException("cannot create class " + className);
            }
        });
    }

}
