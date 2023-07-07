package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.VehicleMediaRequest;
import com.boha.kasietransie.data.dto.VehiclePhoto;
import com.boha.kasietransie.data.dto.VehicleVideo;
import com.boha.kasietransie.data.repos.VehicleMediaRequestRepository;
import com.boha.kasietransie.data.repos.VehiclePhotoRepository;
import com.boha.kasietransie.data.repos.VehicleVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {
    final VehiclePhotoRepository vehiclePhotoRepository;
    final VehicleVideoRepository vehicleVideoRepository;

    final  VehicleMediaRequestRepository vehicleMediaRequestRepository;
    final MessagingService messagingService;

    public MediaService(VehiclePhotoRepository vehiclePhotoRepository,
                        VehicleVideoRepository vehicleVideoRepository,
                        VehicleMediaRequestRepository vehicleMediaRequestRepository, MessagingService messagingService) {
        this.vehiclePhotoRepository = vehiclePhotoRepository;
        this.vehicleVideoRepository = vehicleVideoRepository;
        this.vehicleMediaRequestRepository = vehicleMediaRequestRepository;
        this.messagingService = messagingService;
    }
    public VehiclePhoto addVehiclePhoto(VehiclePhoto vehiclePhoto) {
        return vehiclePhotoRepository.insert(vehiclePhoto);
    }
    public List<VehicleMediaRequest> getVehicleMediaRequests(String vehicleId) {
        return vehicleMediaRequestRepository.findByVehicleId(vehicleId);
    }

    public VehicleVideo addVehicleVideo(VehicleVideo vehicleVideo) {
        return vehicleVideoRepository.insert(vehicleVideo);
    }
    public List<VehiclePhoto> getVehiclePhotos(String vehicleId) {
        return vehiclePhotoRepository.findByVehicleId(vehicleId);
    }
    public List<VehicleVideo> getVehicleVideos(String vehicleId) {
        return vehicleVideoRepository.findByVehicleId(vehicleId);
    }
}
