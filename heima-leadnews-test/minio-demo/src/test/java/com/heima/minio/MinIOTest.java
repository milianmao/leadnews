package com.heima.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 14:13
 * @Version 1.0
 */
@SpringBootTest(classes = MinioApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {
    @Test
    public void test() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        FileInputStream fileInputStream = new FileInputStream("h:\\list.html");
        MinioClient client = MinioClient.builder()
                .credentials("minio", "minio123")
                .endpoint("http://1.117.157.193:9000")
                .build();
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .object("list.html")
                .bucket("weizhi")
                .contentType("text/html")
                .stream(fileInputStream, fileInputStream.available(), -1)
                .build();
        client.putObject(objectArgs);
    }

    @Test
    public void test1() {
        int sum = 0;
        int i = 1;
        while (i < 200) {
            if (i % 3 == 0) {
                sum += i;
            }
            i++;
        }
        System.out.println(sum);
    }
}
