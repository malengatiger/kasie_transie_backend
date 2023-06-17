package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.*;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import util.E;

@RequiredArgsConstructor
@Service
public class MessagingService {

   private static final Logger LOGGER = LoggerFactory.getLogger(MessagingService.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static String MM = E.PANDA+E.PANDA+E.PANDA+" MessagingService " + E.RED_APPLE;
    public void sendMessage(VehicleArrival vehicleArrival) {
        try {
            String topic = "vehicleArrival_" + vehicleArrival.getAssociationId();
            Notification notification = Notification.builder()
                    .setBody("A vehicle has arrived at a landmark")
                    .setTitle("Vehicle Arrival")
                    .build();

            Message message = buildMessage("vehicleArrival", topic,
                    G.toJson(vehicleArrival), notification);
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "VehicleArrival message sent via FCM");
        } catch (Exception e) {
            LOGGER.error("Failed to send vehicleArrival FCM message");
            e.printStackTrace();
        }
    }
    public void sendMessage(VehicleDeparture vehicleDeparture) {
        try {
            String topic = "vehicleDeparture_" + vehicleDeparture.getAssociationId();
            Notification notification = Notification.builder()
                    .setBody("A vehicle has departed from landmark: " + vehicleDeparture.getLandmarkName())
                    .setTitle("Vehicle Departure")
                    .build();

            Message message = buildMessage("vehicleDeparture", topic,
                    G.toJson(vehicleDeparture), notification);
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "VehicleDeparture message sent via FCM");

        } catch (Exception e) {
            LOGGER.error("Failed to send vehicleDeparture FCM message");
            e.printStackTrace();
        }
    }
    public void sendMessage(LocationRequest locationRequest) {
        try {
            String topic = "locationRequest_" + locationRequest.getAssociationId();
            Notification notification = Notification.builder()
                    .setBody("A vehicle location has been requested ")
                    .setTitle("Vehicle Location Request")
                    .build();

            Message message = buildMessage("locationRequest", topic,
                    G.toJson(locationRequest), notification);
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "LocationRequest message sent via FCM");

        } catch (Exception e) {
            LOGGER.error("Failed to send locationRequest FCM message");
            e.printStackTrace();
        }
    }
    public void sendMessage(LocationResponse locationResponse) {
        try {
            String topic = "locationResponse_" + locationResponse.getAssociationId();
            Notification notification = Notification.builder()
                    .setBody("A vehicle location response has been sent to you ")
                    .setTitle("Vehicle Location Response")
                    .build();

            Message message = buildMessage("locationResponse", topic,
                    G.toJson(locationResponse), notification);
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "LocationResponse message sent via FCM");

        } catch (Exception e) {
            LOGGER.error("Failed to send locationResponse FCM message");
            e.printStackTrace();
        }
    }
    public void sendMessage(UserGeofenceEvent userGeofenceEvent) {
        try {
            String topic = "userGeofenceEvent_" + userGeofenceEvent.getAssociationId();
            Notification notification = Notification.builder()
                    .setBody("A user has entered or exited a landmark: " + userGeofenceEvent.getLandmarkName())
                    .setTitle("User Geofence Event")
                    .build();

            Message message = buildMessage("userGeofenceEvent", topic,
                    G.toJson(userGeofenceEvent), notification);
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "UserGeofenceEvent message sent via FCM");

        } catch (Exception e) {
            LOGGER.error("Failed to send userGeofenceEvent FCM message");
            e.printStackTrace();
        }
    }
//    public void sendMessage(VehicleHeartbeat vehicleHeartbeat) {
//        try {
//            String topic = "vehicleHeartbeat_" + vehicleHeartbeat.getAssociationId();
//
//            Message message = buildMessage("vehicleHeartbeat", topic,
//                    G.toJson(vehicleHeartbeat));
//            FirebaseMessaging.getInstance().send(message);
//
//        } catch (Exception e) {
//            LOGGER.error("Failed to send projectPosition FCM message");
//            e.printStackTrace();
//        }
//    }
    public void sendMessage(DispatchRecord dispatchRecord) {
        try {
            String topic = "dispatchRecord_" + dispatchRecord.getAssociationId();
            Message message = buildMessage("dispatchRecord", topic,
                    G.toJson(dispatchRecord));
            FirebaseMessaging.getInstance().send(message);
            LOGGER.info(MM + "DispatchRecord message sent via FCM");

        } catch (Exception e) {
            LOGGER.error("Failed to send dispatchRecord FCM message");
            e.printStackTrace();
        }
    }
    private Message buildMessage(String dataName, String topic, String payload) {
        return Message.builder()
                .putData(dataName, payload)
                .setFcmOptions(FcmOptions.builder()
                        .setAnalyticsLabel("KasieTransieFCM").build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setTopic(topic)
                .build();
    }

    private Message buildMessage(String dataName, String topic, String payload, Notification notification) {
        return Message.builder()
                .setNotification(notification)
                .putData(dataName, payload)
                .setFcmOptions(FcmOptions.builder()
                        .setAnalyticsLabel("KasieTransieFCM").build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .setTopic(topic)
                .build();
    }

}
