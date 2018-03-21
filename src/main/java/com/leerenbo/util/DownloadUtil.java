package com.leerenbo.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class DownloadUtil {

    public static final Logger logger = LogManager.getLogger(DownloadUtil.class);

    public static byte[] download(String downloadurl, RestTemplate restTemplate) throws IOException {
        return download(downloadurl, restTemplate, 1);
    }

    public static byte[] download(String downloadurl, RestTemplate restTemplate, int time) throws IOException {
        if (time > 10) {
            throw new IOException("下载失败，重试超过10次:" + downloadurl);
        }

        ResponseEntity<byte[]> a = null;
        try {
            a = restTemplate.exchange(RequestEntity.get(URI.create(downloadurl)).build(), byte[].class);
        } catch (HttpClientErrorException e) {
            System.out.println("httpclient异常:" + downloadurl + "\r\n");
            return null;
        } catch (RestClientException re) {
            return download(downloadurl, restTemplate, ++time);
        }
        if (a.getStatusCode() == HttpStatus.OK) {
            return a.getBody();
        } else {
            logger.error("getStatusCode状态不是OK:" + downloadurl);
            return null;
        }
    }

    public static void main(String[] args) {
        URI.create("http://mtmos.com/v1/mss_e03d26da0ff348159b2ce3b06352b918/mit-storage/%5bMIT%5d%20浅入浅出%20Vue%20响应式原理.86527830-0be8-11e8-84c9-2bd7f1c917f1.pdf?temp_url_sig=9e69d2644e8d6db300a567beb747ac002bd22b1f&temp_url_expires=1608027968&inline=true");
    }
}
