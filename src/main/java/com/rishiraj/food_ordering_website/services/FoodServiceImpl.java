package com.rishiraj.food_ordering_website.services;

import com.rishiraj.food_ordering_website.IO.FoodRequest;
import com.rishiraj.food_ordering_website.IO.FoodResponse;
import com.rishiraj.food_ordering_website.entity.FoodEntity;
import com.rishiraj.food_ordering_website.enums.FoodCategory;
import com.rishiraj.food_ordering_website.repository.FoodRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodServiceImpl.class);

    private final S3Client s3Client;
    private final FoodRepo foodRepo;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public FoodServiceImpl(S3Client s3Client, FoodRepo foodRepo) {
        this.s3Client = s3Client;
        this.foodRepo = foodRepo;
    }

    @Override
    public String uploadFile(MultipartFile file) {

        String fileExtension = getFileExtension(file);
        String key = UUID.randomUUID().toString() + "." + fileExtension;

        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl("public-read")
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            if(response.sdkHttpResponse().isSuccessful()){
                return "https://"+bucketName+".s3.amazonaws.com/"+key;
            }
            else{
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed.");
            }
        }
        catch (IOException e){
           log.error("Error while uploading files to S3");
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while uploading the files.");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest foodRequest, MultipartFile file) {

        FoodEntity foodEntity = foodRequestToFoodEntity(foodRequest);

        //uploading the file and getting the public URL of the file
        String publicFileUrl = uploadFile(file);

        //adding the public URL to the entity object.
        foodEntity.setImageUrl(publicFileUrl);

        //saving in database
        FoodEntity savedFoodEntity = foodRepo.save(foodEntity);

        //converting it to a suitable response object to send to front end.
       return foodEntityToFoodResponse(savedFoodEntity);

    }


    @Override
    public List<FoodResponse> getAllFoods(){

        List<FoodEntity> allFoods = foodRepo.findAll();

        return allFoods.stream().map(foodEntity ->
             foodEntityToFoodResponse(foodEntity)
        ).collect(Collectors.toList());
    }

    @Override
    public FoodResponse getFood(String id) {
        FoodEntity foodEntity = foodRepo.findById(id).orElseThrow(() -> new RuntimeException("Food with ID " + id + " not found"));
        return foodEntityToFoodResponse(foodEntity);
    }

    @Override
    public boolean deleteFile(String fileName) {
        //delete from s3
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public void deleteFood(String id) {
        FoodResponse food = getFood(id);
        String imageUrl = food.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/')+1);
        boolean isFileDeleted = deleteFile(fileName);
        if(isFileDeleted){
            foodRepo.deleteById(id);
        }
    }


    private String getFileExtension(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String extension = "";
        if(!fileName.isEmpty()){
           extension = fileName.substring(fileName.lastIndexOf('.')+1);
        }
        return fileName;
    }


    private FoodEntity foodRequestToFoodEntity(FoodRequest foodRequest){
        return FoodEntity.builder()
                .name(foodRequest.getName())
                .description(foodRequest.getDescription())
                .price(foodRequest.getPrice())
                .category(FoodCategory.valueOf(foodRequest.getCategory().toUpperCase()))
                .build();
    }

    private FoodResponse foodEntityToFoodResponse(FoodEntity foodEntity){
       return FoodResponse.builder()
               .id(foodEntity.getId())
               .name(foodEntity.getName())
               .description(foodEntity.getDescription())
               .category(foodEntity.getCategory().toString().toUpperCase())
               .price(foodEntity.getPrice())
               .imageUrl(foodEntity.getImageUrl())
               .build();
    }
}
