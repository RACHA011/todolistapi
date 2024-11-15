package com.racha.todolistapi.util.apputils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

public class AppUtil {
    private static final String PATH = "src/main/resources/static/profile/";

    public static String getPhotoUploadPath(String fileName, String folderName, Long photoId) throws IOException {
        String path = PATH + photoId + "\\" + folderName;
        Files.createDirectories(Paths.get(path));
        return new File(path).getAbsolutePath() + "\\" + fileName;
    }

    public static BufferedImage getThumbnail(MultipartFile originalFile, Integer width) throws IOException {
        BufferedImage thumgImg = null;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());
        thumgImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, width, Scalr.OP_ANTIALIAS);

        return thumgImg;
    }

    public static Resource getFileResource(Long photoId, String folderName, String filename) throws IOException {
        String location = PATH + photoId + "/" + folderName + "/" + filename;
        Path path = Paths.get(location);
        if (Files.exists(path)) {
            return new UrlResource(path.toUri());
        }
        return null;
    }

}
