package com.example.travel.models;

import com.example.travel.util.CryptoConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userID;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "user_email", columnDefinition = "bytea")
    private String userEmail;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "user_first_name", columnDefinition = "bytea")
    private String userFirstName;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "user_second_name", columnDefinition = "bytea")
    private String userSecondName;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "user_surname", columnDefinition = "bytea")
    private String userSurname;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "user_birthday", columnDefinition = "bytea")
    private String userBirthday;

    // Конструкторы
    public User() {}

    public User(String userEmail, String userFirstName, String userSecondName, String userSurname, String userBirthday) {
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userSecondName = userSecondName;
        this.userSurname = userSurname;
        this.userBirthday = userBirthday;
    }

    // Геттеры и сеттеры
    public long getUserID() { return userID; }
    public void setUserID(long userID) { this.userID = userID; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserSecondName() { return userSecondName; }
    public void setUserSecondName(String userSecondName) { this.userSecondName = userSecondName; }

    public String getUserSurname() { return userSurname; }
    public void setUserSurname(String userSurname) { this.userSurname = userSurname; }

    public String getUserBirthday() { return userBirthday; }
    public void setUserBirthday(String userBirthday) { this.userBirthday = userBirthday; }
}