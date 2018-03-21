package com.leerenbo;

import com.leerenbo.model.Activity;
import com.leerenbo.util.DownloadUtil;
import com.leerenbo.util.FileUtil;
import com.leerenbo.util.JsonSnakeTool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MTActivity {
    private Logger logger = LogManager.getLogger(MTActivity.class);

    private RestTemplate restTemplate = new RestTemplate();

    public static Set<Long> downloadedActivityId = ConcurrentHashMap.newKeySet();

    public static FileSystemResource rootDirUrlResource = new FileSystemResource("d:/mt/activity/");

    @Resource
    MTVideo mtVideo;


    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        MTActivity mtActivity = applicationContext.getBean(MTActivity.class);
    }

    @Async
    public void downloadI(Long i, List<String> filePath) {
        System.out.println("activity=" + i);
        try {
            if (downloadedActivityId.add(i)) {
                RequestEntity requestHttpEntity = RequestEntity
                        .get(URI.create("https://api-mit.sankuai.com/activity/" + i + "?_include%5B0%5D=lecturer&_include%5B1%5D=owner&_include%5B2%5D=guest&_include%5B3%5D=tags&_include%5B4%5D=city&_include%5B5%5D=student_daxiang_group&_include%5B6%5D=appointment&_include%5B7%5D=organization&_include%5B8%5D%5Bassociation%5D=videos&_include%5B8%5D%5Binclude%5D%5B0%5D=owner&_include%5B9%5D=children"))
                        .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ODcxMTM5Miwic3NvX2lkIjoyMDY4Mjg4LCJsb2dpbiI6ImxpcmVuYm8iLCJuYW1lIjoi5p2O5LuB5Y2aIiwiZXhwaXJlc19hdCI6MTUyMjg0OTg0NiwiaWF0IjoxNTIxNjQwMjQ2fQ.PSrhWipO1IeMewIKtFbKddK4_rc4cvLUGk8eEGE_E-I")
                        .build();
                try {

                    ResponseEntity<String> responseEntity = null;
                    for (int j = 0; j < 10; j++) {
                        try {
                            responseEntity = restTemplate.exchange(requestHttpEntity, String.class);
                            break;
                        } catch (Exception e) {
                            if (j == 9) {
                                logger.error("activity=" + i + " 信息获取异常:" + ExceptionUtils.getStackTrace(e));
                            }
                            return;
                        }
                    }

                    String responseString = responseEntity.getBody();
                    Activity activity = JsonSnakeTool.parse(responseString, Activity.class);
                    if (activity == null) {
                        logger.error("activity=" + i + " 信息获取异常:" + responseString);
                        return;
                    }

                    FileSystemResource dirUrlResource;
                    if (activity.getOrganization() != null) {
                        dirUrlResource = (FileSystemResource) rootDirUrlResource.createRelative(activity.getOrganization().toFilePath());
                    } else {
                        dirUrlResource = (FileSystemResource) rootDirUrlResource.createRelative("其他/");
                    }

                    if (CollectionUtils.isNotEmpty(filePath)) {
                        dirUrlResource = (FileSystemResource) dirUrlResource.createRelative(filePath.stream().collect(Collectors.joining()));
                    }
                    dirUrlResource = (FileSystemResource) dirUrlResource.createRelative(activity.toFilePath());


                    if (StringUtils.isNotBlank(activity.getSlide()) && activity.getSlide().startsWith("http")) {
                        try (OutputStream outputStream = FileUtil.getStream((FileSystemResource) dirUrlResource.createRelative(activity.toPDFFileName()))) {
                            try {
                                byte[] pdfBytes = DownloadUtil.download(activity.getSlide().replace("[", "%5b").replace("]", "%5d"), restTemplate);
                                if (pdfBytes == null) {
                                    logger.error("activity=" + i + " PDF文件下载异常:");
                                }
                                outputStream.write(pdfBytes);
                            } catch (IOException e) {
                                logger.error("activity=" + i + " PDF文件下载异常:" + ExceptionUtils.getStackTrace(e));
                            }

                        } catch (IOException e) {
                            logger.error("activity=" + i + " PDF文件创建异常:" + ExceptionUtils.getStackTrace(e));
                        }
                    }

                    if (CollectionUtils.isNotEmpty(activity.getChildren())) {
                        filePath = new ArrayList<>(filePath);
                        filePath.add(activity.toFilePath());
                        for (int j = 0; j < activity.getChildren().size(); j++) {
                            downloadI(activity.getChildren().get(j).getId(), filePath);
                        }
                    }

                    if (CollectionUtils.isNotEmpty(activity.getVideos())) {
                        for (int j = 0; j < activity.getVideos().size(); j++) {
                            try {
                                mtVideo.downloadI(activity.getVideos().get(j).getReichVodid(), (FileSystemResource) dirUrlResource.createRelative(activity.getVideos().get(j).toFileName()));
                            } catch (IOException e) {
                                logger.error("activity=" + i + " videoVodId=" + activity.getVideos().get(j).getReichVodid() + "下载异常:" + ExceptionUtils.getStackTrace(e));
                            }
                        }

                    }

                    if (dirUrlResource.getFile().exists()) {
                        try (OutputStream o = FileUtil.getStream((FileSystemResource) dirUrlResource.createRelative("info.json"))) {
                            IOUtils.write(responseString, o);
                        } catch (IOException e) {
                            logger.error("activity=" + i + " json文件创建异常:" + ExceptionUtils.getStackTrace(e));
                        }
                    }

                } catch (HttpClientErrorException e) {
                    logger.error("activity=" + i + " 信息不存在");
                }
            }
        } catch (Exception e) {
            logger.error("activity=" + i + " 下载异常" + ExceptionUtils.getStackTrace(e));
        }
    }
}