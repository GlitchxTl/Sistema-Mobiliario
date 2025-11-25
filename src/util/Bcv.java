package util;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Bcv {

    private final String urlBcv;

    public Bcv() {
        this.urlBcv = "https://www.tcambio.app/";
        if (this.urlBcv == null) {
            throw new IllegalArgumentException("La variable de entorno URL_BCV no está configurada.");
        }
    }

    public double getRate() throws IOException {
        return this.getRateInternal();
    }

    private String connect() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .hostnameVerifier((hostname, session) -> true)
            .build();
        
        Request request = new Request.Builder()
            .url(this.urlBcv)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Petición HTTP fallida: " + response);
            }
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private double getRateInternal() throws IOException {
        String htmlText = this.connect();
        return this.findDolarRate(htmlText);
    }

    private double findDolarRate(String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        
        Element dolarElement = doc.select(".text-4xl.md\\:text-7xl.font-bold").last();
        
        if (dolarElement == null) {
            throw new RuntimeException("No se encontró el elemento del dólar.");
        }
        
        Element dolarAmountElement = dolarElement.select("strong").last();

        if (dolarAmountElement == null) {
            throw new RuntimeException("No se encontró el elemento strong dentro del elemento del dólar.");
        }
        
        String rateText = dolarAmountElement.text();
        
        rateText = rateText.replace(",", ".").trim();

        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(rateText);
        
        if (matcher.find()) {
            double rate = Double.parseDouble(matcher.group());
            return Math.round(rate * 100.0) / 100.0;
        } else {
            throw new RuntimeException("No se pudo extraer el valor numérico de la tasa.");
        }
    }

    
}