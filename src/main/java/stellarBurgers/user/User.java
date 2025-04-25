package stellarBurgers.user;

import com.github.javafaker.Faker;

public class User {

    private static final Faker faker = new Faker();

    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User() {

    }

    public static User random() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();
        return new User(email, password, name);

    }

    public static User randomWithoutPassword() {
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        return new User(email, null, name);

    }


    public User withUpdatedData(String newEmail, String newName) {
        return new User(newEmail, this.password, newName);
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
