package com.raja.springbootblobservice;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobAccessPolicy;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobSignedIdentifier;
import com.azure.storage.blob.models.PublicAccessType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@SpringBootApplication
public class SpringbootBlobserviceApplication {

    private static final String connectionString = "connection_string_details";
    private static final String containerName = "dev";  // Azure blob storage won't allow capital letters in container name
    private static final String blobName = "MYBLOB.jpg";
    private static final String localFilePath = "C:\\Users\\ryarraguntla\\Downloads\\Pavan.jpg";

    public static void main(String[] args) {

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();


        final BlobContainerClient devContainer = blobServiceClient.createBlobContainerIfNotExists(containerName);

        BlobSignedIdentifier accessPolicy = new BlobSignedIdentifier()
                .setId("policy1")  // Identifier for the policy
                .setAccessPolicy(new BlobAccessPolicy().setStartsOn(OffsetDateTime.now()).setExpiresOn(OffsetDateTime.now().plusDays(7)).setPermissions("r"));


        devContainer.setAccessPolicy(PublicAccessType.BLOB, List.of(accessPolicy));

        BlobClient blobClient = devContainer.getBlobClient(blobName);


        try (FileInputStream fileInputStream = new FileInputStream(localFilePath)) {
            blobClient.upload(fileInputStream, new File(localFilePath).length(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Download a blob to a local file
        try (FileOutputStream fileOutputStream = new FileOutputStream("downloaded_file.jpg")) {
            blobClient.download(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // List all blobs in the container
        for (BlobItem blobItem : devContainer.listBlobs()) {
            System.out.println("Blob Name: " + blobItem.getName());
        }

        // Delete the blob
        blobClient.delete();

        // Delete the container
        devContainer.deleteIfExists();
    }

}
