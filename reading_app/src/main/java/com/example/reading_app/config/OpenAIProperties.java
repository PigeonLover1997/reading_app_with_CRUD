//やっぱり使わないクラス？
package com.example.reading_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
// openai. で始まる設定値を、Javaクラスのフィールドにまとめて読み込めるようにする
@ConfigurationProperties(prefix = "openai")
// プロパティ定義用のクラス（application.propertiesのワーニング解消用）
public class OpenAIProperties {
    private String apiUrl;
    private String apiKey;
    private String model;

    // Getter/Setter 
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
