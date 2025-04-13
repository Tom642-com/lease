package com.itheima.lease.web.admin.service.impl;

import com.itheima.lease.common.minio.MinioProperties;
import com.itheima.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 检查存储桶是否存在，如果不存在则创建
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(minioProperties.getBucketName()).config(createBucketPolicyConfig(minioProperties.getBucketName())).build());
        }

        // 生成文件名
        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 上传文件到MinIO
        minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.getBucketName()).stream(file.getInputStream(), file.getSize(), -1).object(filename).contentType(file.getContentType()).build());

        return String.join("/", minioProperties.getEndpoint(), minioProperties.getBucketName(), filename); // 返回上传文件的路径
    }

    /**
     * 创建存储桶策略配置
     *
     * @param bucketName 存储桶名称
     * @return 存储桶策略配置字符串
     */
    private String createBucketPolicyConfig(String bucketName) {
        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }
}
