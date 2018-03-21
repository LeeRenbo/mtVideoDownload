package com.leerenbo;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class MTActivityCorrect {

    public static void main(String[] args) throws IOException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        MTActivity mtActivity = applicationContext.getBean(MTActivity.class);


        allDirFile().stream().forEach(file -> {
                    Long id = Long.valueOf(file.getName().split("_")[0]);
                    mtActivity.downloadedActivityId.add(id);
                }
        );


        List<File> half = allDirFile().stream().filter(file -> file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals("info.json");
            }
        }).length == 0).collect(Collectors.toList());
        for (int i = 0; i < half.size(); i++) {
            File file = half.get(i);
            if (file == null) {
                continue;
            }
            System.out.println(file);
            Long id = Long.valueOf(file.getName().split("_")[0]);
            mtActivity.downloadedActivityId.remove(id);
        }



        for (long i = 33463l; i < 33740l; i++) {
            mtActivity.downloadI(i, new ArrayList<>());
        }

    }

    public static List<File> allDirFile() {
        List re = new ArrayList();
        File[] ogDirs = MTActivity.rootDirUrlResource.getFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        Arrays.stream(ogDirs).forEach(file1 -> re.addAll(dft(file1)));
        re.removeAll(Arrays.asList(ogDirs));
        return re;
    }

    public static List<File> dft(File file) {
        if (file.isFile()) {
            return Collections.emptyList();
        }
        List<File> re = new ArrayList<>();
        re.add(file);
        Arrays.stream(file.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)).forEach(file1 -> re.addAll(dft(file1)));
        return re;
    }

}
