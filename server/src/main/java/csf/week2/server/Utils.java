package csf.week2.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bson.Document;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Utils {

    public static File[] unzip(MultipartFile zipFile) throws IOException{
        File tempDir = Files.createTempDirectory("temp").toFile();

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                String entryPath = tempDir.getAbsolutePath() + File.separator + entryName;

                // System.out.println(zipEntry.getName());
                // System.out.println(tempDir.getAbsolutePath() + File.separator + entryName);
    
                if (zipEntry.isDirectory()) {
                    new File(entryPath).mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(entryPath);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
    
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }    
                    fos.close();
                }    
                zipInputStream.closeEntry();
            }
        }
        return tempDir.listFiles();
    }


    public static JsonArray toJsonArray(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
            return jsonReader.readArray();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            JsonArray jsonArr = Json.createArrayBuilder().add("Object notmapped correctly").build();
            return jsonArr;
        }
    }

    public static JsonObject toJsonObject(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
            return jsonReader.readObject();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            JsonObject jsonObject = Json.createObjectBuilder().add("message", "Object not mapped correctly").build();
            return jsonObject;
        }
    }

    public static String toJsonStr(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            return jsonString;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject docToJsonObject(Document doc) {
        String jsonString = doc.toJson();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        return jsonReader.readObject();

    }

    public static JsonArray docToJsonArray(Document doc) {
        String jsonString = doc.toJson();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        return jsonReader.readArray();
    }

    public static JsonArray docToJsonArray(List<Document> docList) {

        String jsonString = docList.toString();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        return jsonReader.readArray();
    }

}
