package main.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeneratorUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateApiKey(){
        StringBuilder apiKey = new StringBuilder(32);

        for(int count= 0; count < 32; count++){
            int index = secureRandom.nextInt(CHARACTERS.length());
            apiKey.append(CHARACTERS.charAt(index));
        }
        return apiKey.toString();
    }
}
