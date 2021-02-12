package org.dreamcat.jwrap.mybatis.entity;

import java.util.function.IntSupplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Create by tuke on 2020/7/17
 */
@Getter
@Setter
@ToString(callSuper = true)
public class SimpleEntity extends BaseEntity {
    private Type type;
    private String content;

    @Getter
    public enum Type implements IntSupplier {
        COMMON(1, "common");

        private final int value;
        private final String description;

        Type(int value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public int getAsInt() {
            return value;
        }
    }
}
