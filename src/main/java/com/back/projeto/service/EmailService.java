package  com.back.projeto.service;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarEmail(String email, String templateId, String userId, String userParams) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.emailjs.com/api/v1.0/email/send");

        String json = String.format("{\"service_id\":\"%s\",\"template_id\":\"%s\",\"user_id\":\"%s\",\"template_params\":%s}",
                                    "service_tj6nffn", templateId, userId, userParams);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        httpClient.close();
    }
}
