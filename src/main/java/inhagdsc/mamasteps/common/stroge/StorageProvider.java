package inhagdsc.mamasteps.common.stroge;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageProvider {
    public static final String PROFILE  = "profile";
    private final StorageProperties storageProperties;

    public String fileUpload(MultipartFile multipartFile, String folderName)  {
        log.info("fileUpload api 호출");
        Storage storage = StorageOptions.newBuilder().setProjectId(storageProperties.getProjectId()).build().getService();
        String uuid = UUID.randomUUID().toString();
        String ext = multipartFile.getContentType();

        // Cloud에 이미지 업로드
        String url;
        try {
            String blobName = String.format("%s/%s", folderName, uuid);
            Blob blob = storage.createFrom(

                    BlobInfo.newBuilder(storageProperties.getBucketName(), blobName)
                            .setContentType(ext)
                            .build(),
                    multipartFile.getInputStream()
            );
            url = String.format("https://storage.googleapis.com/%s/%s", blob.getBucket(), blob.getName());
            log.info("url : {}", url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public boolean deleteFile(String fileName) {
        log.info("deleteFile api 호출");
        Storage storage = StorageOptions.newBuilder().setProjectId(storageProperties.getProjectId()).build().getService();
        // 삭제할 Blob의 정보
        BlobId blobId = BlobId.of(storageProperties.getBucketName(), fileName);
        return storage.delete(blobId);


    }

}
