package com.oidea.oidea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.oidea.oidea.util.Util;

@SpringBootApplication
@RestController
public class OideaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OideaApplication.class, args);
	}

	@RequestMapping(value = "/hello")
	public String sayHello() throws Exception {
	      /*String fileName = "c://cat.jpeg";

          Path path = Paths.get(fileName);
	      List<AnnotateImageResponse> immageDetails = Util.getImmageDetails(path);
	      for (AnnotateImageResponse res:immageDetails) {
	    	  if (!res.hasError()) {
	    		  break;
	    	  }
	      }*/

		return "txt";
	}
	

	   @RequestMapping(value = "/upload", method = RequestMethod.POST, 
	      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	   
	   public String fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("choice") String choice) throws Exception {
		   String ImmageDetails = "";
		   if (choice != null && choice.equalsIgnoreCase("singleimg")) {
		   byte[] bytes = file.getBytes();
	      ImmageDetails = Util.getImmageDetails(bytes);
		   } else if (choice != null && choice.equalsIgnoreCase("multiimg")) {
			   File convertFile = new File("f://nagababu//"+file.getOriginalFilename());
			      convertFile.createNewFile();
			      String path = convertFile.getAbsolutePath();
			   ImmageDetails = Util.getMultiImmageDetails(path);
			   convertFile.delete();
		   } else if (choice != null && choice.equalsIgnoreCase("OCR")) {
			   byte[] bytes = file.getBytes();
			   ImmageDetails = Util.getImmageText(bytes);
		   }
	      return ImmageDetails;
	   }

	
}
