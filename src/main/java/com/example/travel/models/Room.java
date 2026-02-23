package com.example.travel.models;

import com.example.travel.util.ImageConverter;
import jakarta.persistence.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.time.LocalTime;

@Entity
@Table(name = "rooms", schema = "public")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rooms")
    private int idRooms;
    @ManyToOne
    @JoinColumn(name = "id_hotel")
    private Hotel hotel;
    @Column(name = "room_name")
    private String roomName;
    @Column(name = "room_description")
    private String roomDescription;
    @Column(name = "room_sleeping_places")
    private short roomSleepingPlaces;
    @Column(name = "room_square")
    private double roomSquare;
    @ManyToOne
    @JoinColumn(name = "id_meal_plan")
    private MealPlan mealPlan;
    @Column(name = "check_in_time")
    private LocalTime checkInTime;
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
    @ManyToOne
    @JoinColumn(name = "id_refund_policy")
    private RefundPolicy refundPolicy;
    @ManyToOne
    @JoinColumn(name = "id_payment_method")
    private PaymentMethod paymentMethod;
    @Column(name = "room_photos")
    private byte[][] roomPhotos;
    @Column(name = "room_price")
    private double roomPrice;

    @Transient
    private Image[] cachedImages;

    @Transient
    private final IntegerProperty currentImageIndex = new SimpleIntegerProperty(0);

    public Image getImageByNumber(int imageNumber) {
        if (roomPhotos == null || imageNumber < 0 || imageNumber >= roomPhotos.length) {
            return ImageConverter.getDefaultImage();
        }
        if (cachedImages == null) {
            cachedImages = new Image[roomPhotos.length];
        }
        if (cachedImages[imageNumber] == null) {
            try {
                cachedImages[imageNumber] = ImageConverter.convertBytesToImage(roomPhotos[imageNumber]);
            } catch (Exception e) {
                System.err.println("Ошибка загрузки изображения " + imageNumber + ": " + e.getMessage());
                cachedImages[imageNumber] = ImageConverter.getDefaultImage();
            }
        }
        return cachedImages[imageNumber];
    }

    public Room() {}

    public Room(int idRooms, Hotel hotel, String roomName, String roomDescription, short roomSleepingPlaces, double roomSquare, MealPlan mealPlan, LocalTime checkInTime, LocalTime checkOutTime, RefundPolicy refundPolicy, PaymentMethod paymentMethod, byte[][] roomPhotos, double roomPrice) {
        this.idRooms = idRooms;
        this.hotel = hotel;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomSleepingPlaces = roomSleepingPlaces;
        this.roomSquare = roomSquare;
        this.mealPlan = mealPlan;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.refundPolicy = refundPolicy;
        this.paymentMethod = paymentMethod;
        this.roomPhotos = roomPhotos;
        this.roomPrice = roomPrice;
    }

    public int getIdRooms() {
        return idRooms;
    }

    public void setIdRooms(int idRooms) {
        this.idRooms = idRooms;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public short getRoomSleepingPlaces() {
        return roomSleepingPlaces;
    }

    public void setRoomSleepingPlaces(short roomSleepingPlaces) {
        this.roomSleepingPlaces = roomSleepingPlaces;
    }

    public double getRoomSquare() {
        return roomSquare;
    }

    public void setRoomSquare(double roomSquare) {
        this.roomSquare = roomSquare;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public RefundPolicy getRefundPolicy() {
        return refundPolicy;
    }

    public void setRefundPolicy(RefundPolicy refundPolicy) {
        this.refundPolicy = refundPolicy;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public byte[][] getRoomPhotos() {
        return roomPhotos;
    }

    public void setRoomPhotos(byte[][] roomPhotos) {
        this.roomPhotos = roomPhotos;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
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
}
