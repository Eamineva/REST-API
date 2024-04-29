import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.springframework.web.client.RestTemplate;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Consumer {
    public static void main(String[] args) throws Exception {

        RestTemplate restTemplate=new RestTemplate();
        String url = "http://94.198.50.185:7081/api/users";
        String response = restTemplate.getForObject(url, String.class);
        System.out.println(response);


        // Создание объекта CookieStore для хранения cookie
        CookieStore cookieStore = new BasicCookieStore();

        // Создание объекта HttpClient с использованием CookieStore
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response1 = httpClient.execute(httpGet);
        // Получение cookie из ответа
        String sessionId = cookieStore.getCookies()
                .stream()
                .filter(cookie -> cookie.getName().equals("JSESSIONID")) // Предполагается, что имя cookie - "sessionId"
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElse(null);



        // Проверка наличия sessionId
        if (sessionId != null) {
            // Ваша логика сохранения sessionId, например, в файл или в базе данных
            System.out.println("Session ID: " + sessionId);
            String newUserJson = "{\"id\": \"3\", \"name\": \"James\", \"lastName\":\"Brown\", \"age\": 42}";
            String updatedUserJson = "{\"id\": \"3\", \"name\": \"Thomas\", \"lastName\":\"Shelby\", \"age\": 42}";
            String deleteUserJson = "{\"id\": \"3\", \"name\": \"Thomas\", \"lastName\":\"Shelby\", \"age\": 42}";
            //String savedUserId = saveUser(httpClient, sessionId, url, newUserJson);
            saveUser(httpClient, sessionId, url, newUserJson);
            updateUser(httpClient, sessionId, url, updatedUserJson);
            deleteUser(httpClient, sessionId, url, deleteUserJson);

        } else {
            System.out.println("Session ID не найден в cookie.");

        }

        // Закрытие HttpClient
        httpClient.close();

        /*RestTemplate restTemplate=new RestTemplate();
        String url = "http://94.198.50.185:7081/api/users";
        String response = restTemplate.getForObject(url, String.class);
        System.out.println(response);*/
    }
    private static void saveUser(CloseableHttpClient httpClient, String sessionId, String url, String newUserJson) throws Exception {
        // Выполнение POST запроса для сохранения пользователя с новым ID
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Cookie", "sessionId=" + sessionId);
        httpPost.setHeader("Content-Type", "application/json");

        // Установка данных нового пользователя в теле запроса
        StringEntity entity = new StringEntity(newUserJson);
        httpPost.setEntity(entity);

        // Выполнение запроса и получение ответа
        HttpResponse response = httpClient.execute(httpPost);

        // Обработка ответа
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            String responseString = EntityUtils.toString(responseEntity);
            System.out.print("Response: " + responseString);
        }
    }

    private static void updateUser(CloseableHttpClient httpClient, String sessionId, String url, String updatedUserJson) throws Exception {
        // Выполнение PUT запроса для обновления пользователя
        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Cookie", "sessionId=" + sessionId);
        httpPut.setHeader("Content-Type", "application/json");

        // Установка данных обновленного пользователя в теле запроса
        StringEntity entity = new StringEntity(updatedUserJson);
        httpPut.setEntity(entity);

        // Выполнение запроса и получение ответа
        HttpResponse response = httpClient.execute(httpPut);

        // Обработка ответа
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            String responseString = EntityUtils.toString(responseEntity);
            System.out.print(responseString);
        }
    }

    private static void deleteUser(CloseableHttpClient httpClient, String sessionId, String url, String deleteUserJson) throws Exception {
        // Выполнение PUT запроса для обновления пользователя
        HttpDelete httpDelete = new HttpDelete(url+"/3");
        httpDelete.setHeader("Cookie", "sessionId=" + sessionId);
        httpDelete.setHeader("Content-Type", "application/json");

        // Установка данных обновленного пользователя в теле запроса
        StringEntity entity = new StringEntity(deleteUserJson);
        //httpDelete.

        // Выполнение запроса и получение ответа
        HttpResponse response = httpClient.execute(httpDelete);

        // Обработка ответа
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            String responseString = EntityUtils.toString(responseEntity);
            System.out.print(responseString);
        }
    }

}
