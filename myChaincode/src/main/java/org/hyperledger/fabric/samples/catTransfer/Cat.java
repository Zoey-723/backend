/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.catTransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public final class Cat {

    @Property()
    private final String name;

    @Property()
    private final Integer age;

    @Property()
    private final String color;

    @Property()
    private final String breed;


    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getColor() {
        return color;
    }

    public String getBreed() {
        return breed;
    }

    public Cat(@JsonProperty("name")final String name, @JsonProperty("age")final Integer age, @JsonProperty("color")final String color, @JsonProperty("breed")final String breed) {
        this.name = name;
        this.age = age;
        this.color = color;
        this.breed = breed;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Cat other = (Cat) obj;

        return Objects.deepEquals(
                new String[] {getName(), getColor(), getBreed()},
                new String[] {other.getName(), other.getColor(), other.getBreed()})
                &&
                Objects.deepEquals(
                        new int[] {getAge()},
                        new int[] {other.getAge()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAge(), getColor(), getBreed());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode())
                + " [name=" + name + ", age=" + age + ", color=" + color + ", breed=" + breed + "]";
    }
}
