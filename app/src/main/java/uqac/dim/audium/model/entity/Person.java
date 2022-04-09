package uqac.dim.audium.model.entity;

import java.io.Serializable;
import java.util.Objects;

public abstract class Person implements Serializable {

    protected String firstName;
    protected String lastName;
    protected int age;

    /**
     * Construcs a new Person.
     * This constructor is used for deserializing from database.
     * Don't use this constructor!
     */
    protected Person() {
    }


    /**
     * Construcs a new Person with first name, last name and age.
     *
     * @param firstName First name of this person
     * @param lastName  Last name of this person
     * @param age       Age of this person
     */
    public Person(String firstName, String lastName, int age) {
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
    }

    /**
     * Returns the first name of this person.
     *
     * @return First name of this person
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of this person.
     *
     * @param firstName The new first name of this person
     */
    public void setFirstName(String firstName) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("firstName cannot be null or empty");
        }
    }

    /**
     * Returns the last name of this person.
     *
     * @return Last name of this person
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of this person.
     *
     * @param lastName The new last name of this person
     */
    public void setLastName(String lastName) {
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("lastName cannot be null or empty");
        }
    }

    /**
     * Returns the age of this person.
     *
     * @return Age of this person
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of this person.
     *
     * @param age The new age of this person
     */
    public void setAge(int age) {
        if (age > 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("age cannot be lower than 0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return this.age == person.age && Objects.equals(this.firstName, person.firstName) && Objects.equals(this.lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getAge());
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                '}';
    }
}
