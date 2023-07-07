package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.VehiclePhoto;
import com.boha.kasietransie.data.dto.VehicleVideo;
import com.boha.kasietransie.data.repos.VehiclePhotoRepository;
import com.boha.kasietransie.data.repos.VehicleVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {
    final VehiclePhotoRepository vehiclePhotoRepository;
    final VehicleVideoRepository vehicleVideoRepository;


    public MediaService(VehiclePhotoRepository vehiclePhotoRepository, VehicleVideoRepository vehicleVideoRepository) {
        this.vehiclePhotoRepository = vehiclePhotoRepository;
        this.vehicleVideoRepository = vehicleVideoRepository;
    }
    public VehiclePhoto addVehiclePhoto(VehiclePhoto vehiclePhoto) {
        return vehiclePhotoRepository.insert(vehiclePhoto);
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
