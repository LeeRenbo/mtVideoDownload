package com.leerenbo;

import com.leerenbo.util.DownloadUtil;
import com.leerenbo.util.FileUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

@Component
@EnableAsync
public class MTVideo implements ApplicationContextAware {
    private Logger logger = LogManager.getLogger(MTVideo.class);

    public MessageFormat messageFormat = new MessageFormat("http://s3.meituan.net/v1/mss_f0b5d15aa7ea4fa2a440fcb8f4bdcfff/mt_reich_vod_ba_1/{0,number,####}-{1,number,0000}.ts");

    public NumberFormat decimalFormat = new DecimalFormat("0000");
    RestTemplate restTemplate = new RestTemplate();

    FileSystemResource fileUrlResource = new FileSystemResource("d:/mt/video/");

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        MTVideo mtVideo = applicationContext.getBean(MTVideo.class);
//        mtVedio.downloadFT(5590, 6000);
        mtVideo.downloadI(6000);
    }

    public void downloadFT(int from, int to) {
        MTVideo mtVideo = applicationContext.getBean(MTVideo.class);
        for (int i = from; i < to; i++) {
            mtVideo.downloadI(i);
        }
    }

    public void downloadI(int i, FileSystemResource vedioFileResource) throws IOException {
        OutputStream o = FileUtil.getStream(vedioFileResource);
        for (int j = 0; j < 9999; j++) {
            byte[] data = null;
            data = download(i, j, restTemplate, 0);
            if (data != null) {
                try {
                    o.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                break;
            }
        }
        try {
            o.flush();
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Async
    public void downloadI(int i) {
        FileSystemResource vedioFileResource;
        vedioFileResource = (FileSystemResource) fileUrlResource.createRelative(decimalFormat.format(i) + ".ts");
        OutputStream o = null;
        try {
            o = vedioFileResource.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            vedioFileResource.getFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (int j = 0; j < 9999; j++) {
            byte[] data = null;
            data = download(i, j, restTemplate, 0);
            if (data != null) {
                try {
                    o.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                break;
            }
        }
        try {
            o.flush();
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] download(int i, int j, RestTemplate restTemplate, int time) {
        String downloadurl = messageFormat.format(new Object[]{i, j});
        System.out.println(downloadurl);
        try {
            return DownloadUtil.download(downloadurl, restTemplate);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
