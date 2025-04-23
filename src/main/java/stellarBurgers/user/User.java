package stellarBurgers.user;


import java.util.concurrent.ThreadLocalRandom;

public class User {

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

    // пользователь со всеми обязательными полями
    public static User random() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 100_000);
        return new User("user" + suffix + "@yandex.ru", "password123", "TestUser");
    }

    // пользователь без "пароля"
    public static User randomWithoutPassword() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 100_000);
        return new User("user" + suffix + "@yandex.ru", null, "TestUserTwo");
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
