package com.example.apimodel;
import android.widget.Toast;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class FaceComparison {
    private FaceComparisonListener listener;
    public interface FaceComparisonListener {
        void onComparisonResult(double similarityPercentage);
        void onError(String errorMessage);
    }
    public FaceComparison(FaceComparisonListener listener) {
        this.listener = listener;
    }

    protected double faceComparison(String Base_64_2, String Base_64_1){
        String url = "https://api-us.faceplusplus.com/facepp/v3/compare";
        double app = 0;
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> paramMap = new HashMap<>();
        map.put("api_key", "L7JErrkANbkduQXNAgESRtln-HV5LrJX");
        map.put("api_secret", "9RvhwTXlfxAFLm2hb90hjHRooW9hH4p9");
        paramMap.put("image_base64_1", Base_64_1);
        paramMap.put("image_base64_2", Base_64_2);
        try {
            byte[] bacd = post(url, map, paramMap);
            String str=new String(bacd);
            JSONObject jsonObject=new JSONObject(str);
            if(jsonObject.has("confidence")){
                app=jsonObject.getDouble("confidence");
                System.out.println(app);
                listener.onComparisonResult(app);
            }else{
                listener.onError("please retake photo face not found");
            }
            System.out.println(str);
            return app;
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError("API response failed");
        }
        return  0.0;
    }
    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();

    protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, String> paramMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());

        Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }

        if (paramMap != null && paramMap.size() > 0) {
            Iterator<Map.Entry<String, String>> paramIter = paramMap.entrySet().iterator();
            while (paramIter.hasNext()) {
                Map.Entry<String, String> paramEntry = paramIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + paramEntry.getKey() + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.writeBytes(paramEntry.getValue() + "\r\n");
            }
        }

        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();

        InputStream inputStream = null;
        int code = conne.getResponseCode();
        try {
            if (code == 200) {
                inputStream=conne.getInputStream();
            } else {
                inputStream = conne.getErrorStream();
                System.out.println("everything is not okay");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        if (inputStream != null) {
            while ((len= inputStream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            inputStream.close();
        } else {
            System.out.println("null input stream");
        }
        String data=baos.toString();
        baos.close();
        return data.getBytes();
    }

    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-"
                    .charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    private static String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }
}
