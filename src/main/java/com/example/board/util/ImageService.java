package com.example.board.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.AttachedImage;

@Service
public class ImageService {
	public List<AttachedImage> uploadImages(MultipartFile[] files, String uploadPath) throws IOException {
        List<AttachedImage> images = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File destination = new File(uploadPath + filename);
            file.transferTo(destination);

            AttachedImage image = new AttachedImage();
            image.setFileName(filename);
            image.setFilePath(uploadPath + filename);

            images.add(image);
        }

        return images;
    }
}
