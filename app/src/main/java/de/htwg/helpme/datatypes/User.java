package de.htwg.helpme.datatypes;

public class User {
    String token;
    String name;
    String phone;
    String image;

    public User(String token, String name, String phone, String image) {
        this.token = token;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }



    @Override
    public String toString() {
        return "token : " + token + " - name: " + name + " - phone: " + phone;
    }
}
