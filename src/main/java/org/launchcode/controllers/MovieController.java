package org.launchcode.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "movies")
public class MovieController {

    @RequestMapping(value="")
    public void mvdetails() {

        String movieTitle = "trancers";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final String url = "http://www.omdbapi.com/?apikey=6b4b81c2&t=" + movieTitle;
            final HttpGet get = new HttpGet(url);
            try (final CloseableHttpResponse response = httpClient.execute(get)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    System.out.println("Connection Error...");
                }
                String jsonResponse = EntityUtils.toString(response.getEntity());

                final Gson gson = new Gson();
                final Map<String, Object> json = gson.fromJson(jsonResponse, Map.class);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
