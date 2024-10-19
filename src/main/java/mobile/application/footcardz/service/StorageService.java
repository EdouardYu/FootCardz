package mobile.application.footcardz.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Transactional
@AllArgsConstructor
@Service
public class StorageService {
    private final Path rootLocation = Paths.get("src/main/resources/static/images");

    public void storeImageWithSizeCheck(MultipartFile file, String folder, String fileName, int expectedWidth, int expectedHeight) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image.getWidth() != expectedWidth || image.getHeight() != expectedHeight)
                throw new IllegalArgumentException("Image size must be " + expectedWidth + "x" + expectedHeight + " pixels");

            Path destinationFile = this.rootLocation.resolve(Paths.get(folder)).resolve(Paths.get(fileName)).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + fileName, e);
        }
    }

    public void deleteImage(String folder, String fileName) {
        try {
            Path filePath = this.rootLocation.resolve(Paths.get(folder)).resolve(Paths.get(fileName)).normalize().toAbsolutePath();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }
}
