package com.oidea.oidea.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;

public class Util {

public static ImageAnnotatorClient getImmageClient() {
	try{
	ImageAnnotatorClient vision = ImageAnnotatorClient.create();
	return vision;
} catch(Exception e) {
e.printStackTrace();	
}
	return null;
}

public static String processImmageDetails(HashMap<String,byte[]> immages) throws Exception {
	ImageAnnotatorClient immageClient = getImmageClient();
	int count = 1;
	Iterator<Entry<String, byte[]>> iterator = immages.entrySet().iterator();
	List<AnnotateImageRequest> requests = new ArrayList<>();
	JsonArray details = new JsonArray();
	while(iterator.hasNext()) {
		Entry<String, byte[]> next = iterator.next();
		
    ByteString imgBytes = ByteString.copyFrom(next.getValue());
	
    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
        .addFeatures(feat)
        .setImage(img)
        .build();
    requests.add(request);
    
	}
    
    // Performs label detection on the image file
    BatchAnnotateImagesResponse response = immageClient.batchAnnotateImages(requests);
    List<AnnotateImageResponse> responses = response.getResponsesList();
//    StringBuilder txt = new StringBuilder();
    JsonObject obj = new JsonObject();
    for (AnnotateImageResponse res:responses) {
  	  if (res.hasError()) {
  		  obj.addProperty("error", "error in processing the request");
  		  return obj.toString();
  	  }
  	  
  	  String txt = "";
  	obj = new JsonObject();
    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
  	  txt+=annotation.getDescription()+" ";
    }
    obj.addProperty("immageNumber", count);
    obj.addProperty("description", txt);
    count++;
    details.add(obj);
    obj = null;
    }
return details.toString();
}




public static String getMultiImmageDetails(String filePath) throws Exception {
//	JsonArray data = new JsonArray();
//    ZipFile zipFile = new ZipFile("f://nagababu//immages.zip");
	ZipFile zipFile = new ZipFile(filePath);
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    System.out.println(entries);
//byte[] immages = new byte[1024*1024*1024*3];
HashMap<String,byte[]> mapData = new HashMap<String,byte[]>();
//int count = 0;
    while(entries.hasMoreElements()){
        ZipEntry entry = entries.nextElement();
        System.out.println(entry.getName());
            InputStream stream = zipFile.getInputStream(entry);
            byte[] buffer = new byte[(int)entry.getSize()];
			IOUtils.readFully(stream, buffer);
			mapData.put(entry.getName(),buffer);
//			immages[count] = buffer;
			buffer = null;
			 }
    String processImmageDetails = processImmageDetails(mapData);
    

	return processImmageDetails;
}


public static String getImmageDetails(byte[] data) throws IOException {
	ImageAnnotatorClient immageClient = getImmageClient();
    ByteString imgBytes = ByteString.copyFrom(data);
	List<AnnotateImageRequest> requests = new ArrayList<>();
    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
        .addFeatures(feat)
        .setImage(img)
        .build();
    requests.add(request);

    // Performs label detection on the image file
    BatchAnnotateImagesResponse response = immageClient.batchAnnotateImages(requests);
    List<AnnotateImageResponse> responses = response.getResponsesList();
    StringBuilder txt = new StringBuilder();
    for (AnnotateImageResponse res:responses) {
  	  if (res.hasError()) {
  		  return "error in processing the request";
  	  }
    for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
  	  txt.append(annotation.getDescription()+"\n");
    }
    }
return txt.toString();
}



public static String getImmageText(byte[] data) throws Exception {
	ImageAnnotatorClient immageClient = getImmageClient();
	JsonObject pages = new JsonObject();
    ByteString imgBytes = ByteString.copyFrom(data);
	List<AnnotateImageRequest> requests = new ArrayList<>();
    Image img = Image.newBuilder().setContent(imgBytes).build();
    Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
        .addFeatures(feat)
        .setImage(img)
        .build();
    requests.add(request);

    // Performs label detection on the image file
    BatchAnnotateImagesResponse response = immageClient.batchAnnotateImages(requests);
    List<AnnotateImageResponse> responses = response.getResponsesList();
    StringBuilder txt = new StringBuilder();
    for (AnnotateImageResponse res:responses) {
  	  if (res.hasError()) {
  		  pages.addProperty("error","Error in processing the request");
  		  break;
  	  }
  	  int pc = 1;
    for (Page page:res.getFullTextAnnotation().getPagesList()) {
    	pages.addProperty(String.valueOf(pc), page.toByteString().toStringUtf8());
    	pc++;
    }
    }
return pages.toString();
}



    public static void main(String... args) throws Exception {
    	String multiImmageDetails = getMultiImmageDetails("f://nagababu//immages.zip");
    	System.out.println(multiImmageDetails);
         }
}
