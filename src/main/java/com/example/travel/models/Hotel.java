package com.example.travel.models;

import com.example.travel.util.ImageConverter;
import jakarta.persistence.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

@Entity
@Table(name = "hotels", schema = "public")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hotel")
    private int idHotel;
    @ManyToOne
    @JoinColumn(name = "id_direction")
    private Direction direction;
    @Column(name = "hotel_address")
    private String hotelAddress;
    @Column(name = "hotel_name")
    private String hotelName;
    @Column(name = "hotel_rating")
    private double hotelRating;
    @Column(name = "hotel_photos", columnDefinition = "bytea[]")
    private byte[][] hotelPhotos;
    @Column(name = "count_stars")
    private short countStars;
    @Column(name = "count_ratings")
    private int countRatings;
    @Column(name = "days_from_application_to_check_in")
    private short daysFromApplicationToCheckIn;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "latitude")
    private Double latitude;

    @Transient
    private Image[] cachedImages;

    @Transient
    private final IntegerProperty currentImageIndex = new SimpleIntegerProperty(0);

    public Image getImageByNumber(int imageNumber) {
        if (hotelPhotos == null || imageNumber < 0 || imageNumber >= hotelPhotos.length) {
            return ImageConverter.getDefaultImage();
        }
        if (cachedImages == null) {
            cachedImages = new Image[hotelPhotos.length];
        }
        if (cachedImages[imageNumber] == null) {
            try {
                cachedImages[imageNumber] = ImageConverter.convertBytesToImage(hotelPhotos[imageNumber]);
            } catch (Exception e) {
                System.err.println("Ошибка загрузки изображения " + imageNumber + ": " + e.getMessage());
                cachedImages[imageNumber] = ImageConverter.getDefaultImage();
            }
        }
        return cachedImages[imageNumber];
    }

    public Hotel() {
    }

    public Hotel(int idHotel, Direction direction, String hotelAddress, String hotelName, double hotelRating, byte[][] hotelPhotos, short countStars, int countRatings, short daysFromApplicationToCheckIn, Double longitude, Double latitude) {
        this.idHotel = idHotel;
        this.direction = direction;
        this.hotelAddress = hotelAddress;
        this.hotelName = hotelName;
        this.hotelRating = hotelRating;
        this.hotelPhotos = hotelPhotos;
        this.countStars = countStars;
        this.countRatings = countRatings;
        this.daysFromApplicationToCheckIn = daysFromApplicationToCheckIn;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public double getHotelRating() {
        return hotelRating;
    }

    public void setHotelRating(double hotelRating) {
        this.hotelRating = hotelRating;
    }

    public byte[][] getHotelPhotos() {
        return hotelPhotos;
    }

    public void setHotelPhotos(byte[][] hotelPhotos) {
        this.hotelPhotos = hotelPhotos;
    }

    public short getCountStars() {
        return countStars;
    }

    public void setCountStars(short countStars) {
        this.countStars = countStars;
    }

    public int getCountRatings() {
        return countRatings;
    }

    public void setCountRatings(int countRatings) {
        this.countRatings = countRatings;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex.get();
    }

    public void setCurrentImageIndex(int index) {
        currentImageIndex.set(index);
    }

    public IntegerProperty currentImageIndexProperty() {
        return currentImageIndex;
    }

    public short getDaysFromApplicationToCheckIn() {
        return daysFromApplicationToCheckIn;
    }

    public void setDaysFromApplicationToCheckIn(short daysFromApplicationToCheckIn) {
        this.daysFromApplicationToCheckIn = daysFromApplicationToCheckIn;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
