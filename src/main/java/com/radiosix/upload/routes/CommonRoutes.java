package com.radiosix.upload.routes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.radiosix.upload.SparkService.service;

public class CommonRoutes {


    private static final Logger LOG = LoggerFactory.getLogger(CommonRoutes.class);

    //********* Below are the static lambda Routes *********

    private static TemplateViewRoute showFileUploadRoute = (request, response) -> {

        Map<String, Object> responseMap = new HashMap<>();

        return new ModelAndView(responseMap, "file-upload.ftl");
    };

    private static TemplateViewRoute fileUploadRoute = (request, response) -> {

        Map<String, Object> responseMap = new HashMap<>();

        String location = "upload";
        long maxFileSize = 20000000;
        long maxRequestSize = 60100000;
        int fileSizeThreshold = 1024;

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                location, maxFileSize, maxRequestSize, fileSizeThreshold);
        request.attribute("org.eclipse.jetty.multipartConfig",
                multipartConfigElement);

        responseMap.put("msg", "File uploaded successfully !!!");
        try {
                Part uploadedFile = request.raw().getPart("fileToUpload");

                if (!StringUtils.isBlank(uploadedFile.getSubmittedFileName())) {
                    LOG.info("File Name: [{}]", uploadedFile.getSubmittedFileName());
                    Path out = Paths.get( Math.random() +"_"+ uploadedFile.getSubmittedFileName());
                    final InputStream in = uploadedFile.getInputStream();

                    Files.copy(in, out);
                    uploadedFile.delete();
                    //cleanup
                    uploadedFile = null;
                }
		} catch (IllegalStateException ise) {
            LOG.error(ise.toString());
        } finally {
            multipartConfigElement = null;
        }

        return new ModelAndView(responseMap, "file-upload.ftl");
    };


    public static void setUpRoutes(FreeMarkerEngine freeMarkerEngine) {

        service.get("/upload", showFileUploadRoute, freeMarkerEngine);

        service.post("/upload", "multipart/form-data", fileUploadRoute, freeMarkerEngine);
    }
}
