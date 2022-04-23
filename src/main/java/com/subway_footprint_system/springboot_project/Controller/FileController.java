package com.subway_footprint_system.springboot_project.Controller;

import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Utils.FtpUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration
@RestController
public class FileController {
    @Autowired
    private FtpUtil ftpUtil;
    /*
     * 上传files
     * 路径 /file/uploadFiles
     * 传参(MultipartFile) files
     * 返回值(json--Result) code,message,data(List<String> urlList)
     * */
    @CrossOrigin
    @PostMapping(value ="/file/uploadFiles")
    @ResponseBody
    public Result uploadFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<File> filelist=new ArrayList<>();
        for (MultipartFile mfile :files) {
            File file = new File(mfile.getOriginalFilename());
            FileUtils.copyInputStreamToFile(mfile.getInputStream(), file);
            filelist.add(file);
        }

        //上传得到原图url列表
        List<String> urlList= ftpUtil.ftpUpload(filelist);
        //资源释放
        for (File file:filelist) {
            if(file.exists()){
                file.delete();
            }
        }
        if (urlList!=null){
            List<String> filenames=new ArrayList<>();
            for (String url :urlList) {
                String filename=url.substring(url.lastIndexOf("/")+1);
                filenames.add(filename);
            }
            //对图片进行压缩，得到略缩图并返回
            urlList=ftpUtil.ftpCompress(ftpUtil.DEFAULT_SCALE,filenames);
            if(null!=urlList){
                return ResultFactory.buildSuccessResult(urlList);
            }
        }

        return ResultFactory.buildFailResult("上传失败");
    }
}
