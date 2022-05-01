package com.subway_footprint_system.springboot_project.Controller;

import com.subway_footprint_system.springboot_project.Dao.Impl.ResultFactory;
import com.subway_footprint_system.springboot_project.Pojo.Result;
import com.subway_footprint_system.springboot_project.Utils.FtpUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableAutoConfiguration
@RestController
public class FileController {
    @Autowired
    private FtpUtil ftpUtil;
    /*
     * 请求方式：post
     * 功能：上传files
     * 路径 /file/uploadFiles
     * 传参(MultipartFile[]) files
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
    /*
     * 请求方式：post
     * 功能：上传图片(头像）
     * 路径 /file/uploadImage
     * 传参(MultipartFile) file
     * 返回值(json--Result) code,message,data(url)
     * */
    @CrossOrigin
    @PostMapping(value ="/file/uploadImage")
    @ResponseBody
    public Result uploadImage(@RequestParam("file") MultipartFile mfile) {
        List<File> filelist=new ArrayList<>();
        File file = new File(mfile.getOriginalFilename());
        try {
            FileUtils.copyInputStreamToFile(mfile.getInputStream(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        filelist.add(file);

        //上传得到原图url列表
        List<String> urlList= ftpUtil.ftpUpload(filelist);
        //资源释放
         file.delete();
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
    /*
     * 请求方式：post
     * 功能：上传图片(头像）
     * 路径 /file/uploadOrderSignImage
     * 传参(File) file
     * 返回值(json--Result) code,message,data(url)
     * */
    @CrossOrigin
    @RequestMapping(value = "/file/uploadOrderSignImage", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadOrderSignImage(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
        try {
            MultipartHttpServletRequest rq = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> file_list = rq.getFileMap();

            if (file_list != null && file_list.size() > 0) {
                if (file_list.containsKey("file")) {
                    MultipartFile file = file_list.get("file");
                    if (file != null) {
                        //上传得到原图url列表
                        List<File> filelist=new ArrayList<>();
                        File mfile = new File(file.getOriginalFilename());
                        try {
                            FileUtils.copyInputStreamToFile(file.getInputStream(), mfile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        filelist.add(mfile);
                        List<String> urlList= ftpUtil.ftpUpload(filelist);
                        //资源释放
                        mfile.delete();
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
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultFactory.buildFailResult("上传失败");
    }


    /*
     * 请求方式：get
     * 功能：获取原图(头像）
     * 路径 /file/getOriginalImage
     * 传参(String) url
     * 返回值(json--Result) code,message,data(url)
     * */
    @CrossOrigin
    @RequestMapping(value = "/file/getOriginalImage", method = RequestMethod.GET)
    public Result getOriginalImage( String url){
        String originalImagePath=null;
        try {
            originalImagePath=ftpUtil.getOriginalImage(url);
        }catch (Exception e){
            e.printStackTrace();
            return ResultFactory.buildFailResult("获取失败！");
        }
        return ResultFactory.buildSuccessResult(originalImagePath);
    }

}
