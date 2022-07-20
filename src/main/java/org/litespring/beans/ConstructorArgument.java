package org.litespring.beans;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConstructorArgument {
    private List<ValueHolder> argumentValues = new LinkedList<ValueHolder>();

    /**
     * Create a new empty ConstructorArgumentValues object.
     */
    public ConstructorArgument() {
    }

    public void addArgumentValue(ValueHolder valueHolder) {
        this.argumentValues.add(valueHolder);
    }

    public void addArgumentValue(Object value) {
        this.argumentValues.add(new ValueHolder(value));
    }

    public List<ValueHolder> getArgumentValues() {
        return Collections.unmodifiableList(this.argumentValues);
    }

    public int getArgumentCount() {
        return this.argumentValues.size();
    }

    public boolean isEmpty() {
        return this.argumentValues.isEmpty();
    }

    /**
     * Clear this holder, removing all argument values.
     */
    public void clear() {
        this.argumentValues.clear();
    }

    public static class ValueHolder {
        private String name;
        private String type;
        private Object value;

        public ValueHolder(Object value) {
            this.value = value;
        }

        public ValueHolder(Object value, String type) {
            this.value = value;
            this.type = type;
        }


        public ValueHolder(Object value, String type, String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }


        public void setValue(Object value) {
            this.value = value;
        }


        public Object getValue() {
            return this.value;
        }


        public void setType(String type) {
            this.type = type;
        }


        public String getType() {
            return this.type;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getName() {
            return this.name;
        }
    }
}
