package com.example.board.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.AttachedImage;


public class ImageService {
    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 업로드 된 파일을 지정된 경로에 저장하고, 저장된 파일명을 리턴
     * @param mImage 업로드 된 파일
     * @param path 저장한 경로
     * @return 저장된 파일명
     */
    public AttachedImage saveImage(MultipartFile mImage) {
        // 업로드 된 파일이 없거나 크기가 0이면 저장하지 않고 null 을 리턴
        if (mImage == null || mImage.isEmpty() || mImage.getSize() == 0) {
            return null;
        }

        // 저장 폴더가 없으면 생성
        File path = new File(uploadPath);
        if (!path.isDirectory()) {
            path.mkdirs();
        }

        // 원본 파일명
        String originalFilename = mImage.getOriginalFilename();

        // 저장할 파일명을 오늘 날짜의 년월일로 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String savedFilename = sdf.format(new Date());

        // 원본 파일의 확장자
        String ext;
        int lastIndex = originalFilename.lastIndexOf(".");

        // 확장자가 없는 경우
        if (lastIndex == -1) {
            ext = "";
        }

        // 확장자가 있는 경우
        else {
            ext = "." + originalFilename.substring(lastIndex + 1);
        }

        // 저장할 전체 경로를 포함한 File 객체
        File serverFile = null;

        // 같은 이름의 파일이 있는 경우의 처리
        while (true) {
            serverFile = new File(uploadPath + "/" + savedFilename + ext);
            // 같은 이름의 파일이 없으면 나감
            if (!serverFile.isFile()) break;
            // 같은 이름의 파일이 있으면 이름 뒤에 long 타입의 시간 정보를 덧붙임.
            savedFilename += new Date().getTime();
        }

        // 파일 저장
        try {
            mImage.transferTo(serverFile);
        } catch (Exception e) {
            savedFilename = null;
            e.printStackTrace();
        }

        return new AttachedImage(originalFilename, savedFilename + ext, mImage.getSize());
    }

    /**
     * 서버에 저장된 파일의 전체 경로를 전달받아, 해당 파일을 삭제
     * @param fullPath 삭제할 파일의 경로
     * @return 삭제 여부
     */
    public boolean deleteImage(String fullpath) {
        // 파일 삭제 여부를 리턴할 변수
        boolean result = false;

        // 전달된 전체 경로로 File 객체 생성
        File delImage = new File(fullpath);

        // 해당 파일이 존재하면 삭제
        if (delImage.isFile()) {
            delImage.delete();
            return true;
        }

        return result;
    }
}
