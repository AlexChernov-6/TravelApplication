package com.example.travel.models;

import com.example.travel.util.ImageConverter;
import jakarta.persistence.*;
import javafx.scene.image.Image;

import java.util.Objects;

@Entity
@Table(name = "directions", schema = "public")
public class Direction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direction")
    private int idDirection;
    @Column(name = "city")
    private String city;
    @Column(name = "photo_city")
    private byte[] photoCity;

    @Transient
    private Image photoCityImage;

    public Image getPhotoCityImage() {
        if (photoCityImage == null && photoCity != null) {
            try {
                photoCityImage = ImageConverter.convertBytesToImage(photoCity);
            } catch (Exception e) {
                System.err.println( e.getMessage());
                photoCityImage = ImageConverter.getDefaultImage();
            }
        }
        return photoCityImage != null ? photoCityImage : ImageConverter.getDefaultImage();
    }

    public Direction() {}

    public Direction(int idDirection, String city, byte[] photoCity) {
        this.idDirection = idDirection;
        this.city = city;
        this.photoCity = photoCity;
    }

    public int getIdDirection() {
        return idDirection;
    }

    public void setIdDirection(int idDirection) {
        this.idDirection = idDirection;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public byte[] getPhotoCity() {
        return photoCity;
    }

    public void setPhotoCity(byte[] photoCity) {
        this.photoCity = photoCity;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Direction direction = (Direction) object;
        return idDirection == direction.idDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idDirection);
    }
}
